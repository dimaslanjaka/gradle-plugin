package com.dimaslanjaka.kotlin

object Helper {
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