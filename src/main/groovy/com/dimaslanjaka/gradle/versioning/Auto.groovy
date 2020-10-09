package com.dimaslanjaka.gradle.versioning

import java.text.SimpleDateFormat

class Auto {
    static def today = new Date()
    static def base = '3'

    // We use the current year (double digit) and substract 16. We first released Focus in
    // 2017 so this value will start counting at 1 and increment by one every year.
    static def year = String.valueOf((new SimpleDateFormat('yy').format(today) as int) - 16)

    // We use the day in the Year (e.g. 248) as opposed to month + day (0510) because it's one digit shorter.
    // If needed we pad with zeros (e.g. 25 -> 025)
    static def day = String.format('%03d', (new SimpleDateFormat('D').format(today) as int))

    // We append the hour in day (24h) and minute in hour (7:26 pm -> 1926). We do not append
    // seconds. This assumes that we do not need to build multiple release(!) builds the same
    // minute.
    static def time = new SimpleDateFormat('HHmm').format(today)

    public static int VersionCode = (base + year + day + time) as int
    public static int FixedVersionCode = (base + year + day) as int

    static void refresh() {
        VersionCode = (base + year + day + time) as int
        FixedVersionCode = (base + year + day) as int
    }
}