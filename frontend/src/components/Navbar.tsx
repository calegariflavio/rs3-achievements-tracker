import { useState } from 'react'
import { NavLink } from 'react-router-dom'

export default function Navbar() {
  const [mobileOpen, setMobileOpen] = useState(false)

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
      <div className="max-w-6xl mx-auto px-4 flex items-center justify-between h-14">
        <NavLink
          to="/"
          onClick={() => setMobileOpen(false)}
          className="flex items-center gap-2 group shrink-0"
        >
          <span className="text-amber-500 text-xl leading-none">⚔</span>
          <span className="text-amber-400 font-bold text-base tracking-wide group-hover:text-amber-300 transition-colors">
            RS3 Tracker
          </span>
        </NavLink>

        <div className="hidden md:flex items-center gap-1">
          <NavLink to="/" end className={linkClass}>Home</NavLink>
          <NavLink to="/archaeology" className={linkClass}>Archaeology Log</NavLink>
        </div>

        <button
          className="md:hidden flex items-center justify-center w-10 h-10 text-stone-300 hover:text-amber-400 transition-colors"
          onClick={() => setMobileOpen((v) => !v)}
          aria-label="Toggle menu"
        >
          <span className="text-xl leading-none select-none">{mobileOpen ? '✕' : '☰'}</span>
        </button>
      </div>

      {mobileOpen && (
        <div className="md:hidden bg-stone-900 border-t border-stone-800 px-3 py-3 space-y-1">
          <NavLink to="/" end className={mobileLinkClass} onClick={() => setMobileOpen(false)}>
            Home
          </NavLink>
          <NavLink
            to="/archaeology"
            className={mobileLinkClass}
            onClick={() => setMobileOpen(false)}
          >
            Archaeology Log
          </NavLink>
        </div>
      )}
    </nav>
  )
}
