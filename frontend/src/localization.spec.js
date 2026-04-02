import { mount } from '../node_modules/@vue/test-utils/dist/vue-test-utils.esm-bundler.mjs'
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'

function buildSession() {
  return {
    accountId: 12,
    username: 'merchant-cn',
    accessToken: 'test-access-token',
    refreshToken: 'test-refresh-token',
    tokenType: 'Bearer',
    expiresIn: 3600,
    refreshExpiresIn: 7200,
  }
}

async function renderRoute(path, options = {}) {
  localStorage.clear()
  const pinia = createPinia()
  setActivePinia(pinia)

  const authStore = useAuthStore()

  if (options.session) {
    authStore.session = options.session
    localStorage.setItem('dungeon-merchant/session', JSON.stringify(options.session))
  }

  if (options.account) {
    authStore.account = options.account
  }

  await router.push(path)

  return mount(App, {
    global: {
      plugins: [pinia, router],
    },
  })
}

describe('localized frontend copy', () => {
  beforeEach(async () => {
    localStorage.clear()
    await router.push('/login')
    await router.isReady()
  })

  it('renders Chinese login copy', async () => {
    const wrapper = await renderRoute('/login')

    expect(wrapper.text()).toContain('登录')
    expect(wrapper.text()).toContain('账户名')
    expect(wrapper.text()).toContain('密码')
    expect(wrapper.text()).toContain('进入账本')
  })

  it('renders Chinese register copy', async () => {
    const wrapper = await renderRoute('/register')

    expect(wrapper.text()).toContain('创建账户')
    expect(wrapper.text()).toContain('确认密码')
    expect(wrapper.text()).toContain('完成注册')
  })

  it('renders Chinese home copy', async () => {
    const wrapper = await renderRoute('/', {
      session: buildSession(),
      account: {
        id: 12,
        username: 'merchant-cn',
        createdAt: '2026-04-02T00:00:00',
        updatedAt: '2026-04-02T01:00:00',
      },
    })

    expect(wrapper.text()).toContain('账户 ID')
    expect(wrapper.text()).toContain('刷新账户')
    expect(wrapper.text()).toContain('退出登录')
  })
})
