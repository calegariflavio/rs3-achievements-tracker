import { useEffect, useRef, useState } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useAuth } from '../context/AuthContext'
import { claimCharacter } from '../api/authApi'

export default function Navbar() {
  const { isAuthenticated, user, token, logout, refreshUser } = useAuth()
  const navigate = useNavigate()

  const [showClaim, setShowClaim] = useState(false)
  const [claimInput, setClaimInput] = useState('')
  const [claimStatus, setClaimStatus] = useState<'idle' | 'loading' | 'success' | 'error'>('idle')
  const [claimError, setClaimError] = useState<string | null>(null)
  const [mobileOpen, setMobileOpen] = useState(false)
  const claimRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!showClaim) return
    function onClickOutside(e: MouseEvent) {
      if (claimRef.current && !claimRef.current.contains(e.target as Node)) {
        setShowClaim(false)
      }
    }
    document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [showClaim])

  function closeMobile() {
    setMobileOpen(false)
  }

  function openClaim() {
    setShowClaim((v) => !v)
    setClaimStatus('idle')
    setClaimError(null)
    setClaimInput('')
  }

  async function handleClaim(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const name = claimInput.trim()
    if (!name || !token) return
    setClaimStatus('loading')
    setClaimError(null)
    try {
      await claimCharacter(name, token)
      await refreshUser()
      setClaimStatus('success')
      setTimeout(() => {
        setShowClaim(false)
        setClaimStatus('idle')
        setClaimInput('')
      }, 1500)
    } catch (err) {
      setClaimError(
        axios.isAxiosError(err)
          ? (err.response?.data?.message ?? 'Failed to claim character.')
          : 'Failed to claim character.',
      )
      setClaimStatus('error')
    }
  }

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `px-4 py-1.5 text-sm font-medium transition-colors rounded ${
      isActive
        ? 'text-amber-400 bg-amber-900/30 border border-amber-700/50'
        : 'text-stone-300 hover:text-amber-400 hover:bg-stone-800'
    }`

  const mobileLinkClass = ({ isActive }: { isActive: boolean }) =>
    `flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors min-h-[44px] ${
      isActive
        ? 'text-amber-400 bg-amber-900/30'
        : 'text-stone-300 hover:text-amber-400 hover:bg-stone-800'
    }`

  return (
    <nav className="bg-stone-950 border-b border-amber-900/40 shadow-lg shadow-black/50 relative z-50">
      {/* Main bar */}
      <div className="max-w-6xl mx-auto px-4 flex items-center justify-between h-14">
        {/* Logo */}
        <NavLink to="/" onClick={closeMobile} className="flex items-center gap-2 group shrink-0">
          <span className="text-amber-500 text-xl leading-none">⚔</span>
          <span className="text-amber-400 font-bold text-base tracking-wide group-hover:text-amber-300 transition-colors">
            RS3 Tracker
          </span>
        </NavLink>

        {/* Desktop nav links */}
        <div className="hidden md:flex items-center gap-1">
          <NavLink to="/" end className={linkClass}>Home</NavLink>
          <NavLink to="/archaeology" className={linkClass}>Archaeology Log</NavLink>
        </div>

        {/* Desktop auth section */}
        <div className="hidden md:flex items-center gap-2 shrink-0">
          {isAuthenticated ? (
            <>
              <span className="text-stone-500 text-xs max-w-[140px] truncate">
                {user?.email}
              </span>

              {/* Claim Character dropdown */}
              <div className="relative" ref={claimRef}>
                <button
                  onClick={openClaim}
                  className="px-3 py-1.5 text-xs font-medium text-amber-400 border border-amber-700/50 rounded hover:bg-amber-900/30 transition-colors"
                >
                  Claim Character
                </button>

                {showClaim && (
                  <div className="absolute right-0 top-full mt-2 w-64 bg-stone-900 border border-stone-700 rounded-xl p-4 shadow-2xl z-50">
                    {claimStatus === 'success' ? (
                      <p className="text-green-400 text-sm text-center py-1">
                        ✓ Character claimed!
                      </p>
                    ) : (
                      <form onSubmit={handleClaim} className="space-y-3">
                        <p className="text-stone-300 text-xs font-medium">
                          Link a RS3 character to your account
                        </p>
                        <input
                          type="text"
                          value={claimInput}
                          onChange={(e) => setClaimInput(e.target.value)}
                          placeholder="Character name…"
                          autoFocus
                          className="w-full bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-1 focus:ring-amber-500/30 rounded-lg px-3 py-2 text-stone-100 placeholder-stone-500 text-sm outline-none transition-colors"
                        />
                        {claimError && (
                          <p className="text-red-400 text-xs">{claimError}</p>
                        )}
                        <div className="flex gap-2">
                          <button
                            type="submit"
                            disabled={claimStatus === 'loading' || !claimInput.trim()}
                            className="flex-1 bg-amber-600 hover:bg-amber-500 disabled:opacity-50 text-stone-950 font-bold py-1.5 rounded-lg text-xs transition-colors"
                          >
                            {claimStatus === 'loading' ? 'Claiming…' : 'Claim'}
                          </button>
                          <button
                            type="button"
                            onClick={() => setShowClaim(false)}
                            className="px-3 py-1.5 text-xs text-stone-400 hover:text-stone-200 transition-colors"
                          >
                            Cancel
                          </button>
                        </div>
                      </form>
                    )}
                  </div>
                )}
              </div>

              <button
                onClick={() => { logout(); navigate('/') }}
                className="px-3 py-1.5 text-xs font-medium text-stone-300 hover:text-stone-100 hover:bg-stone-800 rounded transition-colors"
              >
                Logout
              </button>
            </>
          ) : (
            <NavLink
              to="/login"
              className="px-4 py-1.5 text-sm font-medium bg-amber-600 hover:bg-amber-500 text-stone-950 rounded transition-colors"
            >
              Login
            </NavLink>
          )}
        </div>

        {/* Mobile hamburger */}
        <button
          className="md:hidden flex items-center justify-center w-10 h-10 text-stone-300 hover:text-amber-400 transition-colors"
          onClick={() => setMobileOpen((v) => !v)}
          aria-label="Toggle menu"
        >
          <span className="text-xl leading-none select-none">{mobileOpen ? '✕' : '☰'}</span>
        </button>
      </div>

      {/* Mobile dropdown */}
      {mobileOpen && (
        <div className="md:hidden bg-stone-900 border-t border-stone-800 px-3 py-3 space-y-1">
          <NavLink to="/" end className={mobileLinkClass} onClick={closeMobile}>Home</NavLink>
          <NavLink to="/archaeology" className={mobileLinkClass} onClick={closeMobile}>
            Archaeology Log
          </NavLink>

          <div className="border-t border-stone-700/60 pt-2 mt-2 space-y-1">
            {isAuthenticated ? (
              <>
                {user?.email && (
                  <p className="px-4 py-1 text-xs text-stone-500 truncate">{user.email}</p>
                )}

                {/* Mobile claim form */}
                {claimStatus === 'success' ? (
                  <p className="text-green-400 text-sm px-4 py-2">✓ Character claimed!</p>
                ) : (
                  <form onSubmit={handleClaim} className="space-y-2 px-1 pb-1">
                    <input
                      type="text"
                      value={claimInput}
                      onChange={(e) => setClaimInput(e.target.value)}
                      placeholder="Claim a character name…"
                      className="w-full bg-stone-800 border border-stone-600 focus:border-amber-500 rounded-lg px-3 py-2.5 text-stone-100 placeholder-stone-500 text-sm outline-none transition-colors min-h-[44px]"
                    />
                    {claimError && <p className="text-red-400 text-xs px-1">{claimError}</p>}
                    <button
                      type="submit"
                      disabled={claimStatus === 'loading' || !claimInput.trim()}
                      className="w-full bg-amber-600 hover:bg-amber-500 disabled:opacity-50 text-stone-950 font-bold py-2.5 rounded-lg text-sm transition-colors min-h-[44px]"
                    >
                      {claimStatus === 'loading' ? 'Claiming…' : 'Claim Character'}
                    </button>
                  </form>
                )}

                <button
                  onClick={() => { logout(); navigate('/'); closeMobile() }}
                  className="w-full flex items-center px-4 py-3 text-sm font-medium text-stone-300 hover:text-stone-100 hover:bg-stone-800 rounded-lg transition-colors min-h-[44px]"
                >
                  Logout
                </button>
              </>
            ) : (
              <NavLink
                to="/login"
                onClick={closeMobile}
                className="flex items-center justify-center px-4 py-3 text-sm font-medium bg-amber-600 hover:bg-amber-500 text-stone-950 rounded-lg transition-colors min-h-[44px]"
              >
                Login
              </NavLink>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}
