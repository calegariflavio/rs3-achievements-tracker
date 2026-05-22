import { useEffect, useState } from 'react'
import axios from 'axios'
import api from '../api/axiosConfig'
import { claimCharacter, getMe } from '../api/authApi'
import { useAuth } from '../context/AuthContext'
import { COLLECTOR_GROUPS } from '../data/archaeologyData'

type StatusFilter = 'all' | 'completed' | 'in-progress' | 'not-started'

const LEVEL_BRACKETS = [
  { label: '1–50', min: 1, max: 50 },
  { label: '51–75', min: 51, max: 75 },
  { label: '76–90', min: 76, max: 90 },
  { label: '91–100', min: 91, max: 100 },
  { label: '101–110', min: 101, max: 110 },
  { label: '111–120', min: 111, max: 120 },
]

export default function ArchaeologyPage() {
  const { token, refreshUser } = useAuth()

  const [inputUsername, setInputUsername] = useState('')
  const [loadedUsername, setLoadedUsername] = useState('')
  const [collected, setCollected] = useState<Set<string>>(new Set())
  const [isOwner, setIsOwner] = useState(false)
  const [fetchLoading, setFetchLoading] = useState(false)
  const [fetchError, setFetchError] = useState<string | null>(null)
  const [pending, setPending] = useState<Set<string>>(new Set())
  const [claimLoading, setClaimLoading] = useState(false)
  const [claimError, setClaimError] = useState<string | null>(null)
  const [claimedCharacters, setClaimedCharacters] = useState<string[]>([])

  // Filters
  const [search, setSearch] = useState('')
  const [collectorFilter, setCollectorFilter] = useState('')
  const [levelFilter, setLevelFilter] = useState('')
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('all')

  const sortedGroups = [...COLLECTOR_GROUPS].sort(
    (a, b) =>
      Math.min(...a.collections.map((c) => c.requiredLevel)) -
      Math.min(...b.collections.map((c) => c.requiredLevel)),
  )

  const totalArtefacts = COLLECTOR_GROUPS.reduce(
    (sum, g) => sum + g.collections.reduce((s, col) => s + col.artefacts.length, 0),
    0,
  )

  const authHeader = token ? { Authorization: `Bearer ${token}` } : {}

  const filtersActive =
    search.trim() !== '' || collectorFilter !== '' || levelFilter !== '' || statusFilter !== 'all'

  const activeLevelBracket = LEVEL_BRACKETS.find((b) => b.label === levelFilter)

  // Apply filters and compute visible groups/collections
  const filteredGroups = sortedGroups
    .map((group) => {
      if (collectorFilter && group.name !== collectorFilter) return null

      const matchingCollections = group.collections.filter((col) => {
        if (activeLevelBracket) {
          if (col.requiredLevel < activeLevelBracket.min || col.requiredLevel > activeLevelBracket.max)
            return false
        }

        const colCollected = col.artefacts.filter((a) => collected.has(a.name)).length
        if (statusFilter === 'completed' && colCollected !== col.artefacts.length) return false
        if (statusFilter === 'not-started' && colCollected !== 0) return false
        if (statusFilter === 'in-progress' && (colCollected === 0 || colCollected === col.artefacts.length))
          return false

        if (search.trim()) {
          const q = search.trim().toLowerCase()
          const hasMatch = col.artefacts.some((a) => a.name.toLowerCase().includes(q))
          if (!hasMatch) return false
        }

        return true
      })

      if (matchingCollections.length === 0) return null
      return { ...group, collections: matchingCollections }
    })
    .filter(Boolean) as typeof sortedGroups

  const visibleCollectionCount = filteredGroups.reduce((s, g) => s + g.collections.length, 0)

  function clearFilters() {
    setSearch('')
    setCollectorFilter('')
    setLevelFilter('')
    setStatusFilter('all')
  }

  useEffect(() => {
    document.title = 'Archaeology Log | RS3 Tracker'
  }, [])

  useEffect(() => {
    if (!token) return
    getMe(token).then((res) => {
      setClaimedCharacters((res.data.claimedCharacters ?? []).map((c) => c.toLowerCase()))
    })
  }, [token])

  function loadCharacter(name: string) {
    if (!name) return
    setFetchLoading(true)
    setFetchError(null)
    setClaimError(null)
    setIsOwner(false)
    const normalizedUsername = name.toLowerCase()

    const loadArtefacts = api.get<string[]>(
      `/api/archaeology/${encodeURIComponent(normalizedUsername)}`,
      { headers: authHeader },
    )
    const loadOwnership = token
      ? getMe(token).then((res) => {
          const claimed = (res.data.claimedCharacters ?? []).map((c) => c.toLowerCase())
          setIsOwner(claimed.includes(normalizedUsername))
        })
      : Promise.resolve()

    Promise.all([loadArtefacts, loadOwnership])
      .then(([res]) => {
        setCollected(new Set(res.data))
        setLoadedUsername(normalizedUsername)
      })
      .catch(() => setFetchError('Failed to load archaeology data. Check the character name.'))
      .finally(() => setFetchLoading(false))
  }

  function handleLoad(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    loadCharacter(inputUsername.trim())
  }

  async function toggleArtefact(
    artefactName: string,
    collectionName: string,
    collectorName: string,
  ) {
    if (!loadedUsername || !isOwner || pending.has(artefactName)) return
    const wasCollected = collected.has(artefactName)

    setPending((prev) => new Set(prev).add(artefactName))
    setCollected((prev) => {
      const next = new Set(prev)
      if (wasCollected) next.delete(artefactName)
      else next.add(artefactName)
      return next
    })

    try {
      if (wasCollected) {
        await api.delete(
          `/api/archaeology/${encodeURIComponent(loadedUsername)}/artifact`,
          { data: { artifactName: artefactName }, headers: authHeader },
        )
      } else {
        await api.post(
          `/api/archaeology/${encodeURIComponent(loadedUsername)}/artifact`,
          { artifactName: artefactName, collectionName, digSite: collectorName },
          { headers: authHeader },
        )
      }
    } catch {
      setCollected((prev) => {
        const next = new Set(prev)
        if (wasCollected) next.add(artefactName)
        else next.delete(artefactName)
        return next
      })
    } finally {
      setPending((prev) => {
        const next = new Set(prev)
        next.delete(artefactName)
        return next
      })
    }
  }

  async function handleClaim() {
    if (!loadedUsername || !token) return
    setClaimLoading(true)
    setClaimError(null)
    try {
      await claimCharacter(loadedUsername, token)
      const res = await getMe(token)
      setClaimedCharacters((res.data.claimedCharacters ?? []).map((c) => c.toLowerCase()))
      await refreshUser()
      setIsOwner(true)
    } catch (err) {
      setClaimError(
        axios.isAxiosError(err)
          ? (err.response?.data?.message ?? 'Failed to claim character.')
          : 'Failed to claim character.',
      )
    } finally {
      setClaimLoading(false)
    }
  }

  const selectClass =
    'w-full md:w-auto bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-1 focus:ring-amber-500/30 rounded-lg px-3 py-2.5 text-stone-300 text-sm outline-none transition-all appearance-none min-h-[44px]'

  return (
    <main className="flex-1 bg-stone-950 text-stone-100 px-4 py-8">
      <div className="max-w-5xl mx-auto space-y-8">
        {/* Header */}
        <div>
          <h1 className="text-3xl font-bold text-amber-400 mb-1">Archaeology Collection Log</h1>
          <p className="text-stone-400 text-sm">
            Track artefacts collected from all RS3 collectors.
          </p>
        </div>

        {/* Claimed character chips */}
        {token && (
          <div>
            <p className="text-stone-400 text-xs mb-2">Your characters:</p>
            {claimedCharacters.length === 0 ? (
              <p className="text-stone-600 text-xs">
                No characters claimed yet. Load a character below and click "Claim this character".
              </p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {claimedCharacters.map((char) => {
                  const isActive = loadedUsername === char
                  return (
                    <button
                      key={char}
                      type="button"
                      onClick={() => loadCharacter(char)}
                      className={`px-3 py-1 rounded-full text-sm font-medium border transition-colors ${
                        isActive
                          ? 'bg-amber-500 text-stone-950 border-amber-500'
                          : 'bg-stone-900 text-amber-400 border-amber-700/50 hover:bg-amber-900/30'
                      }`}
                    >
                      {char}
                    </button>
                  )
                })}
              </div>
            )}
          </div>
        )}

        {/* Username input */}
        <form onSubmit={handleLoad} className="flex gap-3 max-w-lg">
          <input
            type="text"
            value={inputUsername}
            onChange={(e) => setInputUsername(e.target.value)}
            placeholder="Enter character name…"
            className="flex-1 bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-2 focus:ring-amber-500/30 rounded-lg px-4 py-2.5 text-stone-100 placeholder-stone-500 text-sm outline-none transition-all"
          />
          <button
            type="submit"
            disabled={fetchLoading}
            className="bg-amber-600 hover:bg-amber-500 disabled:opacity-50 text-stone-950 font-bold px-7 py-2.5 rounded-lg transition-colors text-sm"
          >
            {fetchLoading ? 'Loading…' : 'Load'}
          </button>
        </form>

        {fetchError && <p className="text-red-400 text-sm -mt-4">{fetchError}</p>}

        {/* Collection summary */}
        {loadedUsername && (
          <div className="bg-stone-900 border border-amber-900/40 rounded-xl px-5 py-4 flex items-center justify-between gap-4">
            <div>
              <p className="text-stone-500 text-xs mb-0.5">Loaded for</p>
              <p className="text-amber-400 font-semibold">{loadedUsername}</p>
            </div>
            <div className="text-right">
              <p className="text-stone-500 text-xs mb-0.5">Artefacts collected</p>
              <p className="text-amber-400 font-semibold">
                {collected.size}{' '}
                <span className="text-stone-500 font-normal">/ {totalArtefacts}</span>
              </p>
            </div>
          </div>
        )}

        {/* Ownership banner */}
        {loadedUsername && !isOwner && (
          <div className="bg-stone-900 border border-amber-700/30 rounded-xl px-5 py-4">
            <div className="flex items-center justify-between gap-4 flex-wrap">
              <div>
                <p className="text-amber-400 text-sm font-medium">🔒 Read-only</p>
                <p className="text-stone-400 text-xs mt-0.5">
                  This character is not linked to your account. Claim it to track artefacts.
                </p>
              </div>
              <button
                onClick={handleClaim}
                disabled={claimLoading}
                className="shrink-0 bg-amber-600 hover:bg-amber-500 disabled:opacity-50 text-stone-950 font-bold px-4 py-2 rounded-lg text-xs transition-colors"
              >
                {claimLoading ? 'Claiming…' : 'Claim this character'}
              </button>
            </div>
            {claimError && <p className="text-red-400 text-xs mt-2">{claimError}</p>}
          </div>
        )}

        {/* Filter bar */}
        <div className="bg-stone-900 border border-stone-700 rounded-xl p-4 space-y-3">
          <div className="flex flex-col md:flex-row md:flex-wrap gap-3">
            {/* Search */}
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search artefacts…"
              className="w-full md:flex-1 md:min-w-40 bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-1 focus:ring-amber-500/30 rounded-lg px-3 py-2.5 text-stone-100 placeholder-stone-500 text-sm outline-none transition-all min-h-[44px]"
            />

            {/* Collector */}
            <select
              value={collectorFilter}
              onChange={(e) => setCollectorFilter(e.target.value)}
              className={selectClass}
            >
              <option value="">All Collectors</option>
              {sortedGroups.map((g) => (
                <option key={g.name} value={g.name}>{g.name}</option>
              ))}
            </select>

            {/* Level range */}
            <select
              value={levelFilter}
              onChange={(e) => setLevelFilter(e.target.value)}
              className={selectClass}
            >
              <option value="">All Levels</option>
              {LEVEL_BRACKETS.map((b) => (
                <option key={b.label} value={b.label}>{b.label}</option>
              ))}
            </select>

            {/* Status */}
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as StatusFilter)}
              className={selectClass}
            >
              <option value="all">All Status</option>
              <option value="completed">Completed</option>
              <option value="in-progress">In Progress</option>
              <option value="not-started">Not Started</option>
            </select>

            {/* Clear */}
            {filtersActive && (
              <button
                type="button"
                onClick={clearFilters}
                className="w-full md:w-auto px-3 py-2.5 text-xs font-medium text-stone-400 hover:text-stone-200 border border-stone-600 hover:border-stone-500 rounded-lg transition-colors min-h-[44px]"
              >
                Clear filters
              </button>
            )}
          </div>

          {filtersActive && (
            <p className="text-stone-500 text-xs">
              {visibleCollectionCount} collection{visibleCollectionCount !== 1 ? 's' : ''} shown
            </p>
          )}
        </div>

        {/* Collector groups */}
        {filteredGroups.map((group) => {
          const groupTotal = group.collections.reduce((s, col) => s + col.artefacts.length, 0)
          const groupCollected = group.collections.reduce(
            (s, col) => s + col.artefacts.filter((a) => collected.has(a.name)).length,
            0,
          )
          const groupComplete = groupCollected === groupTotal
          const minLevel = Math.min(...group.collections.map((c) => c.requiredLevel))

          return (
            <section key={group.name}>
              <div className="flex items-center gap-3 mb-3">
                <h2 className="text-lg font-semibold text-amber-400">
                  <a
                    href={`https://runescape.wiki/w/${group.name.trim().replace(/ /g, '_')}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="hover:text-amber-300 transition-colors"
                  >
                    {group.name}
                  </a>
                </h2>
                <span className="text-xs text-stone-600 font-medium">Lv. {minLevel}+</span>
                <span
                  className={`ml-auto text-xs px-2.5 py-0.5 rounded-full border font-medium ${
                    groupComplete
                      ? 'bg-green-900/40 text-green-400 border-green-700/50'
                      : 'bg-stone-800 text-stone-400 border-stone-600'
                  }`}
                >
                  {groupCollected} / {groupTotal}
                </span>
              </div>

              <div className="space-y-4">
                {group.collections.map((collection) => {
                  const colTotal = collection.artefacts.length
                  const colCollected = collection.artefacts.filter((a) =>
                    collected.has(a.name),
                  ).length
                  const colComplete = colCollected === colTotal

                  // When searching, only render matching artefacts
                  const visibleArtefacts =
                    search.trim()
                      ? collection.artefacts.filter((a) =>
                          a.name.toLowerCase().includes(search.trim().toLowerCase()),
                        )
                      : collection.artefacts

                  return (
                    <div
                      key={collection.name}
                      className="bg-stone-900 border border-stone-700 rounded-xl p-4"
                    >
                      <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center gap-2">
                          <h3 className="text-stone-200 font-medium text-sm">{collection.name}</h3>
                          <span className="text-stone-600 text-xs">Lv. {collection.requiredLevel}</span>
                        </div>
                        <span
                          className={`text-xs px-2 py-0.5 rounded-full border ${
                            colComplete
                              ? 'bg-green-900/40 text-green-400 border-green-700/50'
                              : 'bg-stone-800 text-stone-400 border-stone-600'
                          }`}
                        >
                          {colCollected} / {colTotal}
                        </span>
                      </div>

                      <div className="h-1 bg-stone-800 rounded-full mb-3 overflow-hidden">
                        <div
                          className="h-full bg-amber-500 rounded-full transition-all duration-300"
                          style={{ width: `${(colCollected / colTotal) * 100}%` }}
                        />
                      </div>

                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-1.5">
                        {visibleArtefacts.map((artefact) => {
                          const isCollected = collected.has(artefact.name)
                          const isPending = pending.has(artefact.name)
                          const editable = loadedUsername && isOwner && !isPending
                          const isDisabled = !editable

                          return (
                            <div
                              key={artefact.name}
                              className={`flex items-center gap-2 px-3 py-2 rounded-lg border transition-colors ${
                                isCollected
                                  ? 'bg-amber-950/40 border-amber-700/40'
                                  : 'bg-stone-800 border-stone-700'
                              }`}
                            >
                              <label
                                className={`flex items-center gap-2 flex-1 min-w-0 ${
                                  isDisabled ? 'cursor-not-allowed opacity-60' : 'cursor-pointer'
                                }`}
                              >
                                <input
                                  type="checkbox"
                                  checked={isCollected}
                                  disabled={isDisabled}
                                  onChange={() =>
                                    toggleArtefact(artefact.name, collection.name, group.name)
                                  }
                                  className="accent-amber-500 w-4 h-4 shrink-0"
                                />
                                <span
                                  className={`text-sm truncate ${
                                    isCollected ? 'text-amber-300' : 'text-stone-400'
                                  }`}
                                >
                                  {artefact.name}
                                </span>
                              </label>
                              <div className="flex items-center gap-1 shrink-0">
                                {isCollected && isOwner && (
                                  <span className="text-green-400 text-xs">✓</span>
                                )}
                                {!isOwner && loadedUsername && (
                                  <span className="text-stone-600 text-xs">🔒</span>
                                )}
                                <a
                                  href={artefact.wikiUrl}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  onClick={(e) => e.stopPropagation()}
                                  className="text-stone-600 hover:text-amber-400 transition-colors text-xs"
                                  title="View on wiki"
                                >
                                  🔗
                                </a>
                              </div>
                            </div>
                          )
                        })}
                      </div>
                    </div>
                  )
                })}
              </div>
            </section>
          )
        })}

        {filtersActive && filteredGroups.length === 0 && (
          <p className="text-stone-500 text-sm text-center py-8">
            No collections match the current filters.
          </p>
        )}
      </div>
    </main>
  )
}
