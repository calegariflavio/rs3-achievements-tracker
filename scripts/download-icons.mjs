import { writeFileSync, mkdirSync } from 'fs'
import { join } from 'path'

const icons = [
  ['Attack',        'https://runescape.wiki/images/Attack-icon.png'],
  ['Strength',      'https://runescape.wiki/images/Strength-icon.png'],
  ['Defence',       'https://runescape.wiki/images/Defence.png'],
  ['Constitution',  'https://runescape.wiki/images/Constitution.png'],
  ['Ranged',        'https://runescape.wiki/images/Ranged-icon.png'],
  ['Prayer',        'https://runescape.wiki/images/Prayer_icon.png'],
  ['Magic',         'https://runescape.wiki/images/Magic.png'],
  ['Cooking',       'https://runescape.wiki/images/Cooking-icon.png'],
  ['Woodcutting',   'https://runescape.wiki/images/Woodcutting-icon.png'],
  ['Fletching',     'https://runescape.wiki/images/Fletching.png'],
  ['Fishing',       'https://runescape.wiki/images/Fishing-icon.png'],
  ['Firemaking',    'https://runescape.wiki/images/Firemaking.png'],
  ['Crafting',      'https://runescape.wiki/images/Crafting.png'],
  ['Smithing',      'https://runescape.wiki/images/Smithing-icon.png'],
  ['Mining',        'https://runescape.wiki/images/Mining.png'],
  ['Herblore',      'https://runescape.wiki/images/Herblore-icon.png'],
  ['Agility',       'https://runescape.wiki/images/Agility.png'],
  ['Thieving',      'https://runescape.wiki/images/Thieving-icon.png'],
  ['Slayer',        'https://runescape.wiki/images/Slayer-icon.png'],
  ['Farming',       'https://runescape.wiki/images/Farming.png'],
  ['Runecrafting',  'https://runescape.wiki/images/Runecrafting-icon.png'],
  ['Hunter',        'https://runescape.wiki/images/Hunter.png'],
  ['Construction',  'https://runescape.wiki/images/Construction.png'],
  ['Summoning',     'https://runescape.wiki/images/Summoning_icon.png'],
  ['Dungeoneering', 'https://runescape.wiki/images/Dungeoneering.png'],
  ['Divination',    'https://runescape.wiki/images/Divination-icon.png'],
  ['Invention',     'https://runescape.wiki/images/Invention-icon.png'],
  ['Archaeology',   'https://runescape.wiki/images/Archaeology.png'],
  ['Necromancy',    'https://runescape.wiki/images/Necromancy-icon.png'],
]

mkdirSync('src/assets/skills', { recursive: true })

for (const [skill, url] of icons) {
  const res = await fetch(url, {
    headers: { 'User-Agent': 'rs3-tracker/1.0 (local dev icon downloader)' }
  })
  if (res.ok) {
    const buffer = await res.arrayBuffer()
    writeFileSync(join('src/assets/skills', `${skill}.png`), Buffer.from(buffer))
    console.log(`✓ ${skill}`)
  } else {
    console.log(`✗ ${skill} - ${res.status}`)
  }
}
