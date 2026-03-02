<script setup lang="tsx">
import { NTag } from 'naive-ui';
import UserSearch from './modules/user-search.vue';

const appStore = useAppStore();

function apiFn(params: Api.User.SearchParams) {
  return request<Api.User.List>({ url: '/admin/users/list', params });
}

const { columns, columnChecks, data, getData, loading, mobilePagination, searchParams, resetSearchParams } = useTable({
  apiFn,
  apiParams: {
    keyword: null,
    status: null
  },
  columns: () => [
    {
      key: 'index',
      title: '序号',
      width: 64
    },
    {
      key: 'username',
      title: '用户名',
      minWidth: 100
    },
    {
      key: 'status',
      title: '状态',
      width: 100,
      render: row => <NTag type={row.status === 1 ? 'success' : 'warning'}>{row.status === 1 ? '已启用' : '已禁用'}</NTag>
    },
    {
      key: 'createdAt',
      title: '创建时间',
      width: 180,
      render: row => row.createdAt ? dayjs(row.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-'
    }
  ]
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <Teleport defer to="#header-extra">
      <UserSearch v-model:model="searchParams" @reset="resetSearchParams" @search="getData" />
    </Teleport>
    <NCard title="用户列表" :bordered="false" size="small" class="sm:flex-1-hidden card-wrapper">
      <template #header-extra>
        <TableHeaderOperation v-model:columns="columnChecks" :addable="false" :loading="loading" @refresh="getData" />
      </template>
      <NDataTable
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :scroll-x="400"
        :loading="loading"
        remote
        :row-key="(row: Api.User.Item) => row.userId"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
