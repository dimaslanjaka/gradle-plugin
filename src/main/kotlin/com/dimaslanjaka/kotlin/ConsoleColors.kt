package com.dimaslanjaka.kotlin

import java.util.*

enum class ConsoleColors(var code: String?) {
    // Reset
    RESET("\u001b[0m"),  // Text Reset

    // Regular Colors
    BLACK("\u001b[0;30m"),  // BLACK
    RED("\u001b[0;31m"),  // RED
    GREEN("\u001b[0;32m"),  // GREEN
    YELLOW("\u001b[0;33m"),  // YELLOW
    BLUE("\u001b[0;34m"),  // BLUE
    PURPLE("\u001b[0;35m"),  // PURPLE
    CYAN("\u001b[0;36m"),  // CYAN
    WHITE("\u001b[0;37m"),  // WHITE

    // Bold
    ONLY_BOLD("\u001b[1m"),  // ONLY BOLD
    BLACK_BOLD("\u001b[1;30m"),  // BLACK
    RED_BOLD("\u001b[1;31m"),  // RED
    GREEN_BOLD("\u001b[1;32m"),  // GREEN
    YELLOW_BOLD("\u001b[1;33m"),  // YELLOW
    BLUE_BOLD("\u001b[1;34m"),  // BLUE
    PURPLE_BOLD("\u001b[1;35m"),  // PURPLE
    CYAN_BOLD("\u001b[1;36m"),  // CYAN
    WHITE_BOLD("\u001b[1;37m"),  // WHITE

    // Underline
    ONLY_UNDERLINED("\u001b[4m"),  // ONLY UNDERLINED
    BLACK_UNDERLINED("\u001b[4;30m"),  // BLACK
    RED_UNDERLINED("\u001b[4;31m"),  // RED
    GREEN_UNDERLINED("\u001b[4;32m"),  // GREEN
    YELLOW_UNDERLINED("\u001b[4;33m"),  // YELLOW
    BLUE_UNDERLINED("\u001b[4;34m"),  // BLUE
    PURPLE_UNDERLINED("\u001b[4;35m"),  // PURPLE
    CYAN_UNDERLINED("\u001b[4;36m"),  // CYAN
    WHITE_UNDERLINED("\u001b[4;37m"),  // WHITE

    // High Intensity
    BLACK_BRIGHT("\u001b[0;90m"),  // BLACK
    RED_BRIGHT("\u001b[0;91m"),  // RED
    GREEN_BRIGHT("\u001b[0;92m"),  // GREEN
    YELLOW_BRIGHT("\u001b[0;93m"),  // YELLOW
    BLUE_BRIGHT("\u001b[0;94m"),  // BLUE
    PURPLE_BRIGHT("\u001b[0;95m"),  // PURPLE
    CYAN_BRIGHT("\u001b[0;96m"),  // CYAN
    WHITE_BRIGHT("\u001b[0;97m"),  // WHITE

    // Bold High Intensity
    BLACK_BOLD_BRIGHT("\u001b[1;90m"),  // BLACK
    RED_BOLD_BRIGHT("\u001b[1;91m"),  // RED
    GREEN_BOLD_BRIGHT("\u001b[1;92m"),  // GREEN
    YELLOW_BOLD_BRIGHT("\u001b[1;93m"),  // YELLOW
    BLUE_BOLD_BRIGHT("\u001b[1;94m"),  // BLUE
    PURPLE_BOLD_BRIGHT("\u001b[1;95m"),  // PURPLE
    CYAN_BOLD_BRIGHT("\u001b[1;96m"),  // CYAN
    WHITE_BOLD_BRIGHT("\u001b[1;97m");

    companion object {
        // WHITE
        private val VALUES = Collections.unmodifiableList(Arrays.asList(*values()))
        private val SIZE = VALUES.size
        private val RANDOM: Random = Random()
        fun bold(message: String?): String {
            return ONLY_BOLD.code + (message ?: "NULL") + RESET.code
        }

        @JvmStatic
        fun printstr(o: String?) {
            kotlin.io.println(green(o))
        }

        @JvmStatic
        fun printint(o: Int?) {
            kotlin.io.println(red(o.toString()))
        }

        @JvmStatic
        fun underline(message: String?): String {
            return ONLY_UNDERLINED.code + (message ?: "NULL") + RESET.code
        }

        @JvmStatic
        fun red(message: String?): String {
            return styler(RED, (message ?: "NULL"))
        }

        @JvmStatic
        fun green(message: String?): String {
            return styler(GREEN, (message ?: "NULL"))
        }

        @JvmStatic
        fun error(message: Any?) {
            print(styler(RED, message.toString()))
        }

        @JvmStatic
        fun success(message: Any?) {
            print(green(message.toString()))
        }

        @JvmStatic
        fun styler(color: ConsoleColors, message: String): String {
            return color.code + message + RESET.code
        }

        @JvmStatic
        fun success(vararg msg: Any?) {
            success(msg.contentToString())
        }

        @JvmStatic
        fun print(msg: Any?) {
            kotlin.io.println(msg)
        }

        @JvmStatic
        fun print(vararg msg: Any?) {
            print(msg.contentToString())
        }

        @JvmStatic
        fun error(vararg msg: Any?) {
            error(msg.contentToString())
        }

        /**
         * Get random color
         *
         * @return com.dimaslanjaka.kotlin.ConsoleColors
         */
        @JvmStatic
        fun random(): ConsoleColors {
            return VALUES[RANDOM.nextInt(SIZE)]
        }

        /**
         * Return random color string
         * usage <code>println(randomColor("hello"))</code>
         */
        @JvmStatic
        fun randomColor(string: String): String {
            return styler(random(), string)
        }

        @JvmStatic
        fun println(vararg obj: Any) {
            val build = mutableListOf<Any>()
            obj.forEach {
                build.add(styler(random(), it.toString()))
            }
            print(build)
        }

        @JvmStatic
        fun println(obj: Any) {
            when (obj) {
                is Boolean -> {
                    printBoolean(obj)
                }
                is String -> {
                    printstr(obj)
                }
                is Int -> {
                    printint(obj)
                }
                else -> {
                    print(styler(random(), obj.toString()))
                }
            }
        }

        @JvmStatic
        fun printBoolean(boolean: Boolean) {
            if (boolean) {
                print(styler(GREEN, boolean.toString()))
            } else {
                print(styler(RED, boolean.toString()))
            }
        }
    }
}