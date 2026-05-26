import { useEffect, useState } from 'react'
import { QUEST_REQUIREMENTS } from '../data/questRequirements'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../api/axiosConfig'
import { useRecentCharacters } from '../hooks/useRecentCharacters'
import AttackIcon from '../assets/skills/Attack.png'
import StrengthIcon from '../assets/skills/Strength.png'
import DefenceIcon from '../assets/skills/Defence.png'
import ConstitutionIcon from '../assets/skills/Constitution.png'
import RangedIcon from '../assets/skills/Ranged.png'
import PrayerIcon from '../assets/skills/Prayer.png'
import MagicIcon from '../assets/skills/Magic.png'
import CookingIcon from '../assets/skills/Cooking.png'
import WoodcuttingIcon from '../assets/skills/Woodcutting.png'
import FletchingIcon from '../assets/skills/Fletching.png'
import FishingIcon from '../assets/skills/Fishing.png'
import FiremakingIcon from '../assets/skills/Firemaking.png'
import CraftingIcon from '../assets/skills/Crafting.png'
import SmithingIcon from '../assets/skills/Smithing.png'
import MiningIcon from '../assets/skills/Mining.png'
import HerbloreIcon from '../assets/skills/Herblore.png'
import AgilityIcon from '../assets/skills/Agility.png'
import ThievingIcon from '../assets/skills/Thieving.png'
import SlayerIcon from '../assets/skills/Slayer.png'
import FarmingIcon from '../assets/skills/Farming.png'
import RunecraftingIcon from '../assets/skills/Runecrafting.png'
import HunterIcon from '../assets/skills/Hunter.png'
import ConstructionIcon from '../assets/skills/Construction.png'
import SummoningIcon from '../assets/skills/Summoning.png'
import DungeoneeringIcon from '../assets/skills/Dungeoneering.png'
import DivinationIcon from '../assets/skills/Divination.png'
import InventionIcon from '../assets/skills/Invention.png'
import ArchaeologyIcon from '../assets/skills/Archaeology.png'
import NecromancyIcon from '../assets/skills/Necromancy.png'

interface Skill {
  id: number
  level: number
  xp: number
  rank: number
}

interface Activity {
  text: string
  date?: string
}

interface Quest {
  title: string
  status: 'COMPLETED' | 'STARTED' | 'NOT_STARTED'
  difficulty?: number
  questPoints?: number
}

interface XpDataPoint {
  date: string  // "YYYY-MM-DD"
  xp: number    // cumulative XP at this snapshot
}

interface SkillXpGained {
  skillId: number
  skillName: string
  dataPoints: XpDataPoint[]
  xpGained: number
}

interface PlayerData {
  username: string
  skills?: Skill[]
  recentActivity?: Activity[]
  quests?: Quest[]
}

function skillLevelClass(level: number): string {
  if (level >= 120) return 'text-purple-400 font-bold'
  if (level >= 99) return 'text-amber-400 font-bold'
  if (level >= 90) return 'text-amber-300'
  if (level >= 70) return 'text-yellow-400'
  if (level >= 50) return 'text-green-400'
  return 'text-stone-300'
}

function formatXP(xp: number): string {
  return xp.toLocaleString()
}

const SKILL_ICON_MAP: Record<string, string> = {
  'Attack':        AttackIcon,
  'Strength':      StrengthIcon,
  'Defence':       DefenceIcon,
  'Constitution':  ConstitutionIcon,
  'Ranged':        RangedIcon,
  'Prayer':        PrayerIcon,
  'Magic':         MagicIcon,
  'Cooking':       CookingIcon,
  'Woodcutting':   WoodcuttingIcon,
  'Fletching':     FletchingIcon,
  'Fishing':       FishingIcon,
  'Firemaking':    FiremakingIcon,
  'Crafting':      CraftingIcon,
  'Smithing':      SmithingIcon,
  'Mining':        MiningIcon,
  'Herblore':      HerbloreIcon,
  'Agility':       AgilityIcon,
  'Thieving':      ThievingIcon,
  'Slayer':        SlayerIcon,
  'Farming':       FarmingIcon,
  'Runecrafting':  RunecraftingIcon,
  'Hunter':        HunterIcon,
  'Construction':  ConstructionIcon,
  'Summoning':     SummoningIcon,
  'Dungeoneering': DungeoneeringIcon,
  'Divination':    DivinationIcon,
  'Invention':     InventionIcon,
  'Archaeology':   ArchaeologyIcon,
  'Necromancy':    NecromancyIcon,
}

const SKILL_EMOJI_MAP: Record<string, string> = {
  'Attack':        '⚔️',
  'Strength':      '💪',
  'Defence':       '🛡️',
  'Constitution':  '❤️',
  'Ranged':        '🏹',
  'Prayer':        '🙏',
  'Magic':         '🔮',
  'Cooking':       '🍳',
  'Woodcutting':   '🪓',
  'Fletching':     '🎯',
  'Fishing':       '🎣',
  'Firemaking':    '🔥',
  'Crafting':      '💎',
  'Smithing':      '🔨',
  'Mining':        '⛏️',
  'Herblore':      '🌿',
  'Agility':       '🏃',
  'Thieving':      '🗝️',
  'Slayer':        '💀',
  'Farming':       '🌾',
  'Runecrafting':  '✨',
  'Hunter':        '🦊',
  'Construction':  '🔧',
  'Summoning':     '🐾',
  'Dungeoneering': '🗺️',
  'Divination':    '🌀',
  'Invention':     '⚙️',
  'Archaeology':   '🏺',
  'Necromancy':    '👻',
}

const SKILL_ID_MAP: Record<number, string> = {
  0:  'Attack',
  1:  'Defence',
  2:  'Strength',
  3:  'Constitution',
  4:  'Ranged',
  5:  'Prayer',
  6:  'Magic',
  7:  'Cooking',
  8:  'Woodcutting',
  9:  'Fletching',
  10: 'Fishing',
  11: 'Firemaking',
  12: 'Crafting',
  13: 'Smithing',
  14: 'Mining',
  15: 'Herblore',
  16: 'Agility',
  17: 'Thieving',
  18: 'Slayer',
  19: 'Farming',
  20: 'Runecrafting',
  21: 'Hunter',
  22: 'Construction',
  23: 'Summoning',
  24: 'Dungeoneering',
  25: 'Divination',
  26: 'Invention',
  27: 'Archaeology',
  28: 'Necromancy',
}


type PageTab = 'overview' | 'xp-gained' | 'quests'
type QuestTab = 'COMPLETED' | 'STARTED' | 'NOT_STARTED'

const DIFFICULTY_CONFIG: Record<number, { label: string; cls: string }> = {
  0:   { label: 'Novice',       cls: 'bg-stone-700/80 text-stone-300' },
  1:   { label: 'Intermediate', cls: 'bg-blue-900/60 text-blue-300' },
  2:   { label: 'Experienced',  cls: 'bg-green-900/60 text-green-300' },
  3:   { label: 'Master',       cls: 'bg-orange-900/60 text-orange-300' },
  4:   { label: 'Grandmaster',  cls: 'bg-red-900/60 text-red-300' },
  250: { label: 'Special',      cls: 'bg-purple-900/60 text-purple-300' },
}

const QUEST_STATUS_CONFIG = {
  COMPLETED:   { icon: '✓', cls: 'text-green-400' },
  STARTED:     { icon: '▶', cls: 'text-yellow-400' },
  NOT_STARTED: { icon: '○', cls: 'text-stone-500' },
} as const

function QuestRow({
  quest,
  skillMap,
  completedQuestSet,
  totalQP,
  expanded,
  onToggle,
}: {
  quest: Quest
  skillMap: Record<string, number>
  completedQuestSet: Set<string>
  totalQP: number
  expanded: boolean
  onToggle: () => void
}) {
  const status = QUEST_STATUS_CONFIG[quest.status]
  const difficulty = quest.difficulty !== undefined ? DIFFICULTY_CONFIG[quest.difficulty] : undefined
  const wikiUrl = `https://runescape.wiki/w/${quest.title.replace(/ /g, '_')}`
    .replace(/ [(]quest[)]$/i, "")
  const normalizedTitle = quest.title
    .replace(/[‘’‚‛′‵]/g, "’")
    .replace(/ [(]quest[)]$/i, "")
  const reqs = QUEST_REQUIREMENTS[normalizedTitle]

  const hasSkills = (reqs?.skills?.length ?? 0) > 0
  const hasQuests = (reqs?.quests?.length ?? 0) > 0
  const hasQP = reqs?.questPoints != null

  return (
    <div className="bg-stone-900 border border-stone-700 rounded-lg overflow-hidden">
      <button
        className="w-full px-4 py-2.5 flex items-center gap-3 hover:bg-stone-800/40 transition-colors text-left"
        onClick={onToggle}
      >
        <span className={`text-xs shrink-0 w-3 text-center ${status.cls}`}>{status.icon}</span>
        <span className="text-stone-200 text-sm flex-1 min-w-0 truncate">{quest.title}</span>
        <div className="flex items-center gap-1.5 shrink-0">
          {quest.questPoints != null && quest.questPoints > 0 && (
            <span className="text-xs bg-amber-900/50 text-amber-300 border border-amber-700/50 px-1.5 py-0.5 rounded">
              ⭐ {quest.questPoints}
            </span>
          )}
          {difficulty && (
            <span className={`text-xs px-1.5 py-0.5 rounded ${difficulty.cls}`}>
              {difficulty.label}
            </span>
          )}
          <a
            href={wikiUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="text-stone-500 hover:text-amber-400 transition-colors text-sm leading-none"
            title={`Open wiki: ${quest.title}`}
            onClick={(e) => e.stopPropagation()}
          >
            🔗
          </a>
          <span className={`text-stone-500 text-xs transition-transform duration-150 ${expanded ? 'rotate-180' : ''}`} aria-hidden>
            ▼
          </span>
        </div>
      </button>

      {expanded && (
        <div className="px-4 pb-3 pt-2 border-t border-stone-700/50">
          {reqs === undefined ? (
            <p className="text-xs text-stone-500 italic">
              Requirement data not available.{' '}
              <a
                href={wikiUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-amber-500 hover:text-amber-400 not-italic"
              >
                View on wiki ↗
              </a>
            </p>
          ) : !hasQP && !hasSkills && !hasQuests ? (
            <p className="text-xs text-stone-500">No skill or quest requirements.</p>
          ) : (
            <div className="space-y-1.5 mt-0.5">
              {hasQP && reqs.questPoints != null && (
                <div className={`flex items-center gap-2 text-xs ${totalQP >= reqs.questPoints ? 'text-green-400' : 'text-red-400'}`}>
                  <span className="w-4 text-center shrink-0">⭐</span>
                  <span>{reqs.questPoints} Quest Points</span>
                  <span className="text-stone-500 ml-auto">{totalQP} / {reqs.questPoints}</span>
                </div>
              )}

              {reqs.skills?.map((req) => {
                const playerLevel = skillMap[req.skill] ?? 1
                const met = playerLevel >= req.level
                return (
                  <div key={req.skill} className={`flex items-center gap-2 text-xs ${met ? 'text-green-400' : 'text-red-400'}`}>
                    <span className="w-4 text-center shrink-0 text-sm leading-none" aria-hidden>
                      {SKILL_EMOJI_MAP[req.skill] ?? '📊'}
                    </span>
                    <span>{req.skill}</span>
                    <span className="font-medium">{req.level}</span>
                    <span className="text-stone-500 ml-auto">{playerLevel} / {req.level}</span>
                  </div>
                )
              })}

              {reqs.quests?.map((prereqTitle) => {
                const done = completedQuestSet.has(prereqTitle)
                return (
                  <div key={prereqTitle} className={`flex items-center gap-2 text-xs ${done ? 'text-green-400' : 'text-red-400'}`}>
                    <span className="w-4 text-center shrink-0">{done ? '✓' : '○'}</span>
                    <span>{prereqTitle}</span>
                  </div>
                )
              })}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default function PlayerPage() {
  const { username } = useParams<{ username: string }>()
  const navigate = useNavigate()
  const [player, setPlayer] = useState<PlayerData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [questTab, setQuestTab] = useState<QuestTab>('COMPLETED')
  const [questSearch, setQuestSearch] = useState('')
  const [failedIcons, setFailedIcons] = useState<Set<string>>(new Set())
  const { add } = useRecentCharacters()
  const [pageTab, setPageTab] = useState<PageTab>('overview')
  const [xpGained, setXpGained] = useState<SkillXpGained[] | null>(null)
  const [xpLoading, setXpLoading] = useState(false)
  const [xpError, setXpError] = useState<string | null>(null)
  const [xpDays, setXpDays] = useState(30)
  const [xpSkillFilter, setXpSkillFilter] = useState('')
  const [expandedQuests, setExpandedQuests] = useState<Set<string>>(new Set())

  useEffect(() => {
    document.title = username ? `${username} | RS3 Tracker` : 'RS3 Tracker'
  }, [username])

  useEffect(() => {
    if (!username) return
    setLoading(true)
    setError(null)
    setPlayer(null)
    setXpGained(null)
    setXpError(null)
    api
      .get<PlayerData>(`/api/player/${encodeURIComponent(username)}`)
      .then((res) => { setPlayer(res.data); add(res.data.username) })
      .catch((err) => {
        if (err.response?.status === 404) {
          setError(`Player "${username}" was not found.`)
        } else if (err.response?.status === 403) {
          setError(`"${username}" has a private profile.`)
        } else {
          setError('Failed to load player data. The server may be unavailable.')
        }
      })
      .finally(() => setLoading(false))
  }, [username])

  useEffect(() => {
    setXpGained(null)
  }, [xpDays])

  useEffect(() => {
    if (pageTab !== 'xp-gained' || xpGained !== null || !username) return
    setXpLoading(true)
    setXpError(null)
    api
      .get<SkillXpGained[]>(`/api/player/${encodeURIComponent(username)}/xp-gained?days=${xpDays}`)
      .then((res) => setXpGained(res.data))
      .catch(() => setXpError('Failed to load XP data.'))
      .finally(() => setXpLoading(false))
  }, [pageTab, username, xpGained, xpDays])

  if (loading) {
    return (
      <div className="flex-1 flex items-center justify-center bg-stone-950">
        <div className="flex flex-col items-center gap-4">
          <div className="w-14 h-14 border-4 border-stone-700 border-t-amber-500 rounded-full animate-spin" />
          <p className="text-stone-400 text-sm">Loading {username}…</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex-1 flex items-center justify-center bg-stone-950 px-4">
        <div className="text-center max-w-sm">
          <p className="text-red-400 text-lg mb-2">Player not found</p>
          <p className="text-stone-500 text-sm mb-6">{error}</p>
          <button
            onClick={() => navigate('/')}
            className="bg-amber-600 hover:bg-amber-500 text-stone-950 font-bold px-6 py-2.5 rounded-lg transition-colors text-sm"
          >
            Search again
          </button>
        </div>
      </div>
    )
  }

  if (!player) return null

  const skills = player.skills ?? []
  const sortedSkills = [...skills].sort((a, b) => a.id - b.id)
  const activities = player.recentActivity ?? []
  const quests = player.quests ?? []
  const completed = quests.filter((q) => q.status === 'COMPLETED')

  const skillMap: Record<string, number> = {}
  skills.forEach((s) => {
    const name = SKILL_ID_MAP[s.id]
    if (name) skillMap[name] = s.level
  })

  const completedQuestSet = new Set(completed.map((q) => q.title))
  const totalQP = completed.reduce((sum, q) => sum + (q.questPoints ?? 0), 0)

  function toggleQuest(title: string) {
    setExpandedQuests((prev) => {
      const next = new Set(prev)
      if (next.has(title)) next.delete(title)
      else next.add(title)
      return next
    })
  }
  const started = quests.filter((q) => q.status === 'STARTED')
  const notStarted = quests.filter((q) => q.status === 'NOT_STARTED')

  const pageTabs: { key: PageTab; label: string }[] = [
    { key: 'overview',  label: 'Overview' },
    { key: 'xp-gained', label: 'XP Gained' },
    { key: 'quests',    label: `Quests (${quests.length})` },
  ]

  const questTabs: { label: string; status: QuestTab; list: Quest[] }[] = [
    { label: 'Completed',   status: 'COMPLETED',   list: completed },
    { label: 'In Progress', status: 'STARTED',     list: started },
    { label: 'Not Started', status: 'NOT_STARTED', list: notStarted },
  ]
  const activeList = questTabs.find((t) => t.status === questTab)?.list ?? []
  const visibleQuests = questSearch.trim()
    ? activeList.filter((q) => q.title.toLowerCase().includes(questSearch.toLowerCase()))
    : activeList

  return (
    <main className="flex-1 bg-stone-950 text-stone-100 px-4 py-8">
      <div className="max-w-5xl mx-auto space-y-6">
        {/* Player header */}
        <div className="bg-stone-900 border border-amber-900/40 rounded-xl p-6 flex flex-col sm:flex-row items-center gap-4 sm:gap-6">
          <img
            src={`http://secure.runescape.com/m=avatar-rs/${encodeURIComponent(player.username)}/chat.png`}
            alt={`${player.username}'s avatar`}
            className="w-20 h-20 rounded-lg border-2 border-amber-700/60 bg-stone-800 object-cover shrink-0"
            onError={(e) => {
              e.currentTarget.style.display = 'none'
            }}
          />
          <div className="text-center sm:text-left">
            <h1 className="text-2xl sm:text-3xl font-bold text-amber-400 tracking-wide">{player.username}</h1>
            <p className="text-stone-400 text-sm mt-1">RuneScape 3 Player</p>
            {skills.length > 0 && (
              <p className="text-stone-500 text-xs mt-1">
                Total level:{' '}
                <span className="text-amber-300 font-medium">
                  {sortedSkills.reduce((sum, s) => sum + s.level, 0).toLocaleString()}
                </span>
              </p>
            )}
          </div>
        </div>

        {/* Page tab bar */}
        <div className="flex border-b border-stone-700">
          {pageTabs.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setPageTab(tab.key)}
              className={`px-5 py-3 text-sm font-medium border-b-2 transition-colors -mb-px min-h-[44px] ${
                pageTab === tab.key
                  ? 'border-amber-500 text-amber-400'
                  : 'border-transparent text-stone-400 hover:text-stone-200 hover:border-stone-500'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Overview tab: Skills + Recent Activity */}
        {pageTab === 'overview' && (
          <div className="space-y-8">
            {skills.length > 0 && (
              <section>
                <h2 className="text-xl font-semibold text-amber-400 mb-4 pb-2 border-b border-stone-700">
                  Skills
                </h2>
                <div className="grid grid-cols-3 sm:grid-cols-5 md:grid-cols-7 gap-2">
                  {sortedSkills.map((skill) => {
                    const name = SKILL_ID_MAP[skill.id]
                    const emoji = name ? SKILL_EMOJI_MAP[name] : undefined
                    const iconUrl = name ? SKILL_ICON_MAP[name] : undefined
                    const iconFailed = name ? failedIcons.has(name) : true
                    return (
                      <a
                        key={skill.id}
                        href={name ? `https://runescape.wiki/w/${name}` : undefined}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="bg-stone-900 border border-stone-700 hover:border-amber-700/40 rounded-lg p-2.5 flex flex-col items-center gap-1 transition-colors"
                      >
                        {iconUrl && !iconFailed ? (
                          <img
                            src={iconUrl}
                            alt={name}
                            className="w-6 h-6"
                            onError={() =>
                              setFailedIcons((prev) => new Set(prev).add(name!))
                            }
                          />
                        ) : emoji ? (
                          <span className="text-lg leading-none" aria-hidden="true">{emoji}</span>
                        ) : null}
                        <p className={`text-xl leading-none ${skillLevelClass(skill.level)}`}>
                          {skill.level}
                        </p>
                        <p className="text-stone-400 text-xs truncate w-full text-center">{name}</p>
                        <p className="text-stone-600 text-xs">{formatXP(skill.xp)}</p>
                      </a>
                    )
                  })}
                </div>
              </section>
            )}

            {activities.length > 0 && (
              <section>
                <h2 className="text-xl font-semibold text-amber-400 mb-4 pb-2 border-b border-stone-700">
                  Recent Activity
                </h2>
                <div className="space-y-2">
                  {activities.map((activity, idx) => (
                    <div
                      key={idx}
                      className="bg-stone-900 border border-stone-700 rounded-lg px-4 py-3 flex items-center justify-between gap-4"
                    >
                      <p className="text-stone-200 text-sm">{activity.text}</p>
                      {activity.date && (
                        <p className="text-stone-500 text-xs shrink-0">{activity.date}</p>
                      )}
                    </div>
                  ))}
                </div>
              </section>
            )}
          </div>
        )}

        {/* XP Gained tab */}
        {pageTab === 'xp-gained' && (
          <section>
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 mb-4 pb-3 border-b border-stone-700">
              <h2 className="text-xl font-semibold text-amber-400 flex-1">XP Gained</h2>
              {/* Period filter */}
              <div className="flex gap-1">
                {([7, 30, 90, 365] as const).map((d) => (
                  <button
                    key={d}
                    onClick={() => setXpDays(d)}
                    className={`px-3 py-1 rounded text-xs font-medium transition-colors ${
                      xpDays === d
                        ? 'bg-amber-600 text-stone-950'
                        : 'bg-stone-800 text-stone-400 hover:text-stone-200'
                    }`}
                  >
                    {d === 365 ? '1y' : `${d}d`}
                  </button>
                ))}
              </div>
            </div>

            {xpLoading ? (
              <div className="flex items-center justify-center py-16">
                <div className="w-10 h-10 border-4 border-stone-700 border-t-amber-500 rounded-full animate-spin" />
              </div>
            ) : xpError ? (
              <p className="text-red-400 text-sm text-center py-8">{xpError}</p>
            ) : xpGained && xpGained.length > 0 ? (
              <div className="space-y-2">
                {/* Skill search filter */}
                <input
                  type="text"
                  value={xpSkillFilter}
                  onChange={(e) => setXpSkillFilter(e.target.value)}
                  placeholder="Filter skills…"
                  className="w-full bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-1 focus:ring-amber-500/30 rounded-lg px-4 py-2 text-stone-100 placeholder-stone-500 text-sm outline-none transition-colors mb-1"
                />
                {/* Column headers */}
                <div className="flex items-center gap-4 px-4 pb-1 text-xs text-stone-500">
                  <div className="w-28 shrink-0">Skill</div>
                  <div className="flex-1 text-center">XP over period</div>
                  <div className="w-32 text-right shrink-0">Total gained</div>
                </div>
                {xpGained
                  .filter((s) =>
                    xpSkillFilter === '' ||
                    s.skillName.toLowerCase().includes(xpSkillFilter.toLowerCase())
                  )
                  .map((skill) => {
                    const iconUrl = SKILL_ICON_MAP[skill.skillName]
                    const emoji = SKILL_EMOJI_MAP[skill.skillName]
                    const iconFailed = failedIcons.has(skill.skillName)
                    // Compute per-interval XP gains from consecutive snapshot dataPoints
                    const deltas = skill.dataPoints.length >= 2
                      ? skill.dataPoints.slice(1).map((dp, i) => ({
                          date: dp.date,
                          gain: Math.max(0, dp.xp - skill.dataPoints[i].xp),
                        }))
                      : []
                    const maxGain = Math.max(...deltas.map((d) => d.gain), 1)
                    return (
                      <div
                        key={skill.skillId}
                        className="bg-stone-900 border border-stone-700 hover:border-amber-700/40 rounded-lg px-4 py-3 flex items-center gap-4 transition-colors"
                      >
                        <div className="flex items-center gap-2 w-28 shrink-0">
                          {iconUrl && !iconFailed ? (
                            <img
                              src={iconUrl}
                              alt={skill.skillName}
                              className="w-5 h-5 shrink-0"
                              onError={() => setFailedIcons((prev) => new Set(prev).add(skill.skillName))}
                            />
                          ) : emoji ? (
                            <span className="text-base leading-none shrink-0" aria-hidden="true">{emoji}</span>
                          ) : null}
                          <span className="text-stone-200 text-sm truncate">{skill.skillName}</span>
                        </div>
                        <div className="flex-1 flex flex-col min-w-0">
                          <div className="flex items-end gap-px h-8">
                            {deltas.map((d) => (
                              <div
                                key={d.date}
                                className="flex-1 bg-amber-600/50 hover:bg-amber-500/80 rounded-sm transition-colors min-w-[2px]"
                                style={{ height: `${Math.max((d.gain / maxGain) * 100, d.gain > 0 ? 4 : 0)}%` }}
                                title={`${d.date}: +${d.gain.toLocaleString()} XP`}
                              />
                            ))}
                          </div>
                          {deltas.length > 0 && (
                            <div className="flex justify-between mt-0.5 text-stone-600 text-[9px]">
                              <span>{skill.dataPoints[0].date}</span>
                              <span>{skill.dataPoints[skill.dataPoints.length - 1].date}</span>
                            </div>
                          )}
                        </div>
                        <div className="text-amber-400 font-bold text-sm w-32 text-right shrink-0">
                          +{skill.xpGained.toLocaleString()}
                        </div>
                      </div>
                    )
                  })}
              </div>
            ) : xpGained !== null ? (
              <div className="text-center py-10 space-y-2">
                <p className="text-stone-400 text-sm">No XP gains recorded for the last {xpDays} day{xpDays !== 1 ? 's' : ''}.</p>
                <p className="text-stone-600 text-xs">
                  Tracking started when this player was first looked up. Check back after another session.
                </p>
              </div>
            ) : null}
          </section>
        )}

        {/* Quests tab */}
        {pageTab === 'quests' && (
          <section>
            {quests.length > 0 ? (
              <>
                <input
                  type="text"
                  value={questSearch}
                  onChange={(e) => setQuestSearch(e.target.value)}
                  placeholder="Filter quests…"
                  className="w-full bg-stone-800 border border-stone-600 focus:border-amber-500 focus:ring-1 focus:ring-amber-500/30 rounded-lg px-4 py-2 text-stone-100 placeholder-stone-500 text-sm outline-none transition-colors mb-4"
                />

                <div className="flex flex-wrap border-b border-stone-700 mb-4">
                  {questTabs.map((tab) => (
                    <button
                      key={tab.status}
                      onClick={() => { setQuestTab(tab.status); setQuestSearch('') }}
                      className={`px-4 py-3 text-sm font-medium border-b-2 transition-colors -mb-px whitespace-nowrap min-h-[44px] ${
                        questTab === tab.status
                          ? 'border-amber-500 text-amber-400'
                          : 'border-transparent text-stone-400 hover:text-stone-200 hover:border-stone-500'
                      }`}
                    >
                      {tab.label} ({tab.list.length})
                    </button>
                  ))}
                </div>

                {visibleQuests.length > 0 ? (
                  <div className="space-y-1">
                    {visibleQuests.map((quest) => (
                      <QuestRow
                        key={quest.title}
                        quest={quest}
                        skillMap={skillMap}
                        completedQuestSet={completedQuestSet}
                        totalQP={totalQP}
                        expanded={expandedQuests.has(quest.title)}
                        onToggle={() => toggleQuest(quest.title)}
                      />
                    ))}
                  </div>
                ) : (
                  <p className="text-stone-500 text-sm text-center py-8">
                    {questSearch.trim() ? 'No quests match your search.' : 'No quests in this category.'}
                  </p>
                )}
              </>
            ) : (
              <p className="text-stone-500 text-sm text-center py-8">No quest data available.</p>
            )}
          </section>
        )}
      </div>
    </main>
  )
}
