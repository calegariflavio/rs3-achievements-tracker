import api from './axiosConfig'

export interface AuthResponse {
  token: string
  email: string
}

export interface UserProfile {
  email: string
  claimedCharacters?: string[]
}

export const register = (email: string, password: string) =>
  api.post<AuthResponse>('/api/auth/register', { email, password })

export const login = (email: string, password: string) =>
  api.post<AuthResponse>('/api/auth/login', { email, password })

export const claimCharacter = (characterName: string, token: string) =>
  api.post('/api/auth/claim', { characterName }, {
    headers: { Authorization: `Bearer ${token}` },
  })

export const getMe = (token: string) =>
  api.get<UserProfile>('/api/auth/me', {
    headers: { Authorization: `Bearer ${token}` },
  })
