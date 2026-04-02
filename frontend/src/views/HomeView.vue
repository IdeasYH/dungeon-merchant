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
    { label: 'Account ID', value: String(authStore.account.id) },
    { label: 'Username', value: authStore.account.username },
    { label: 'Created', value: formatDate(authStore.account.createdAt) },
    { label: 'Updated', value: formatDate(authStore.account.updatedAt) },
    { label: 'Token', value: authStore.session?.tokenType || 'Bearer' },
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

  return new Intl.DateTimeFormat('en', {
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
        title="Your merchant house is live."
        lead="This front page is wired to the live account endpoint and reflects the active JWT session stored in Pinia and local storage."
        quote="“Count your coin, study the market, then strike before the dawn traders wake.”"
        left-stat-label="API source"
        left-stat-value="GET /api/account"
        right-stat-label="State"
        right-stat-value="Pinia + persisted session"
      />

      <section class="summary-card stagger">
        <p class="summary-card__eyebrow">Operator overview</p>
        <h2 class="summary-card__title">
          {{ authStore.account?.username || authStore.session?.username || 'Loading account' }}
        </h2>
        <p class="content-card__copy">
          Review the current authenticated account and refresh the ledger whenever the backend changes.
        </p>

        <div
          v-if="authStore.error"
          class="status-card status-card--error"
          role="alert"
        >
          <h3 class="status-card__title">Account sync failed</h3>
          <p class="status-card__copy">{{ authStore.error }}</p>
        </div>

        <div v-else-if="!authStore.account" class="status-card status-card--success">
          <h3 class="status-card__title">Fetching details</h3>
          <p class="status-card__copy">The ledger is contacting the backend now.</p>
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
            Refresh account
          </button>
          <button class="button button--ghost" @click="leaveGuild">
            Sign out
          </button>
        </div>
      </section>
    </div>
  </section>
</template>
