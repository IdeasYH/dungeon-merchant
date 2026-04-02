import axios from 'axios'

export function normalizeBaseUrl(value) {
  const baseUrl = String(value || 'http://localhost:8080').trim()
  return baseUrl.replace(/\/+$/, '') || 'http://localhost:8080'
}

export function createMerchantApi(baseUrl, accessToken) {
  const headers = {}

  if (accessToken) {
    headers.Authorization = `Bearer ${accessToken}`
  }

  return axios.create({
    baseURL: normalizeBaseUrl(baseUrl),
    headers,
  })
}

export function unwrapResponse(response) {
  return response?.data?.data ?? response?.data ?? null
}

export function extractApiMessage(error) {
  return error?.response?.data?.message || error?.message || 'Request failed'
}
