package xyz.thewind.music.common

import kotlin.random.Random

object IpHelper {
    /** 国内若干运营商 IP 段（按 32 位有符号整数表示的起止地址）。 */
    private val CN_IP_RANGES = arrayOf(
        intArrayOf(607649792, 608174079),     // 36.56.0.0-36.63.255.255
        intArrayOf(1038614528, 1039007743),   // 61.232.0.0-61.237.255.255
        intArrayOf(1783627776, 1784676351),   // 106.80.0.0-106.95.255.255
        intArrayOf(2035023872, 2035154943),   // 121.76.0.0-121.77.255.255
        intArrayOf(2078801920, 2079064063),   // 123.232.0.0-123.235.255.255
        intArrayOf(-1950089216, -1948778497), // 139.196.0.0-139.215.255.255
        intArrayOf(-1425539072, -1425014785), // 171.8.0.0-171.15.255.255
        intArrayOf(-1236271104, -1235419137), // 182.80.0.0-182.92.255.255
        intArrayOf(-770113536, -768606209),   // 210.25.0.0-210.47.255.255
        intArrayOf(-569376768, -564133889),   // 222.16.0.0-222.95.255.255
    )

    /** 随机生成一个国内 IP，用于伪造 X-Real-IP 绕过地区限制。 */
    fun randomChinaIp(): String {
        val (start, end) = CN_IP_RANGES[Random.nextInt(CN_IP_RANGES.size)].let { it[0] to it[1] }
        return intToIp(start + Random.nextInt(end - start))
    }

    fun intToIp(ip: Int): String =
        listOf(ip ushr 24, ip ushr 16, ip ushr 8, ip).joinToString(".") { (it and 0xff).toString() }
}
