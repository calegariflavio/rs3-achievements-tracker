import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useAuth } from '../context/AuthContext'
import { register as apiRegister } from '../api/authApi'

export default function LoginPage() {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [registered, setRegistered] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    document.title = 'Login | RS3 Tracker'
  }, [])

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      if (mode === 'register') {
        await apiRegister(email, password)
        setRegistered(true)
        return
      }
      await login(email, password)
      navigate('/')
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const status = err.response?.status
        const msg = err.response?.data?.message ?? err.message
        if (status === 403) {
          setError('Your email address has not been verified yet. Please check your inbox.')
        } else {
          setError(msg)
        }
      } else {
        setError('An unexpected error occurred. Please try again.')
      }
    } finally {
      setLoading(false)
    }
  }

  function toggleMode() {
    setMode((m) => (m === 'login' ? 'register' : 'login'))
    setError(null)
    setRegistered(false)
  }

  if (registered) {
    return (
      <main className="flex-1 bg-stone-950 flex items-center justify-center px-4 py-12">
        <div className="w-full max-w-sm text-center">
          <div className="text-5xl mb-6">📬</div>
          <h2 className="text-xl font-bold text-amber-400 mb-3">Check your inbox</h2>
          <p className="text-stone-400 text-sm mb-2">
            We sent a verification link to{' '}
            <span className="text-stone-200 font-medium">{email}</span>.
          </p>
          <p className="text-stone-500 text-sm mb-8">
            Click the link in the email to activate your account, then come back to sign in.
          </p>
          <button
            onClick={toggleMode}
            className="text-amber-400 hover:text-amber-300 text-sm transition-colors"
          >
            Back to sign in
          </button>
        </div>
      </main>
    )
  }

  return (
    <main className="flex-1 bg-stone-950 flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-sm">
        {/* Logo */}
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex flex-col items-center gap-1">
            <span className="text-amber-500 text-4xl leading-none">⚔</span>
            <span className="text-2xl font-bold text-amber-400 tracking-wide">RS3 Tracker</span>
          </Link>
          <p className="text-stone-400 text-sm mt-2">
            {mode === 'login' ? 'Sign in to your account' : 'Create a new account'}
          </p>
        </div>

        {/* Form card */}
        <div className="bg-stone-900 border border-stone-700 rounded-xl p-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-stone-400 text-xs font-medium mb-1.5">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoFocus
                placeholder="you@example.com"
                className="w-full bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-1 focus:ring-amber-500/30 rounded-lg px-4 py-3 text-stone-100 placeholder-stone-500 text-sm outline-none transition-colors min-h-[44px]"
              />
            </div>

            <div>
              <label className="block text-stone-400 text-xs font-medium mb-1.5">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="••••••••"
                className="w-full bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-1 focus:ring-amber-500/30 rounded-lg px-4 py-3 text-stone-100 placeholder-stone-500 text-sm outline-none transition-colors min-h-[44px]"
              />
            </div>

            {error && (
              <div className="text-red-400 text-sm bg-red-950/40 border border-red-800/50 rounded-lg px-3 py-2">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-amber-600 hover:bg-amber-500 disabled:opacity-50 text-stone-950 font-bold py-3 rounded-lg transition-colors text-sm min-h-[44px]"
            >
              {loading
                ? mode === 'login' ? 'Signing in…' : 'Creating account…'
                : mode === 'login' ? 'Sign In' : 'Create Account'}
            </button>
          </form>

          <div className="mt-5 pt-4 border-t border-stone-700/60 text-center">
            <button
              onClick={toggleMode}
              className="text-stone-400 hover:text-amber-400 text-sm transition-colors"
            >
              {mode === 'login'
                ? "Don't have an account? Register"
                : 'Already have an account? Sign in'}
            </button>
          </div>
        </div>
      </div>
    </main>
  )
}
