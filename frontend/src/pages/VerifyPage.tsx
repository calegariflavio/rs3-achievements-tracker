import { useEffect, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import axios from 'axios'
import { verifyEmail } from '../api/authApi'
import { useAuth } from '../context/AuthContext'

export default function VerifyPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { loginWithToken } = useAuth()
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading')
  const [errorMsg, setErrorMsg] = useState('')

  useEffect(() => {
    document.title = 'Verify Email | RS3 Tracker'
    const token = searchParams.get('token')
    if (!token) {
      setStatus('error')
      setErrorMsg('No verification token found in the link.')
      return
    }
    verifyEmail(token)
      .then(async (res) => {
        await loginWithToken(res.data.token)
        setStatus('success')
        setTimeout(() => navigate('/'), 2500)
      })
      .catch((err) => {
        setStatus('error')
        if (axios.isAxiosError(err)) {
          setErrorMsg(err.response?.data?.message ?? 'Verification failed.')
        } else {
          setErrorMsg('An unexpected error occurred.')
        }
      })
  }, [])

  return (
    <main className="flex-1 bg-stone-950 flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-sm text-center">
        {status === 'loading' && (
          <>
            <div className="w-12 h-12 border-4 border-stone-700 border-t-amber-500 rounded-full animate-spin mx-auto mb-6" />
            <p className="text-stone-400 text-sm">Verifying your email…</p>
          </>
        )}

        {status === 'success' && (
          <>
            <div className="text-5xl mb-6">✅</div>
            <h2 className="text-xl font-bold text-amber-400 mb-3">Email verified!</h2>
            <p className="text-stone-400 text-sm mb-6">
              Your account is now active. Redirecting you to the home page…
            </p>
            <Link to="/" className="text-amber-400 hover:text-amber-300 text-sm transition-colors">
              Go now
            </Link>
          </>
        )}

        {status === 'error' && (
          <>
            <div className="text-5xl mb-6">❌</div>
            <h2 className="text-xl font-bold text-red-400 mb-3">Verification failed</h2>
            <p className="text-stone-400 text-sm mb-8">{errorMsg}</p>
            <Link
              to="/login"
              className="bg-amber-600 hover:bg-amber-500 text-stone-950 font-bold px-6 py-2.5 rounded-lg transition-colors text-sm"
            >
              Back to sign in
            </Link>
          </>
        )}
      </div>
    </main>
  )
}
