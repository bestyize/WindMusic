package xyz.thewind.music.util

object NumberHelper {
    @JvmStatic
    fun parseNumber(num: String?, defaultVal: Int): Int {
        var realNum = defaultVal
        if (num.isNullOrBlank())  {
            return defaultVal
        }
        try {
            realNum = num.toInt()
        } catch (e: Exception) {
            println(e.cause)
        }
        return realNum
    }
}