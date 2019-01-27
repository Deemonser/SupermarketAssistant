package com.followme.basiclib.expand.utils

import android.app.Application
import android.support.annotation.StringDef
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * author： deemons
 * date:    2018/4/24
 * desc:    此类是对 Joda Time 的扩展
 * Joda Time 使用参考：https://www.ibm.com/developerworks/cn/java/j-jodatime.html#artdownload
 * http://ylq365.iteye.com/blog/1769680
 * Joda Time 本身非常强大，此处只是针对本项目做了一下扩展
 */
object TimeUtils {

    const val GMT_DATE = "MM/dd HH:mm ('GMT'+08:00)"
    const val TIME_NORMAL = "yyyy-MM-dd HH:mm:ss"
    const val TIME_SLASH = "yyyy/MM/dd HH:mm:ss"
    const val TIME_SLASH_M_Y = "MM/yyyy"
    const val TIME_HMS = "HH:mm:ss"
    const val TIME_HM = "HH/mm"


    @StringDef(TIME_NORMAL, TIME_SLASH, TIME_HMS, GMT_DATE)
    @Retention(RetentionPolicy.SOURCE)
    private annotation class TimeFormat

    @JvmStatic
    fun init(app: Application){
        JodaTimeAndroid.init(app)
    }


    /**
     * 将 时间字符串 转为 DataTime
     * 如果字符串格式是标准格式，可以直接使用 [DateTime.parse] 或者  [DateTime]构造
     *
     * @param time   时间字符串
     * @param format 格式 [TimeFormat]
     * @return DateTime
     */
    fun parse(time: String, @TimeFormat format: String): DateTime {
        return DateTime.parse(time, DateTimeFormat.forPattern(format))
    }

    /**
     *  友好显示时间
     */
    fun convert(long: Long): String {
        val duration = Duration(long, System.currentTimeMillis())
        return when (duration.standardMinutes.toInt()) {
            in 0..1 -> "1分钟前"
            in 1..60 -> "${duration.standardMinutes.toInt()}分钟前"
            in 60..60 * 24 -> "${duration.standardHours.toInt()}小时前"
            in 60 * 24..60 * 24 * 4 -> "${duration.standardDays.toInt()}天前"
            else -> {
                val time = DateTime(long)
                time.toString((if (DateTime.now().year == time.year) "MM/dd HH:mm" else "yy/MM/dd HH:mm"))
            }
        }
    }

}
