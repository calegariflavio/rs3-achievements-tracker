import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, test, expect, beforeEach, vi } from 'vitest'
import HomePage from './HomePage'

const mockNavigate = vi.fn()

vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>()
  return { ...actual, useNavigate: () => mockNavigate }
})

beforeEach(() => {
  localStorage.clear()
  mockNavigate.mockClear()
})

function renderPage() {
  return render(
    <MemoryRouter>
      <HomePage />
    </MemoryRouter>
  )
}

describe('HomePage', () => {
  test('renders search input and button', () => {
    renderPage()
    expect(screen.getByPlaceholderText('Enter character name…')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument()
  })

  test('does not show recent section when localStorage is empty', () => {
    renderPage()
    expect(screen.queryByTestId('recent-characters')).not.toBeInTheDocument()
  })

  test('shows recent characters loaded from localStorage', () => {
    localStorage.setItem('rs3tracker_recent', JSON.stringify(['Zezima', 'Bob']))
    renderPage()
    expect(screen.getByTestId('recent-characters')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Zezima' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Bob' })).toBeInTheDocument()
  })

  test('clicking a recent character navigates to its player page', () => {
    localStorage.setItem('rs3tracker_recent', JSON.stringify(['Zezima']))
    renderPage()
    fireEvent.click(screen.getByRole('button', { name: 'Zezima' }))
    expect(mockNavigate).toHaveBeenCalledWith('/player/Zezima')
  })

  test('remove button removes a character from the recent list', () => {
    localStorage.setItem('rs3tracker_recent', JSON.stringify(['Zezima']))
    renderPage()
    fireEvent.click(screen.getByRole('button', { name: 'Remove Zezima from recent' }))
    expect(screen.queryByTestId('recent-characters')).not.toBeInTheDocument()
  })

  test('submitting the form navigates to the player page', () => {
    renderPage()
    const input = screen.getByPlaceholderText('Enter character name…')
    fireEvent.change(input, { target: { value: 'Zezima' } })
    fireEvent.submit(input.closest('form')!)
    expect(mockNavigate).toHaveBeenCalledWith('/player/Zezima')
  })

  test('submitting an empty form does not navigate', () => {
    renderPage()
    const input = screen.getByPlaceholderText('Enter character name…')
    fireEvent.submit(input.closest('form')!)
    expect(mockNavigate).not.toHaveBeenCalled()
  })
})
