import { useEffect } from 'react'
import { Link } from 'react-router-dom'

export default function NotFoundPage() {
  useEffect(() => {
    document.title = '404 | RS3 Tracker'
  }, [])

  return (
    <main className="flex-1 bg-stone-950 flex items-center justify-center px-4">
      <div className="text-center">
        <p className="text-amber-500 text-8xl font-bold tracking-tight mb-4">404</p>
        <h1 className="text-stone-100 text-2xl font-semibold mb-2">Page not found</h1>
        <p className="text-stone-400 text-base mb-8">
          This page doesn't exist in Gielinor.
        </p>
        <Link
          to="/"
          className="inline-block bg-amber-600 hover:bg-amber-500 text-stone-950 font-bold px-6 py-3 rounded-lg transition-colors"
        >
          Return to Home
        </Link>
      </div>
    </main>
  )
}
