package com.dimaslanjaka.kotlin

object Helper {
    const val ANSI_RESET = "\u001B[0m"
    const val ANSI_BLACK = "\u001B[30m"
    const val ANSI_RED = "\u001B[31m"
    const val ANSI_GREEN = "\u001B[32m"
    const val ANSI_YELLOW = "\u001B[33m"
    const val ANSI_BLUE = "\u001B[34m"
    const val ANSI_PURPLE = "\u001B[35m"
    const val ANSI_CYAN = "\u001B[36m"
    const val ANSI_WHITE = "\u001B[37m"
    const val ANSI_BLACK_BACKGROUND = "\u001B[40m"
    const val ANSI_RED_BACKGROUND = "\u001B[41m"
    const val ANSI_GREEN_BACKGROUND = "\u001B[42m"
    const val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
    const val ANSI_BLUE_BACKGROUND = "\u001B[44m"
    const val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
    const val ANSI_CYAN_BACKGROUND = "\u001B[46m"
    const val ANSI_WHITE_BACKGROUND = "\u001B[47m"

    @JvmStatic
    @SuppressWarnings("all")
    fun println(o: Any?) {
        when (o) {
            is Boolean -> {
                printbool(o)
            }
            is String -> {
                printstr(o)
            }
            is Int -> {
                printint(o)
            }
            else -> {
                kotlin.io.println(o)
            }
        }
    }

    @JvmStatic
    fun println(vararg o: Any?) {
        val list = mutableListOf<Any?>()
        o.forEach { obj ->
            if (obj is Int) {
                list.add(ANSI_RED + obj + ANSI_RESET)
            } else if (obj is String) {
                list.add(ANSI_GREEN + obj + ANSI_RESET)
            } else if (obj is Boolean) {
                list.add(ANSI_BLUE + obj + ANSI_RESET)
            } else {
                if (obj != null) {
                    list.add(obj)
                } else {
                    list.add("NULL")
                }
            }
        }
        kotlin.io.println(list)
    }

    /**
     * Print boolean
     */
    @JvmStatic
    fun printbool(o: Any?) {
        kotlin.io.println(ANSI_BLUE + o + ANSI_RESET)
    }

    @JvmStatic
    fun printstr(o: Any?) {
        kotlin.io.println(ANSI_GREEN + o + ANSI_RESET)
    }

    @JvmStatic
    fun printint(o: Any?) {
        kotlin.io.println(ANSI_RED + o + ANSI_RESET)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //kotlin.io.println(ANSI_RED + "This text is red!" + ANSI_RESET);
        //kotlin.io.println(ANSI_GREEN_BACKGROUND + "This text has a green background but default text!" + ANSI_RESET);
        //kotlin.io.println(ANSI_RED + "This text has red text but a default background!" + ANSI_RESET);
        //kotlin.io.println(ANSI_GREEN_BACKGROUND + ANSI_RED + "This text has a green background and red text!" + ANSI_RESET);

        //getMethod()
    }

    @JvmStatic
    fun getMethod() {
        val list = mutableListOf<String>()
        val stackTraceElements = Thread.currentThread().stackTrace
        for (i in 1 until stackTraceElements.size) {
            val ste: StackTraceElement = stackTraceElements[i]
            //println(ste.className, ste.methodName)
            if (ste.className != "com.dimaslanjaka.kotlin.Helper") {
                if (ste.className !in list) {
                    list.add(ste.className)
                }
            }
        }
    }

    @JvmStatic
    fun getCallerCallerClassName(): String? {
        val stElements = Thread.currentThread().stackTrace
        var callerClassName: String? = null
        for (i in 1 until stElements.size) {
            val ste = stElements[i]
            if (ste.className != Helper::class.java.name) {
                if (callerClassName == null) {
                    callerClassName = ste.className
                } else if (callerClassName != ste.className) {
                    return ste.className
                }
            }
        }
        return null
    }
}