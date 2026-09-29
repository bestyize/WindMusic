package xyz.thewind.music.provider.kw

import java.util.Base64

/**
 * 从酷我客户端逆向出的加密算法，用于 `nmobi.kuwo.cn/mobi.s` 接口的 `q` 参数。
 *
 * 本质上是固定密钥 `ylzsxkwm` 的单 DES（ECB 模式、小端字节序、零填充），
 * 只是位编号与标准 DES 相反，所以各置换表看起来和教科书里的不一样。
 * 注意末尾总会多出一个分组：明文长度恰好是 8 的倍数时会追加一个全 0 分组。
 *
 * 实现无共享状态，线程安全。
 */
internal object KwCrypto {

    private val KEY = "ylzsxkwm".toByteArray(Charsets.UTF_8)

    /** BIT[i] = 第 i 位为 1 的掩码 */
    private val BIT = LongArray(64) { 1L shl it }

    /** 初始置换 IP */
    private val IP = intArrayOf(
        57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7,
        56, 48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2,
        60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6,
    )

    /** 扩展置换 E：32 位 -> 8 组 6 位，每组占一个字节的低 6 位（-1 表示补 0） */
    private val EXPAND = intArrayOf(
        31, 0, 1, 2, 3, 4, -1, -1, 3, 4, 5, 6, 7, 8, -1, -1,
        7, 8, 9, 10, 11, 12, -1, -1, 11, 12, 13, 14, 15, 16, -1, -1,
        15, 16, 17, 18, 19, 20, -1, -1, 19, 20, 21, 22, 23, 24, -1, -1,
        23, 24, 25, 26, 27, 28, -1, -1, 27, 28, 29, 30, 31, 30, -1, -1,
    )

    /** 8 个 S 盒，按 6 位输入直接索引 */
    private val SBOX = arrayOf(
        intArrayOf(14, 4, 3, 15, 2, 13, 5, 3, 13, 14, 6, 9, 11, 2, 0, 5, 4, 1, 10, 12, 15, 6, 9, 10, 1, 8, 12, 7, 8, 11, 7, 0, 0, 15, 10, 5, 14, 4, 9, 10, 7, 8, 12, 3, 13, 1, 3, 6, 15, 12, 6, 11, 2, 9, 5, 0, 4, 2, 11, 14, 1, 7, 8, 13),
        intArrayOf(15, 0, 9, 5, 6, 10, 12, 9, 8, 7, 2, 12, 3, 13, 5, 2, 1, 14, 7, 8, 11, 4, 0, 3, 14, 11, 13, 6, 4, 1, 10, 15, 3, 13, 12, 11, 15, 3, 6, 0, 4, 10, 1, 7, 8, 4, 11, 14, 13, 8, 0, 6, 2, 15, 9, 5, 7, 1, 10, 12, 14, 2, 5, 9),
        intArrayOf(10, 13, 1, 11, 6, 8, 11, 5, 9, 4, 12, 2, 15, 3, 2, 14, 0, 6, 13, 1, 3, 15, 4, 10, 14, 9, 7, 12, 5, 0, 8, 7, 13, 1, 2, 4, 3, 6, 12, 11, 0, 13, 5, 14, 6, 8, 15, 2, 7, 10, 8, 15, 4, 9, 11, 5, 9, 0, 14, 3, 10, 7, 1, 12),
        intArrayOf(7, 10, 1, 15, 0, 12, 11, 5, 14, 9, 8, 3, 9, 7, 4, 8, 13, 6, 2, 1, 6, 11, 12, 2, 3, 0, 5, 14, 10, 13, 15, 4, 13, 3, 4, 9, 6, 10, 1, 12, 11, 0, 2, 5, 0, 13, 14, 2, 8, 15, 7, 4, 15, 1, 10, 7, 5, 6, 12, 11, 3, 8, 9, 14),
        intArrayOf(2, 4, 8, 15, 7, 10, 13, 6, 4, 1, 3, 12, 11, 7, 14, 0, 12, 2, 5, 9, 10, 13, 0, 3, 1, 11, 15, 5, 6, 8, 9, 14, 14, 11, 5, 6, 4, 1, 3, 10, 2, 12, 15, 0, 13, 2, 8, 5, 11, 8, 0, 15, 7, 14, 9, 4, 12, 7, 10, 9, 1, 13, 6, 3),
        intArrayOf(12, 9, 0, 7, 9, 2, 14, 1, 10, 15, 3, 4, 6, 12, 5, 11, 1, 14, 13, 0, 2, 8, 7, 13, 15, 5, 4, 10, 8, 3, 11, 6, 10, 4, 6, 11, 7, 9, 0, 6, 4, 2, 13, 1, 9, 15, 3, 8, 15, 3, 1, 14, 12, 5, 11, 0, 2, 12, 14, 7, 5, 10, 8, 13),
        intArrayOf(4, 1, 3, 10, 15, 12, 5, 0, 2, 11, 9, 6, 8, 7, 6, 9, 11, 4, 12, 15, 0, 3, 10, 5, 14, 13, 7, 8, 13, 14, 1, 2, 13, 6, 14, 9, 4, 1, 2, 14, 11, 13, 5, 0, 1, 10, 8, 3, 0, 11, 3, 5, 9, 4, 15, 2, 7, 8, 12, 15, 10, 7, 6, 12),
        intArrayOf(13, 7, 10, 0, 6, 9, 5, 15, 8, 4, 3, 10, 11, 14, 12, 5, 2, 11, 9, 6, 15, 12, 0, 3, 4, 1, 14, 13, 1, 2, 7, 8, 1, 2, 12, 15, 10, 4, 0, 3, 13, 14, 6, 9, 7, 8, 9, 6, 15, 1, 5, 12, 3, 10, 14, 5, 8, 7, 11, 0, 4, 13, 2, 11),
    )

    /** P 置换 */
    private val P = intArrayOf(
        15, 6, 19, 20, 28, 11, 27, 16, 0, 14, 22, 25, 4, 17, 30, 9,
        1, 7, 23, 13, 31, 26, 2, 8, 18, 12, 29, 5, 21, 10, 3, 24,
    )

    /** 逆初始置换 IP^-1 */
    private val FP = intArrayOf(
        39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30,
        37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28,
        35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26,
        33, 1, 41, 9, 49, 17, 57, 25, 32, 0, 40, 8, 48, 16, 56, 24,
    )

    /** 密钥置换 PC-1：64 位密钥 -> 56 位 */
    private val PC1 = intArrayOf(
        56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1,
        58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38,
        30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36,
        28, 20, 12, 4, 27, 19, 11, 3,
    )

    /** 密钥置换 PC-2：56 位 -> 8 组 6 位，布局与 [EXPAND] 的输出一致 */
    private val PC2 = intArrayOf(
        13, 16, 10, 23, 0, 4, -1, -1, 2, 27, 14, 5, 20, 9, -1, -1,
        22, 18, 11, 3, 25, 7, -1, -1, 15, 6, 26, 19, 12, 1, -1, -1,
        40, 51, 30, 36, 46, 54, -1, -1, 29, 39, 50, 44, 32, 47, -1, -1,
        43, 48, 38, 55, 33, 52, -1, -1, 45, 41, 49, 35, 28, 31, -1, -1,
    )

    /** 每轮子密钥的循环移位位数 */
    private val SHIFTS = intArrayOf(1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1)

    /** 按移位位数取 C、D 两个 28 位半区中会被移出的低位：ROTATE_MASK[n] */
    private val ROTATE_MASK = longArrayOf(0, 1048577, 3145731)

    /**
     * 加密并 Base64 编码。
     *
     * 注意：明文长度取的是 `text.length`（UTF-16 字符数）而不是 UTF-8 字节数，
     * 含中文等非 ASCII 字符时末尾的字节会被丢弃。这是旧版的原样行为，接口参数都是 ASCII，不受影响。
     */
    fun encrypt(text: String): String {
        val cipher = encrypt(text.toByteArray(Charsets.UTF_8), text.length, KEY)
        return Base64.getEncoder().encodeToString(cipher)
    }

    private fun encrypt(text: ByteArray, textLen: Int, key: ByteArray): ByteArray {
        // 密钥和明文都按小端序装入 long；字节是有符号的，与旧版一样做符号扩展
        var keyBits = 0L
        for (i in key.indices) {
            keyBits = keyBits or (key[i].toLong() shl (i shl 3))
        }
        val subKeys = subKeys(keyBits)

        val fullBlocks = textLen shr 3
        val blocks = LongArray(fullBlocks + 1)
        for (i in 0 until fullBlocks) {
            var block = 0L
            for (j in 0 until 8) {
                block = (text[i * 8 + j].toLong() shl (j * 8)) or block
            }
            blocks[i] = encryptBlock(subKeys, block)
        }

        // 最后一组：剩余不足 8 字节的部分补 0（剩余 0 字节时即为全 0 分组）
        val tailStart = fullBlocks shl 3
        var tail = 0L
        for (i in 0 until textLen % 8) {
            tail = tail or (text[tailStart + i].toLong() shl (i * 8))
        }
        blocks[fullBlocks] = encryptBlock(subKeys, tail)

        val cipher = ByteArray(blocks.size * 8)
        for (i in blocks.indices) {
            for (j in 0 until 8) {
                cipher[i * 8 + j] = (blocks[i] shr (j * 8)).toByte()
            }
        }
        return cipher
    }

    /** 按置换表抽取位：结果的第 i 位 = [input] 的第 `table[i]` 位，`table[i] < 0` 时为 0。 */
    private fun permute(table: IntArray, input: Long): Long {
        var out = 0L
        for (i in table.indices) {
            if (table[i] >= 0 && (BIT[table[i]] and input) != 0L) {
                out = out or BIT[i]
            }
        }
        return out
    }

    /** 生成 16 轮加密子密钥（旧版还支持逆序生成解密子密钥，本工程用不到，已删除）。 */
    private fun subKeys(key: Long): LongArray {
        val keys = LongArray(16)
        var cd = permute(PC1, key)
        for (i in 0 until 16) {
            val shift = SHIFTS[i]
            val mask = ROTATE_MASK[shift]
            // C、D 两个 28 位半区各自循环右移 shift 位
            cd = ((cd and mask.inv()) shr shift) or ((mask and cd) shl (28 - shift))
            keys[i] = permute(PC2, cd)
        }
        return keys
    }

    private fun encryptBlock(subKeys: LongArray, block: Long): Long {
        val ip = permute(IP, block)
        var left = (ip and 0xFFFFFFFFL).toInt()
        var right = ((ip and -0x100000000L) shr 32).toInt()

        for (subKey in subKeys) {
            // Feistel 函数 f(R, K)
            val expanded = permute(EXPAND, right.toLong()) xor subKey
            var sboxOut = 0
            for (box in 7 downTo 0) {
                // 每个字节只有低 6 位有效，取值 0..63
                val sboxIn = (expanded shr (box * 8)).toInt() and 0xFF
                sboxOut = (sboxOut shl 4) or SBOX[box][sboxIn]
            }
            val f = permute(P, sboxOut.toLong())

            val newRight = (left.toLong() xor f).toInt()
            left = right
            right = newRight
        }

        // 最后一轮后左右交换
        val preOutput = (right.toLong() and 0xFFFFFFFFL) or ((left.toLong() shl 32) and -0x100000000L)
        return permute(FP, preOutput)
    }
}
