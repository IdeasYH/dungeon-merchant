import { defineStore } from 'pinia'

import { fetchAccount } from '../services/account'
import { login as loginRequest, register as registerRequest } from '../services/auth'
import { formatApiError } from '../services/http'
import { clearSession, readSession, writeSession } from '../utils/session'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    session: readSession(),
    account: null,
    busy: false,
    error: '',
  }),

  getters: {
    isAuthenticated: state => Boolean(state.session?.accessToken),
  },

  actions: {
    hydrate() {
      this.session = readSession()
      return this.session
    },

    async login(payload) {
      return this.authorize(() => loginRequest(payload))
    },

    async register(payload) {
      return this.authorize(() => registerRequest(payload))
    },

    logout() {
      clearSession()
      this.session = null
      this.account = null
      this.error = ''
    },

    async authorize(request) {
      this.busy = true
      this.error = ''

      try {
        const nextSession = writeSession(await request())
        this.session = nextSession
        await this.fetchAccount()
        return nextSession
      } catch (error) {
        this.session = null
        this.account = null
        this.error = formatApiError(error)
        clearSession()
        throw error
      } finally {
        this.busy = false
      }
    },

    async fetchAccount() {
      if (!this.isAuthenticated) {
        this.account = null
        return null
      }

      this.account = await fetchAccount()
      return this.account
    },

    clearError() {
      this.error = ''
    },
  },
})
