<script setup>
import { computed, reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

import BrandPanel from '../components/BrandPanel.vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})
const localError = ref('')

const canSubmit = computed(() => {
  return form.username.trim().length >= 3 && form.password.length >= 8 && !authStore.busy
})

async function submit() {
  localError.value = ''

  if (form.password !== form.confirmPassword) {
    localError.value = 'The confirmation seal does not match the passphrase.'
    return
  }

  try {
    await authStore.register({
      username: form.username,
      password: form.password,
    })
    router.push('/')
  } catch {
  }
}
</script>

<template>
  <section class="scene-grid">
    <BrandPanel
      eyebrow="Founder's Annex"
      title="Forge a new merchant house."
      lead="Register a new guild identity and step straight into the command hall with tokens already issued."
      quote="“The market only remembers the names bold enough to carve themselves into its doors.”"
      left-stat-label="Theme"
      left-stat-value="Dark velvet + molten gold"
      right-stat-label="Layout"
      right-stat-value="Off-axis and cinematic"
    />

    <section class="content-card stagger">
      <div>
        <p class="topbar__eyebrow">Commission a charter</p>
        <h2 class="content-card__title">Create an account</h2>
        <p class="content-card__copy">
          Username must be 3–50 characters and password must be at least 8 characters.
        </p>
      </div>

      <div
        v-if="localError || authStore.error"
        class="status-card status-card--error"
        role="alert"
      >
        <h3 class="status-card__title">Charter rejected</h3>
        <p class="status-card__copy">{{ localError || authStore.error }}</p>
      </div>

      <form class="stack" @submit.prevent="submit">
        <label class="field">
          <span class="field__label">Guild name</span>
          <input
            v-model.trim="form.username"
            class="input"
            autocomplete="username"
            maxlength="50"
            minlength="3"
            placeholder="merchant"
            required
          >
        </label>

        <label class="field">
          <span class="field__label">Passphrase</span>
          <input
            v-model="form.password"
            class="input"
            autocomplete="new-password"
            minlength="8"
            placeholder="Password123!"
            required
            type="password"
          >
        </label>

        <label class="field">
          <span class="field__label">Confirm passphrase</span>
          <input
            v-model="form.confirmPassword"
            class="input"
            autocomplete="new-password"
            placeholder="Repeat your passphrase"
            required
            type="password"
          >
        </label>

        <div class="button-row">
          <button class="button button--primary" :disabled="!canSubmit" type="submit">
            {{ authStore.busy ? 'Stamping your charter...' : 'Seal the charter' }}
          </button>
          <RouterLink class="button button--ghost" to="/login">
            Sign in
          </RouterLink>
        </div>
      </form>
    </section>
  </section>
</template>
