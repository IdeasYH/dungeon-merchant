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
    localError.value = '两次输入的密码不一致。'
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
      title="创建新的 merchant house。"
      lead="注册新的 guild 身份，并在签发令牌后直接进入指挥大厅。"
      quote="“市场只记得那些敢把名字刻进大门的人。”"
      left-stat-label="主题"
      left-stat-value="暗色天鹅绒 + 熔金"
      right-stat-label="布局"
      right-stat-value="偏轴且富有电影感"
    />

    <section class="content-card stagger">
      <div>
        <p class="topbar__eyebrow">签发文书</p>
        <h2 class="content-card__title">创建账户</h2>
        <p class="content-card__copy">
          账户名需为 3–50 个字符，密码至少 8 位。
        </p>
      </div>

      <div
        v-if="localError || authStore.error"
        class="status-card status-card--error"
        role="alert"
      >
        <h3 class="status-card__title">注册失败</h3>
        <p class="status-card__copy">{{ localError || authStore.error }}</p>
      </div>

      <form class="stack" @submit.prevent="submit">
        <label class="field">
          <span class="field__label">账户名</span>
          <input
            v-model.trim="form.username"
            class="input"
            autocomplete="username"
            maxlength="50"
            minlength="3"
            placeholder="请输入账户名"
            required
          >
        </label>

        <label class="field">
          <span class="field__label">密码</span>
          <input
            v-model="form.password"
            class="input"
            autocomplete="new-password"
            minlength="8"
            placeholder="请输入密码"
            required
            type="password"
          >
        </label>

        <label class="field">
          <span class="field__label">确认密码</span>
          <input
            v-model="form.confirmPassword"
            class="input"
            autocomplete="new-password"
            placeholder="请再次输入密码"
            required
            type="password"
          >
        </label>

        <div class="button-row">
          <button class="button button--primary" :disabled="!canSubmit" type="submit">
            {{ authStore.busy ? '正在签发文书...' : '完成注册' }}
          </button>
          <RouterLink class="button button--ghost" to="/login">
            登录
          </RouterLink>
        </div>
      </form>
    </section>
  </section>
</template>
