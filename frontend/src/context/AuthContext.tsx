import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { login as apiLogin, getMe, type UserProfile } from '../api/authApi'

interface AuthContextValue {
  user: UserProfile | null
  token: string | null
  isAuthenticated: boolean
  authReady: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => void
  refreshUser: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null)
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('auth_token'))
  // authReady is false only while we're verifying a stored token on mount
  const [authReady, setAuthReady] = useState(!localStorage.getItem('auth_token'))

  useEffect(() => {
    const stored = localStorage.getItem('auth_token')
    if (!stored) return
    getMe(stored)
      .then((res) => setUser(res.data))
      .catch(() => {
        localStorage.removeItem('auth_token')
        setToken(null)
      })
      .finally(() => setAuthReady(true))
  }, [])

  async function login(email: string, password: string) {
    const res = await apiLogin(email, password)
    const { token: newToken } = res.data
    localStorage.setItem('auth_token', newToken)
    setToken(newToken)
    const profile = await getMe(newToken)
    setUser(profile.data)
    setAuthReady(true)
  }

  function logout() {
    localStorage.removeItem('auth_token')
    setToken(null)
    setUser(null)
  }

  async function refreshUser() {
    if (!token) return
    const res = await getMe(token)
    setUser(res.data)
  }

  return (
    <AuthContext.Provider
      value={{ user, token, isAuthenticated: !!token, authReady, login, logout, refreshUser }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
