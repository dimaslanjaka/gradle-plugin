package com.dimaslanjaka.gradle.Utils

import java.util.concurrent.TimeUnit

class Date extends java.util.Date {
    static boolean isMoreThanMinutes(int mins, java.util.Date fromDate) {
        def difference = new java.util.Date().time - fromDate.time
        return (difference > TimeUnit.MINUTES.toMillis(mins))
    }
}