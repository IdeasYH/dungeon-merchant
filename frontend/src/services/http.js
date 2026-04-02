import axios from 'axios'

import { clearSession, readSession } from '../utils/session'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const session = readSession()

  if (session?.accessToken) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `${session.tokenType} ${session.accessToken}`
  }

  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      clearSession()
    }

    return Promise.reject(error)
  },
)

export function unwrapApiResponse(response) {
  return response?.data?.data ?? null
}

export function formatApiError(error) {
  return (
    error?.response?.data?.message ||
    error?.message ||
    'Guild Ledger 拒绝了这次请求。'
  )
}
