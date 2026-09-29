export type QualityCode = 'LQ' | 'PQ' | 'HQ' | 'SQ' | 'ZQ' | 'EQ'

export interface QualityTier {
  code: QualityCode
  /** 音质条上的短说明 */
  short: string
  /** 完整说明，用于提示和无障碍标签 */
  label: string
}

/** 从低到高排列。 */
export const QUALITY_TIERS: readonly QualityTier[] = [
  { code: 'LQ', short: '128k', label: '标准 128k' },
  { code: 'PQ', short: '192k', label: '较高 192k' },
  { code: 'HQ', short: '320k', label: '高品 320k MP3' },
  { code: 'SQ', short: '无损', label: '无损 FLAC' },
  { code: 'ZQ', short: 'Hi-Res', label: 'Hi-Res 24bit FLAC' },
  { code: 'EQ', short: '杜比', label: '杜比全景声，仅咪咕' },
]

/**
 * 根据平台报告的最高音质判断哪些音质可以下载。
 *
 * 平台报告的最高音质只在 HQ/SQ/ZQ 时可靠，报告为 LQ/PQ 或缺失时不做限制，全部开放尝试。
 */
export function availableQualities(max: string | null | undefined): Set<QualityCode> {
  const all = QUALITY_TIERS.map((t) => t.code)
  const index = all.indexOf(max as QualityCode)
  // 只有咪咕会报告 EQ；报告缺失或只到 LQ/PQ 时平台信息不可靠，全部开放尝试
  if (index < 0 || max === 'EQ' || max === 'LQ' || max === 'PQ') return new Set(all)
  return new Set(all.slice(0, index + 1))
}

const AUDIO_LINK = /^https?:\/\/[^?#]+\.(mp3|flac|m4a|aac|ogg|wav)([?#]|$)/i

/** 平台在没有音源时会返回空串或不完整的地址，只把指向音频文件的链接当作有效。 */
export function isPlayableLink(link: string | null | undefined): link is string {
  return !!link && AUDIO_LINK.test(link)
}
