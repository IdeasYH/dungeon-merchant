import { beforeEach, describe, expect, it } from 'vitest'

import { clearSession, readSession, SESSION_STORAGE_KEY, writeSession } from './session'

describe('session utilities', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('returns null when storage is empty', () => {
    expect(readSession()).toBeNull()
  })

  it('writes and reads a normalized session payload', () => {
    writeSession({
      accountId: 7,
      username: 'merchant',
      accessToken: 'access-token',
      refreshToken: 'refresh-token',
      tokenType: 'Bearer',
      expiresIn: '3600',
      refreshExpiresIn: '7200',
    })

    expect(readSession()).toEqual({
      accountId: 7,
      username: 'merchant',
      accessToken: 'access-token',
      refreshToken: 'refresh-token',
      tokenType: 'Bearer',
      expiresIn: 3600,
      refreshExpiresIn: 7200,
    })
  })

  it('clears invalid persisted data', () => {
    localStorage.setItem(SESSION_STORAGE_KEY, '{"broken":true}')

    expect(readSession()).toBeNull()
    expect(localStorage.getItem(SESSION_STORAGE_KEY)).toBeNull()
  })

  it('removes the stored session', () => {
    localStorage.setItem(SESSION_STORAGE_KEY, '{"accessToken":"x","refreshToken":"y"}')

    clearSession()

    expect(localStorage.getItem(SESSION_STORAGE_KEY)).toBeNull()
  })
})
