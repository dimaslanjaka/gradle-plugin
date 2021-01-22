package com.dimaslanjaka.kotlin

import java.util.concurrent.TimeUnit
import com.dimaslanjaka.kotlin.Helper.println
import java.text.ParseException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TimeAgo {
    val times: List<Long> = listOf(
        TimeUnit.DAYS.toMillis(365),
        TimeUnit.DAYS.toMillis(30),
        TimeUnit.DAYS.toMillis(1),
        TimeUnit.HOURS.toMillis(1),
        TimeUnit.MINUTES.toMillis(1),
        TimeUnit.SECONDS.toMillis(1)
    )
    private val timesString: List<String> = listOf("year", "month", "day", "hour", "minute", "second")
    private var result = 0
    private var res = StringBuffer()

    @JvmStatic
    fun get(duration: Long): Int {
        for (i in times.indices) {
            val current = times[i]
            val temp = duration / current
            println(timesString[i], current, temp)
        }
        return 1
    }

    @JvmStatic
    fun toDuration(duration: Long): String {
        for (i in times.indices) {
            val current = times[i]
            val temp = duration / current
            if (temp > 0) {
                result = temp.toInt()
                res.append(temp).append(" ").append(timesString[i]).append(if (temp != 1L) "s" else "").append(" ago")
                break
            }
        }

        val ret = res.toString()
        res = StringBuffer()

        return if("" == ret) {
            "0 seconds ago"
        }
        else {
            ret
        }
    }

    override fun toString(): String {
        val ret = res.toString()
        res = StringBuffer()
        return ret
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //println(get(200))
        //println(toDuration(123))
        //println(toDuration(1230))
        //println(toDuration(12300))
        //println(toDuration(123000))
        //println(toDuration(1230000))
        //println(toDuration(12300000))
        //println(toDuration(123000000))
        //println(toDuration(1230000000))
        //println(toDuration(12300000000L))
        //println(toDuration(123000000000L))

        val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MM yyyy")
        val inputString1 = "23 01 1997"
        val inputString2 = "27 04 1997"

        try {
            val date1: LocalDate? = LocalDate.parse(inputString1, dtf)
            val date2: LocalDate? = LocalDate.parse(inputString2, dtf)
            val daysBetween: Long = Duration.between(date1, date2).toDays()
            println("Days: $daysBetween")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}