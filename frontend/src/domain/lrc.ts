export interface LyricLine {
  /** 秒 */
  time: number
  text: string
}

const TIME_TAG = /\[(\d{1,3}):(\d{1,2})(?:[.:](\d{1,3}))?]/g

/**
 * 解析 LRC 歌词。一行可以带多个时间标签；`[ti:...]` 这类元信息行和空行会被忽略。
 */
export function parseLrc(text: string | null | undefined): LyricLine[] {
  if (!text) return []
  const lines: LyricLine[] = []
  for (const raw of text.split(/\r?\n/)) {
    const times = [...raw.matchAll(TIME_TAG)]
    if (times.length === 0) continue
    const content = raw.replace(TIME_TAG, '').trim()
    if (!content) continue
    for (const [, mm, ss, frac = '0'] of times) {
      const time = Number(mm) * 60 + Number(ss) + Number(frac) / 10 ** frac.length
      lines.push({ time, text: content })
    }
  }
  return lines.sort((a, b) => a.time - b.time)
}

/** 当前播放时间对应的歌词行下标，还没到第一行时为 -1。 */
export function activeLineIndex(lines: readonly LyricLine[], time: number): number {
  let lo = 0
  let hi = lines.length - 1
  let found = -1
  while (lo <= hi) {
    const mid = (lo + hi) >> 1
    if (lines[mid]!.time <= time) {
      found = mid
      lo = mid + 1
    } else {
      hi = mid - 1
    }
  }
  return found
}

export function formatTime(seconds: number): string {
  if (!Number.isFinite(seconds) || seconds < 0) return '00:00'
  const s = Math.floor(seconds)
  return `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}`
}
