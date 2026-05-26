import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useRecentCharacters } from '../hooks/useRecentCharacters'

const FEATURES = [
  {
    icon: '📊',
    title: 'Player Stats',
    desc: 'View all 28 RS3 skills with levels and XP across every combat and non-combat style.',
  },
  {
    icon: '📜',
    title: 'Quest Log',
    desc: 'Track completed, in-progress, and remaining quests — with requirement checking per character.',
  },
]

export default function HomePage() {
  const [username, setUsername] = useState('')
  const navigate = useNavigate()
  const { recent, remove } = useRecentCharacters()

  useEffect(() => {
    document.title = 'RS3 Achievement Tracker'
  }, [])

  function handleSearch(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const trimmed = username.trim()
    if (trimmed) navigate(`/player/${encodeURIComponent(trimmed)}`)
  }

  return (
    <main className="flex-1 bg-gradient-to-b from-stone-900 to-stone-950 rs3-bg">
      {/* Hero */}
      <div className="flex flex-col items-center justify-center min-h-[55vh] px-4 pt-16 pb-10">
        <div className="text-center mb-10">
          <div className="flex items-center justify-center gap-3 mb-4">
            <span className="text-amber-700/60 text-lg select-none">✦━━</span>
            <span className="text-amber-500 text-3xl drop-shadow-[0_0_12px_rgba(217,119,6,0.5)]">⚔</span>
            <span className="text-amber-700/60 text-lg select-none">━━✦</span>
          </div>
          <h1 className="text-3xl sm:text-5xl font-bold text-amber-400 tracking-wide mb-3 drop-shadow-[0_0_20px_rgba(217,119,6,0.25)]">
            RS3 Achievement Tracker
          </h1>
          <p className="text-stone-400 text-lg max-w-md mx-auto">
            Search any RuneScape 3 player to view their stats, quests, and achievements.
          </p>
        </div>

        <form onSubmit={handleSearch} className="w-full max-w-xl flex gap-3">
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Enter character name…"
            autoFocus
            className="flex-1 bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-2 focus:ring-amber-500/30 rounded-lg px-5 py-3.5 text-stone-100 placeholder-stone-500 text-base outline-none transition-all"
          />
          <button
            type="submit"
            className="bg-amber-600 hover:bg-amber-500 active:bg-amber-700 text-stone-950 font-bold px-8 py-3.5 rounded-lg transition-colors text-base shadow-lg shadow-amber-900/30"
          >
            Search
          </button>
        </form>

        {/* Recent characters */}
        {recent.length > 0 && (
          <div className="w-full max-w-xl mt-5" data-testid="recent-characters">
            <p className="text-stone-500 text-xs mb-2 uppercase tracking-wide">Recent</p>
            <div className="flex flex-wrap gap-2">
              {recent.map((name) => (
                <div key={name} className="flex items-center">
                  <button
                    onClick={() => navigate(`/player/${encodeURIComponent(name)}`)}
                    className="bg-stone-800 hover:bg-amber-900/40 text-stone-300 hover:text-amber-300 text-sm px-3 py-1.5 rounded-l-lg border border-stone-700 hover:border-amber-700/50 transition-colors"
                  >
                    {name}
                  </button>
                  <button
                    onClick={() => remove(name)}
                    aria-label={`Remove ${name} from recent`}
                    className="bg-stone-800 hover:bg-red-900/30 text-stone-600 hover:text-red-400 text-xs px-1.5 py-1.5 rounded-r-lg border border-l-0 border-stone-700 hover:border-red-800/50 transition-colors leading-none"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Feature cards */}
      <div className="max-w-4xl mx-auto px-4 pb-16 space-y-5">
        <div className="flex items-center gap-3 mb-2">
          <span className="text-amber-900/70 text-xs select-none">✦━━━━━</span>
          <span className="text-stone-500 text-xs uppercase tracking-widest">Features</span>
          <span className="flex-1 h-px bg-gradient-to-r from-amber-900/30 to-transparent" />
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          {FEATURES.map((card) => (
            <div
              key={card.title}
              className="relative bg-stone-900 border border-stone-700 rounded-xl p-6 text-center rs3-panel rs3-corners"
            >
              <div className="text-3xl mb-2">{card.icon}</div>
              <h3 className="text-amber-400 font-semibold text-lg mb-2">{card.title}</h3>
              <p className="text-stone-400 text-sm leading-relaxed">{card.desc}</p>
            </div>
          ))}
        </div>

        {/* Archaeology Log — standalone CTA */}
        <Link
          to="/archaeology"
          className="group relative flex items-center gap-5 bg-stone-900 border border-amber-800/50 hover:border-amber-500 hover:bg-amber-900/10 rounded-xl p-6 transition-colors rs3-panel rs3-corners"
        >
          <span className="text-5xl shrink-0">🏺</span>
          <div className="flex-1 min-w-0">
            <h3 className="text-amber-400 font-semibold text-lg mb-1">Archaeology Log</h3>
            <p className="text-stone-400 text-sm leading-relaxed">
              Mark artefacts collected from all seven dig sites and track your collection progress — saved locally in your browser.
            </p>
          </div>
          <span className="shrink-0 text-amber-600 group-hover:text-amber-400 transition-colors text-2xl leading-none">→</span>
        </Link>
      </div>
    </main>
  )
}
