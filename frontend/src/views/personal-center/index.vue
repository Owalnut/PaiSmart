<script setup lang="ts">
const { userInfo } = storeToRefs(useAuthStore());
</script>

<template>
  <div class="flex-cc">
    <NCard class="min-h-400px min-w-600px w-50vw card-wrapper" :segmented="{ content: true, footer: 'soft' }">
      <template #header>
        <div class="flex items-center gap-4">
          <NAvatar size="large">
            <icon-solar:user-circle-linear class="text-icon-large" />
          </NAvatar>
          <div>{{ userInfo.username }}</div>
        </div>
      </template>
      <div class="p-4 text-gray-500">
        文件可见性在「知识库」上传时选择「个人」或「公开」，无需在此设置。
      </div>
    </NCard>
  </div>
</template>

<style scoped lang="scss">
:deep(.n-card__content) {
  flex: none !important;
  height: fit-content;
}
</style>
