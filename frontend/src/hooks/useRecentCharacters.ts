import { useState, useCallback } from 'react'

const STORAGE_KEY = 'rs3tracker_recent'
const MAX_RECENT = 6

function loadFromStorage(): string[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return []
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export function useRecentCharacters() {
  const [recent, setRecent] = useState<string[]>(loadFromStorage)

  const add = useCallback((name: string) => {
    setRecent((prev) => {
      const deduped = [name, ...prev.filter((n) => n.toLowerCase() !== name.toLowerCase())]
      const next = deduped.slice(0, MAX_RECENT)
      try { localStorage.setItem(STORAGE_KEY, JSON.stringify(next)) } catch { /* non-fatal */ }
      return next
    })
  }, [])

  const remove = useCallback((name: string) => {
    setRecent((prev) => {
      const next = prev.filter((n) => n.toLowerCase() !== name.toLowerCase())
      try { localStorage.setItem(STORAGE_KEY, JSON.stringify(next)) } catch { /* non-fatal */ }
      return next
    })
  }, [])

  return { recent, add, remove }
}
