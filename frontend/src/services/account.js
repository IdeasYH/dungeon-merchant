import { http, unwrapApiResponse } from './http'

export async function fetchAccount() {
  const response = await http.get('/account')
  return unwrapApiResponse(response)
}
