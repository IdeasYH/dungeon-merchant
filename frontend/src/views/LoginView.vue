<script setup>
import { reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

import BrandPanel from '../components/BrandPanel.vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const form = reactive({
  username: '',
  password: '',
})

async function submit() {
  try {
    await authStore.login(form)
    router.push('/')
  } catch {
  }
}
</script>

<template>
  <section class="scene-grid">
    <BrandPanel
      eyebrow="Trade Ward"
      title="Run the guild from the shadows."
      lead="Sign in to your ledger and move from smoky tavern deals to full-scale merchant empire management."
      quote="“Every fortune starts with a single deal struck under lantern light.”"
      left-stat-label="Aesthetic"
      left-stat-value="Ash, amber, and gilt"
      right-stat-label="Entry cadence"
      right-stat-value="Staggered and theatrical"
    />

    <section class="content-card stagger">
      <div>
        <p class="topbar__eyebrow">Return to the floor</p>
        <h2 class="content-card__title">Sign in</h2>
        <p class="content-card__copy">
          Use your operator credentials to reopen the guild ledger.
        </p>
      </div>

      <div
        v-if="authStore.error"
        class="status-card status-card--error"
        role="alert"
      >
        <h3 class="status-card__title">Entry denied</h3>
        <p class="status-card__copy">{{ authStore.error }}</p>
      </div>

      <form class="stack" @submit.prevent="submit">
        <label class="field">
          <span class="field__label">Guild name</span>
          <input
            v-model.trim="form.username"
            class="input"
            autocomplete="username"
            placeholder="merchant"
            required
          >
        </label>

        <label class="field">
          <span class="field__label">Passphrase</span>
          <input
            v-model="form.password"
            class="input"
            autocomplete="current-password"
            placeholder="Password123!"
            required
            type="password"
          >
        </label>

        <div class="button-row">
          <button class="button button--primary" :disabled="authStore.busy" type="submit">
            {{ authStore.busy ? 'Opening the ledger...' : 'Open the ledger' }}
          </button>
          <RouterLink class="button button--ghost" to="/register">
            Create an account
          </RouterLink>
        </div>
      </form>
    </section>
  </section>
</template>
