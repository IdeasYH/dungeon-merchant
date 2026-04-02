export const SESSION_STORAGE_KEY = 'dungeon-merchant/session'

export function normalizeSession(payload) {
  if (!payload?.accessToken || !payload?.refreshToken) {
    return null
  }

  return {
    accountId: Number(payload.accountId ?? 0),
    username: String(payload.username ?? ''),
    accessToken: String(payload.accessToken),
    refreshToken: String(payload.refreshToken),
    tokenType: String(payload.tokenType ?? 'Bearer'),
    expiresIn: Number(payload.expiresIn ?? 0),
    refreshExpiresIn: Number(payload.refreshExpiresIn ?? 0),
  }
}

export function readSession() {
  const raw = localStorage.getItem(SESSION_STORAGE_KEY)
  if (!raw) {
    return null
  }

  try {
    const normalized = normalizeSession(JSON.parse(raw))
    if (!normalized) {
      clearSession()
    }
    return normalized
  } catch {
    clearSession()
    return null
  }
}

export function writeSession(payload) {
  const normalized = normalizeSession(payload)
  if (!normalized) {
    clearSession()
    return null
  }

  localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(normalized))
  return normalized
}

export function clearSession() {
  localStorage.removeItem(SESSION_STORAGE_KEY)
}
