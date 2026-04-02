import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

import { useAuthStore } from './auth'
import { clearSession, readSession } from '../utils/session'
import * as authApi from '../services/auth'
import * as accountApi from '../services/account'

vi.mock('../services/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
}))

vi.mock('../services/account', () => ({
  fetchAccount: vi.fn(),
}))

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.resetAllMocks()
    clearSession()
  })

  it('hydrates an existing session from local storage', () => {
    localStorage.setItem(
      'dungeon-merchant/session',
      JSON.stringify({
        accountId: 9,
        username: 'quartermaster',
        accessToken: 'stored-access',
        refreshToken: 'stored-refresh',
        tokenType: 'Bearer',
        expiresIn: 1200,
        refreshExpiresIn: 4800,
      }),
    )

    const store = useAuthStore()
    store.hydrate()

    expect(store.isAuthenticated).toBe(true)
    expect(store.session.username).toBe('quartermaster')
  })

  it('persists session and account details after login', async () => {
    authApi.login.mockResolvedValue({
      accountId: 3,
      username: 'merchant',
      accessToken: 'fresh-access',
      refreshToken: 'fresh-refresh',
      tokenType: 'Bearer',
      expiresIn: 3600,
      refreshExpiresIn: 7200,
    })
    accountApi.fetchAccount.mockResolvedValue({
      id: 3,
      username: 'merchant',
      createdAt: '2026-04-01T00:00:00',
      updatedAt: '2026-04-02T00:00:00',
    })

    const store = useAuthStore()
    await store.login({
      username: 'merchant',
      password: 'Password123!',
    })

    expect(readSession()).toMatchObject({
      accountId: 3,
      username: 'merchant',
      accessToken: 'fresh-access',
    })
    expect(store.account.username).toBe('merchant')
    expect(store.error).toBe('')
  })

  it('clears stale session state after a failed login', async () => {
    authApi.login.mockRejectedValue({
      response: {
        data: {
          message: 'Invalid username or password',
        },
      },
    })

    const store = useAuthStore()
    await expect(
      store.login({
        username: 'merchant',
        password: 'bad-password',
      }),
    ).rejects.toBeDefined()

    expect(store.isAuthenticated).toBe(false)
    expect(store.error).toBe('Invalid username or password')
    expect(readSession()).toBeNull()
  })
})
