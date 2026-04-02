import { defineStore } from 'pinia'
import { normalizeBaseUrl } from '../lib/api'

const STORAGE_KEY = 'dungeon-merchant-session'

function defaultState() {
  return {
    hydrated: false,
    baseUrl: 'http://localhost:8080',
    accountId: null,
    username: '',
    accessToken: '',
    refreshToken: '',
    tokenType: 'Bearer',
    expiresIn: 0,
    refreshExpiresIn: 0,
  }
}

function readPersistedState() {
  if (typeof window === 'undefined') {
    return defaultState()
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    if (!raw) {
      return defaultState()
    }

    return {
      ...defaultState(),
      ...JSON.parse(raw),
    }
  } catch {
    return defaultState()
  }
}

export const useSessionStore = defineStore('session', {
  state: () => defaultState(),

  getters: {
    isAuthenticated: state => Boolean(state.accessToken),
  },

  actions: {
    hydrate() {
      if (this.hydrated) {
        return
      }

      Object.assign(this, readPersistedState(), { hydrated: true })
    },

    persist() {
      if (typeof window === 'undefined') {
        return
      }

      const { hydrated, ...serializableState } = this.$state
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(serializableState))
    },

    setBaseUrl(value) {
      this.baseUrl = normalizeBaseUrl(value)
      this.persist()
    },

    setSession(payload) {
      this.accountId = payload.accountId ?? this.accountId
      this.username = payload.username ?? this.username
      this.accessToken = payload.accessToken ?? ''
      this.refreshToken = payload.refreshToken ?? ''
      this.tokenType = payload.tokenType ?? 'Bearer'
      this.expiresIn = payload.expiresIn ?? 0
      this.refreshExpiresIn = payload.refreshExpiresIn ?? 0
      this.persist()
    },

    setAccount(payload) {
      this.accountId = payload.id ?? this.accountId
      this.username = payload.username ?? this.username
      this.persist()
    },

    setUsername(username) {
      this.username = username
      this.persist()
    },

    clearSession() {
      const currentBaseUrl = this.baseUrl
      Object.assign(this, defaultState(), {
        hydrated: true,
        baseUrl: currentBaseUrl,
      })
      this.persist()
    },
  },
})
