<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import BrandPanel from '../components/BrandPanel.vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const summaryRows = computed(() => {
  if (!authStore.account) {
    return []
  }

  return [
    { label: '账户 ID', value: String(authStore.account.id) },
    { label: '账户名', value: authStore.account.username },
    { label: '创建时间', value: formatDate(authStore.account.createdAt) },
    { label: '更新时间', value: formatDate(authStore.account.updatedAt) },
    { label: '令牌类型', value: authStore.session?.tokenType || 'Bearer' },
  ]
})

onMounted(async () => {
  if (!authStore.account) {
    try {
      await authStore.fetchAccount()
    } catch {
    }
  }
})

async function refreshAccount() {
  authStore.clearError()

  try {
    await authStore.fetchAccount()
  } catch {
  }
}

function leaveGuild() {
  authStore.logout()
  router.push('/login')
}

function formatDate(value) {
  if (!value) {
    return '—'
  }

  return new Intl.DateTimeFormat('zh-CN', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}
</script>

<template>
  <section class="dashboard">
    <div class="dashboard__grid">
      <BrandPanel
        eyebrow="Command Hall"
        title="你的 merchant house 已上线。"
        lead="这个首页已接入真实账户接口，并展示当前保存在 Pinia 与本地存储中的 JWT 会话。"
        quote="“先清点金币，研判市场，再在黎明商贩苏醒前出手。”"
        left-stat-label="API 来源"
        left-stat-value="GET /api/account"
        right-stat-label="状态"
        right-stat-value="Pinia + 持久化会话"
      />

      <section class="summary-card stagger">
        <p class="summary-card__eyebrow">操作概览</p>
        <h2 class="summary-card__title">
          {{ authStore.account?.username || authStore.session?.username || '加载账户中' }}
        </h2>
        <p class="content-card__copy">
          查看当前已认证账户，并在后端数据变化后刷新 Guild Ledger。
        </p>

        <div
          v-if="authStore.error"
          class="status-card status-card--error"
          role="alert"
        >
          <h3 class="status-card__title">账户同步失败</h3>
          <p class="status-card__copy">{{ authStore.error }}</p>
        </div>

        <div v-else-if="!authStore.account" class="status-card status-card--success">
          <h3 class="status-card__title">正在获取详情</h3>
          <p class="status-card__copy">Guild Ledger 正在连接后端。</p>
        </div>

        <div v-else class="detail-list">
          <div
            v-for="row in summaryRows"
            :key="row.label"
            class="detail-list__item"
          >
            <span class="detail-list__label">{{ row.label }}</span>
            <span class="detail-list__value">{{ row.value }}</span>
          </div>
        </div>

        <div class="button-row">
          <button class="button button--primary" :disabled="authStore.busy" @click="refreshAccount">
            刷新账户
          </button>
          <button class="button button--ghost" @click="leaveGuild">
            退出登录
          </button>
        </div>
      </section>
    </div>
  </section>
</template>
