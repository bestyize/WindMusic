import { describe, expect, it } from 'vitest'
import { activeLineIndex, formatTime, parseLrc } from './lrc'
import { availableQualities, isPlayableLink } from './quality'

describe('availableQualities', () => {
  it('caps at the reported max quality when it is HQ/SQ/ZQ', () => {
    expect([...availableQualities('HQ')]).toEqual(['LQ', 'PQ', 'HQ'])
    expect([...availableQualities('SQ')]).toEqual(['LQ', 'PQ', 'HQ', 'SQ'])
    expect([...availableQualities('ZQ')]).toEqual(['LQ', 'PQ', 'HQ', 'SQ', 'ZQ'])
  })

  it('opens every tier when the report is unreliable', () => {
    for (const max of ['EQ', 'PQ', 'LQ', null, undefined, '']) {
      expect(availableQualities(max).size).toBe(6)
    }
  })
})

describe('isPlayableLink', () => {
  it('accepts links to audio files', () => {
    expect(isPlayableLink('http://kw.cn/a/M500.mp3?bitrate$1&format$mp3')).toBe(true)
    expect(isPlayableLink('https://x.cn/F000abc.flac')).toBe(true)
    expect(isPlayableLink('https://x.cn/C400abc.m4a?vkey=1')).toBe(true)
  })

  it('rejects empty and incomplete links', () => {
    expect(isPlayableLink('')).toBe(false)
    expect(isPlayableLink(undefined)).toBe(false)
    expect(isPlayableLink('https://isure.stream.qqmusic.qq.com/&fromtag=75')).toBe(false)
    expect(isPlayableLink('":{"')).toBe(false)
  })
})

describe('parseLrc', () => {
  it('parses timed lines, skips metadata and blanks', () => {
    const lrc = '[ti:晴天]\n[ar:周杰伦]\r\n[00:01.50]故事的小黄花\n[00:03.123]\n[01:02]从出生那年就飘着'
    expect(parseLrc(lrc)).toEqual([
      { time: 1.5, text: '故事的小黄花' },
      { time: 62, text: '从出生那年就飘着' },
    ])
  })

  it('expands lines with several time tags and sorts them', () => {
    expect(parseLrc('[00:20.00][00:05.00]副歌').map((l) => l.time)).toEqual([5, 20])
  })

  it('handles missing lyrics', () => {
    expect(parseLrc('')).toEqual([])
    expect(parseLrc(null)).toEqual([])
  })
})

describe('activeLineIndex', () => {
  const lines = parseLrc('[00:01.00]a\n[00:05.00]b\n[00:09.00]c')
  it('finds the last line that has started', () => {
    expect(activeLineIndex(lines, 0)).toBe(-1)
    expect(activeLineIndex(lines, 1)).toBe(0)
    expect(activeLineIndex(lines, 6.2)).toBe(1)
    expect(activeLineIndex(lines, 99)).toBe(2)
    expect(activeLineIndex([], 3)).toBe(-1)
  })
})

describe('formatTime', () => {
  it('formats seconds as mm:ss', () => {
    expect(formatTime(0)).toBe('00:00')
    expect(formatTime(269.7)).toBe('04:29')
    expect(formatTime(Number.NaN)).toBe('00:00')
  })
})
