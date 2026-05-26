/**
 * Generates frontend/src/data/questRequirements.ts from the RS3 Wiki:
 *   - Quest prerequisites: Module:Questreq/data
 *   - Skill requirements:  each quest's infobox {{Skillreq}} templates
 */

import https from 'https';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const OUT_FILE = path.join(__dirname, '../frontend/src/data/questRequirements.ts');
const WIKI = 'https://runescape.wiki';
const HEADERS = { 'User-Agent': 'RS3QuestTracker/1.0 (educational project)' };

function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }

function fetchRaw(url) {
  return new Promise((resolve, reject) => {
    https.get(url, { headers: HEADERS }, res => {
      let d = '';
      res.on('data', c => d += c);
      res.on('end', () => resolve(d));
    }).on('error', reject);
  });
}

// Normalize curly apostrophes → straight so they match our TS keys
function normalizeApostrophes(s) {
  return s.replace(/[‘’‚‛′‵]/g, "’");
}

// Strip wiki "(quest)" disambiguation suffix — RuneMetrics omits it
function normalizeName(s) {
  return normalizeApostrophes(s).replace(/ [(]quest[)]$/i, "")
}

// ── 1. Parse Module:Questreq/data ────────────────────────────────────────────

async function fetchQuestPrereqs() {
  console.log('Fetching Module:Questreq/data …');
  const raw = await fetchRaw(WIKI + '/w/Module:Questreq/data?action=raw');

  const prereqMap = {};

  // Match each ["Quest Name"] = { ... } block (handles multi-line values)
  const blockRe = /\["([^"]+)"\]\s*=\s*\{([^}]*)\}/gs;
  let m;
  while ((m = blockRe.exec(raw)) !== null) {
    const rawName = m[1];
    // "Follows:X" entries describe the NEXT quest, not a prerequisite
    if (rawName.startsWith('Follows:')) continue;

    const bodyStr = m[2];
    const prereqs = [];

    // Each prerequisite is a quoted string
    const strRe = /"([^"]+)"/g;
    let sm;
    while ((sm = strRe.exec(bodyStr)) !== null) {
      const prereq = sm[1];
      if (prereq.startsWith('Misc:') || prereq.startsWith('Follows:')) continue;
      // Strip Partial: / Full: prefixes — we treat them as full requirements
      const clean = prereq.replace(/^(?:Partial:|Full:)/, '');
      prereqs.push(normalizeName(clean));
    }

    const normalizedName = normalizeName(rawName);
    prereqMap[normalizedName] = prereqs;
  }

  console.log(`  → ${Object.keys(prereqMap).length} quest entries`);
  return prereqMap;
}

// ── 2. Batch-fetch skill requirements from infoboxes ─────────────────────────

function parseSkillReqs(wikitext) {
  const reqMatch = wikitext.match(/\|requirements\s*=([\s\S]*?)(?=\n\||\n==|$)/);
  if (!reqMatch) return { skills: [], questPoints: null };

  const section = reqMatch[1];
  const skills = [];
  let questPoints = null;

  const re = /\{\{[Ss]killreq\|([^|]+)\|(\d+)/g;
  let sm;
  while ((sm = re.exec(section)) !== null) {
    const skill = sm[1].trim();
    const level = parseInt(sm[2], 10);
    if (skill.toLowerCase().replace(/\s+/g, '') === 'questpoints') {
      questPoints = level;
    } else {
      skills.push({ skill, level });
    }
  }
  return { skills, questPoints };
}

async function fetchSkillReqsBatch(questNames) {
  // Wiki page titles: strip "(quest)" suffix that wiki adds to disambiguate
  const titles = questNames.map(n => n.replace(/\s*\(quest\)\s*$/i, ''));
  const titleParam = titles.map(encodeURIComponent).join('|');
  const url = `${WIKI}/api.php?action=query&prop=revisions&titles=${titleParam}&rvprop=content&rvslots=main&format=json`;

  const raw = await fetchRaw(url);
  let data;
  try { data = JSON.parse(raw); } catch { return {}; }

  const result = {};
  for (const page of Object.values(data.query?.pages ?? {})) {
    const content = page.revisions?.[0]?.slots?.main?.['*'] ?? '';
    const normalized = normalizeName(page.title);
    result[normalized] = parseSkillReqs(content);
  }
  return result;
}

async function fetchAllSkillReqs(questNames) {
  const BATCH = 40;
  const all = {};
  for (let i = 0; i < questNames.length; i += BATCH) {
    const batch = questNames.slice(i, i + BATCH);
    process.stdout.write(`  fetching skill reqs ${i + 1}–${Math.min(i + BATCH, questNames.length)} / ${questNames.length}\r`);
    const res = await fetchSkillReqsBatch(batch);
    Object.assign(all, res);
    await sleep(300);
  }
  process.stdout.write('\n');
  return all;
}

// ── 3. Generate TypeScript ───────────────────────────────────────────────────

function renderEntry(name, prereqs, skillData) {
  const parts = [];
  if ((skillData?.questPoints ?? null) !== null) {
    parts.push(`questPoints: ${skillData.questPoints}`);
  }
  if (skillData?.skills?.length) {
    const s = skillData.skills.map(r => `{ skill: ${JSON.stringify(r.skill)}, level: ${r.level} }`).join(', ');
    parts.push(`skills: [${s}]`);
  }
  if (prereqs.length) {
    parts.push(`quests: [${prereqs.map(q => JSON.stringify(q)).join(', ')}]`);
  }
  const val = parts.length ? `{ ${parts.join(', ')} }` : '{}';
  return `  ${JSON.stringify(name)}: ${val},`;
}

// ── main ─────────────────────────────────────────────────────────────────────

(async () => {
  const prereqMap = await fetchQuestPrereqs();
  const questNames = Object.keys(prereqMap).sort();

  console.log('Fetching skill requirements from quest infoboxes …');
  const skillReqs = await fetchAllSkillReqs(questNames);

  const lines = [
    `export interface SkillReq {`,
    `  skill: string`,
    `  level: number`,
    `}`,
    ``,
    `export interface QuestReqs {`,
    `  skills?: SkillReq[]`,
    `  quests?: string[]`,
    `  questPoints?: number`,
    `}`,
    ``,
    `// Auto-generated from RS3 Wiki. Re-run scripts/generateQuestRequirements.mjs to update.`,
    `export const QUEST_REQUIREMENTS: Record<string, QuestReqs> = {`,
  ];

  for (const name of questNames) {
    lines.push(renderEntry(name, prereqMap[name], skillReqs[name]));
  }

  lines.push(`}`);
  lines.push(``);

  fs.writeFileSync(OUT_FILE, lines.join('\n'), 'utf8');
  console.log(`\nWrote ${questNames.length} quests → ${OUT_FILE}`);
})().catch(err => { console.error(err); process.exit(1); });
