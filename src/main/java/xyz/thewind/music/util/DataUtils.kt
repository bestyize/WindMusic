package xyz.thewind.music.util

import java.text.SimpleDateFormat
import java.util.*

object DataUtils {

    @JvmStatic
    fun getTodayStr(): String {
        return sdf.format(Date())
    }
}

private val sdf = SimpleDateFormat("yyyy-MM-dd")