import { http, unwrapApiResponse } from './http'

export async function login(payload) {
  const response = await http.post('/auth/login', payload)
  return unwrapApiResponse(response)
}

export async function register(payload) {
  const response = await http.post('/auth/register', payload)
  return unwrapApiResponse(response)
}
