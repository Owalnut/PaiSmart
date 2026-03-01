package com.yizhaoqi.smartpai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 嵌入向量生成客户端
@Component
public class EmbeddingClient {

    @Value("${embedding.api.model}")
    private String modelId;
    
    @Value("${embedding.api.batch-size:10}")
    private int batchSize;  // text-embedding-v4 单次最多 10 条

    @Value("${embedding.api.dimension:2048}")
    private int dimension;
    
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingClient.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public EmbeddingClient(WebClient embeddingWebClient, ObjectMapper objectMapper) {
        this.webClient = embeddingWebClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 调用通义千问 API 生成向量
     * @param texts 输入文本列表
     * @return 对应的向量列表
     */
    public List<float[]> embed(List<String> texts) {
        try {
            // 空文本用占位符，避免 API 400，保持与 chunks 的 1:1 映射
            List<String> safeTexts = texts.stream()
                    .map(t -> (t == null || t.isBlank()) ? " " : t)
                    .toList();
            if (safeTexts.isEmpty()) {
                throw new IllegalArgumentException("输入文本列表为空");
            }
            logger.info("开始生成向量，文本数量: {}", safeTexts.size());

            List<float[]> all = new ArrayList<>(safeTexts.size());
            for (int start = 0; start < safeTexts.size(); start += batchSize) {
                int end = Math.min(start + batchSize, safeTexts.size());
                List<String> sub = safeTexts.subList(start, end);
                logger.debug("调用向量 API, 批次: {}-{} (size={})", start, end - 1, sub.size());
                String response = callApiOnce(sub);
                all.addAll(parseVectors(response));
            }
            logger.info("成功生成向量，总数量: {}", all.size());
            return all;
        } catch (Exception e) {
            WebClientResponseException wce = null;
            if (e instanceof WebClientResponseException) {
                wce = (WebClientResponseException) e;
            } else if (e.getCause() instanceof WebClientResponseException) {
                wce = (WebClientResponseException) e.getCause();
            }
            if (wce != null) {
                logger.error("DashScope API 错误 status={} 响应体: {}",
                        wce.getStatusCode(), wce.getResponseBodyAsString());
            }
            logger.error("调用向量化 API 失败: {}", e.getMessage(), e);
            throw new RuntimeException("向量生成失败", e);
        }
    }

    private String callApiOnce(List<String> batch) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelId);
        requestBody.put("input", batch);
        requestBody.put("dimensions", dimension);  // DashScope 要求 dimensions（复数）
        requestBody.put("encoding_format", "float");

        return webClient.post()
                .uri("/embeddings")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(e -> {
                            if (e instanceof WebClientResponseException ex) {
                                return ex.getStatusCode().is5xxServerError();
                            }
                            return true;  // 网络等错误可重试
                        }))
                .block(Duration.ofSeconds(30));
    }

    private List<float[]> parseVectors(String response) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode data = jsonNode.get("data");  // 兼容模式下使用data字段
        if (data == null || !data.isArray()) {
            throw new RuntimeException("API 响应格式错误: data 字段不存在或不是数组");
        }
        
        List<float[]> vectors = new ArrayList<>();
        for (JsonNode item : data) {
            JsonNode embedding = item.get("embedding");
            if (embedding != null && embedding.isArray()) {
                float[] vector = new float[embedding.size()];
                for (int i = 0; i < embedding.size(); i++) {
                    vector[i] = (float) embedding.get(i).asDouble();
                }
                vectors.add(vector);
            }
        }
        return vectors;
    }
}
