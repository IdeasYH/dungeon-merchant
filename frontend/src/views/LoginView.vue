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
      title="在阴影中经营你的公会。"
      lead="登录 Guild Ledger，从昏暗酒馆里的小交易一路扩展到完整的 merchant empire 管理。"
      quote="“每一笔财富，都始于灯火下谈成的一桩交易。”"
      left-stat-label="视觉风格"
      left-stat-value="灰烬、琥珀与鎏金"
      right-stat-label="入场节奏"
      right-stat-value="错落而富有戏剧感"
    />

    <section class="content-card stagger">
      <div>
        <p class="topbar__eyebrow">返回大厅</p>
        <h2 class="content-card__title">登录</h2>
        <p class="content-card__copy">
          使用你的账户凭证重新打开 Guild Ledger。
        </p>
      </div>

      <div
        v-if="authStore.error"
        class="status-card status-card--error"
        role="alert"
      >
        <h3 class="status-card__title">登录失败</h3>
        <p class="status-card__copy">{{ authStore.error }}</p>
      </div>

      <form class="stack" @submit.prevent="submit">
        <label class="field">
          <span class="field__label">账户名</span>
          <input
            v-model.trim="form.username"
            class="input"
            autocomplete="username"
            placeholder="请输入账户名"
            required
          >
        </label>

        <label class="field">
          <span class="field__label">密码</span>
          <input
            v-model="form.password"
            class="input"
            autocomplete="current-password"
            placeholder="请输入密码"
            required
            type="password"
          >
        </label>

        <div class="button-row">
          <button class="button button--primary" :disabled="authStore.busy" type="submit">
            {{ authStore.busy ? '正在打开账本...' : '进入账本' }}
          </button>
          <RouterLink class="button button--ghost" to="/register">
            创建账户
          </RouterLink>
        </div>
      </form>
    </section>
  </section>
</template>
