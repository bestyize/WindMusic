package xyz.thewind.music.common

import java.math.BigInteger
import java.security.MessageDigest
import java.util.Base64

object Codec {
    /** 32 位小写十六进制 MD5，按 UTF-8 编码计算。 */
    fun md5(text: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(text.toByteArray(Charsets.UTF_8))
        return BigInteger(1, digest).toString(16).padStart(32, '0')
    }

    fun base64Encode(text: String?): String =
        if (text.isNullOrEmpty()) "" else Base64.getEncoder().encodeToString(text.toByteArray(Charsets.UTF_8))

    /** 解码失败返回空串。 */
    fun base64Decode(encoded: String?): String =
        try {
            String(Base64.getDecoder().decode(encoded.orEmpty()), Charsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            ""
        }
}
