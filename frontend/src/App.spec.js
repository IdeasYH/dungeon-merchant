import { mount } from '../node_modules/@vue/test-utils/dist/vue-test-utils.esm-bundler.mjs'
import { describe, expect, it } from 'vitest'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

describe('App', () => {
  it('renders the merchant operations console', async () => {
    router.push('/login')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [createPinia(), router],
      },
    })

    expect(wrapper.text()).toContain('Dungeon Merchant 控制台')
    expect(wrapper.text()).toContain('创建账户')
    expect(wrapper.text()).toContain('登录')
    expect(wrapper.text()).toContain('加载账户')
  })
})
