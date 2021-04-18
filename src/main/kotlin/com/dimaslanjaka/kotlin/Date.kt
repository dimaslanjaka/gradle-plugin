package com.dimaslanjaka.kotlin

import com.dimaslanjaka.gradle.plugin.Utils
import com.dimaslanjaka.gradle.plugin.date.SimpleDateFormat
import com.dimaslanjaka.kotlin.Helper.println
import java.util.Date
import java.util.concurrent.TimeUnit

object Date {
    private const val secondsInMilli: Long = 1000
    private const val minutesInMilli = secondsInMilli * 60
    private const val hoursInMilli = minutesInMilli * 60
    private const val debug = false

    @Suppress("unused")
    const val daysInMilli = hoursInMilli * 24
    var ONE_SECOND: Long = 60
    var ONE_MINUTE = ONE_SECOND * 1000
    var ONE_HOUR = 60 * ONE_MINUTE
    val now = Date()

    @JvmStatic
    @SuppressWarnings("all")
    fun isLessThanMinuteAgo(comparisonDate: Date, mins: Int): Boolean {
        val calc: Long = mins * ONE_MINUTE
        val sub = now.time - comparisonDate.time
        //println(sub, calc)
        return sub < calc
    }

    @JvmStatic
    @SuppressWarnings("all")
    fun isMoreThanMinuteAgo(comparisonDate: Date, mins: Int): Boolean {
        val calc: Long = mins * ONE_MINUTE
        return (now.time - comparisonDate.time) > calc
    }

    @JvmStatic
    @SuppressWarnings("all")
    fun isMoreThanHourAgo(comparisonDate: Date, ago: Int): Boolean {
        val calc = ago * ONE_HOUR
        return (now.time - comparisonDate.time) > calc
    }

    @JvmStatic
    @SuppressWarnings("all")
    fun isLessThanHourAgo(prev_date: Date, ago: Int): Boolean {
        val calc = ago * ONE_HOUR
        //var different = comparisonDate.time - now.time
        val different = now.time - prev_date.time
        //val result = different - calc
        //@SuppressWarnings("unused")
        //val elapsedHours = different / hoursInMilli
        //different %= hoursInMilli
        return if (different < calc) {
            if (debug) {
                println(different, calc, "Date is less than $ago hour ago")
            }
            true
        } else {
            if (debug) {
                println("now time ${now.time}", different, "hour in ms $calc")
            }
            false
        }
    }

    @JvmStatic
    fun getDifferenceDays(d1: Date, d2: Date): Long {
        val diff: Long = d2.time - d1.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    @JvmStatic
    fun printDifference(startDate: Date, endDate: Date) {
        //milliseconds
        var different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")

        //long elapsedDays = different / daysInMilli;
        //different = different % daysInMilli;
        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        System.out.printf(
            "%d hours, %d minutes, %d seconds%n",
            elapsedHours, elapsedMinutes, elapsedSeconds
        )
    }

    @JvmStatic
    fun testDiff() {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val past: Date? = format.parse("22/01/2021 04:11:10")
        println("previous date $past", "now $now")
        past?.let {
            println(
                isLessThanHourAgo(it, 1),
                isMoreThanHourAgo(it, 1),
                isLessThanMinuteAgo(it, 10),
                isMoreThanMinuteAgo(it, 10)
            )
        }
    }
}

fun main() {
    val tmp = Utils.getTempFile("test", true)
    println(tmp.creationTime, tmp.lastModified, tmp.isModifiedMoreThanHour(1))
}