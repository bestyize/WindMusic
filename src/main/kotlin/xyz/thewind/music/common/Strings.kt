package xyz.thewind.music.common

import java.util.regex.Pattern

/**
 * 与 Java `String.split(regex)` 语义完全一致：按正则切分并丢弃末尾空串。
 * Kotlin 自带的 `split` 按字面量切分且保留末尾空串，行为不同。
 */
fun String.javaSplit(regex: String): Array<String> = Pattern.compile(regex).split(this)
