import { renderHook, act } from '@testing-library/react'
import { describe, test, expect, beforeEach } from 'vitest'
import { useRecentCharacters } from './useRecentCharacters'

const STORAGE_KEY = 'rs3tracker_recent'

beforeEach(() => {
  localStorage.clear()
})

describe('useRecentCharacters', () => {
  test('starts empty when localStorage is empty', () => {
    const { result } = renderHook(() => useRecentCharacters())
    expect(result.current.recent).toEqual([])
  })

  test('add prepends a name to the list', () => {
    const { result } = renderHook(() => useRecentCharacters())
    act(() => result.current.add('Zezima'))
    expect(result.current.recent).toEqual(['Zezima'])
  })

  test('add deduplicates case-insensitively and moves to front', () => {
    const { result } = renderHook(() => useRecentCharacters())
    act(() => result.current.add('Zezima'))
    act(() => result.current.add('PlayerA'))
    act(() => result.current.add('zezima'))
    expect(result.current.recent[0]).toBe('zezima')
    expect(result.current.recent).toHaveLength(2)
  })

  test('add caps list at 6 entries', () => {
    const { result } = renderHook(() => useRecentCharacters())
    for (let i = 1; i <= 7; i++) {
      act(() => result.current.add(`Player${i}`))
    }
    expect(result.current.recent).toHaveLength(6)
  })

  test('remove deletes the matching name', () => {
    const { result } = renderHook(() => useRecentCharacters())
    act(() => result.current.add('Zezima'))
    act(() => result.current.remove('Zezima'))
    expect(result.current.recent).toEqual([])
  })

  test('remove is case-insensitive', () => {
    const { result } = renderHook(() => useRecentCharacters())
    act(() => result.current.add('Zezima'))
    act(() => result.current.remove('ZEZIMA'))
    expect(result.current.recent).toEqual([])
  })

  test('persists to localStorage on add', () => {
    const { result } = renderHook(() => useRecentCharacters())
    act(() => result.current.add('Zezima'))
    const stored = JSON.parse(localStorage.getItem(STORAGE_KEY)!)
    expect(stored).toEqual(['Zezima'])
  })

  test('updates localStorage on remove', () => {
    const { result } = renderHook(() => useRecentCharacters())
    act(() => result.current.add('Zezima'))
    act(() => result.current.add('Bob'))
    act(() => result.current.remove('Zezima'))
    const stored = JSON.parse(localStorage.getItem(STORAGE_KEY)!)
    expect(stored).toEqual(['Bob'])
  })

  test('loads existing entries from localStorage on init', () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(['Zezima', 'Bob']))
    const { result } = renderHook(() => useRecentCharacters())
    expect(result.current.recent).toEqual(['Zezima', 'Bob'])
  })
})
