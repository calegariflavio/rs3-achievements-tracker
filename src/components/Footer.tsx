export default function Footer() {
  return (
    <footer className="bg-stone-950 border-t border-stone-800 py-4 px-4 text-center">
      <p className="text-stone-500 text-xs leading-relaxed max-w-2xl mx-auto">
        RS3 Achievement Tracker is a fan site and is not affiliated with Jagex or RuneScape.
        Game data sourced from{' '}
        <a
          href="https://runescape.wiki"
          target="_blank"
          rel="noopener noreferrer"
          className="text-amber-600 hover:text-amber-400 transition-colors"
        >
          runescape.wiki
        </a>
        .
      </p>
    </footer>
  )
}
