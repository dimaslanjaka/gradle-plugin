package com.dimaslanjaka.gradle.versioning

import java.util.*

class VersionComparator : Comparator<Any?> {
    fun equals(o1: Any?, o2: Any?): Boolean {
        return compare(o1, o2) == 0
    }

    override fun compare(o1: Any?, o2: Any?): Int {
        val version1 = o1 as String?
        val version2 = o2 as String?
        val tokenizer1 = VersionTokenizer(version1)
        val tokenizer2 = VersionTokenizer(version2)
        var number1 = 0
        var number2 = 0
        var suffix1: String? = ""
        var suffix2: String? = ""
        while (tokenizer1.MoveNext()) {
            if (!tokenizer2.MoveNext()) {
                do {
                    number1 = tokenizer1.getNumber()
                    suffix1 = tokenizer1.getSuffix()
                    if (suffix1 != null) {
                        if (number1 != 0 || suffix1.isNotEmpty()) {
                            // Version one is longer than number two, and non-zero
                            return 1
                        }
                    }
                } while (tokenizer1.MoveNext())

                // Version one is longer than version two, but zero
                return 0
            }
            number1 = tokenizer1.getNumber()
            suffix1 = tokenizer1.getSuffix()
            number2 = tokenizer2.getNumber()
            suffix2 = tokenizer2.getSuffix()
            if (number1 < number2) {
                // Number one is less than number two
                return -1
            }
            if (number1 > number2) {
                // Number one is greater than number two
                return 1
            }
            val empty1 = suffix1!!.isEmpty()
            val empty2 = suffix2!!.isEmpty()
            if (empty1 && empty2) continue  // No suffixes
            if (empty1) return 1 // First suffix is empty (1.2 > 1.2b)
            if (empty2) return -1 // Second suffix is empty (1.2a < 1.2)

            // Lexical comparison of suffixes
            val result = suffix1.compareTo(suffix2)
            if (result != 0) return result
        }
        if (tokenizer2.MoveNext()) {
            do {
                number2 = tokenizer2.getNumber()
                suffix2 = tokenizer2.getSuffix()
                if (suffix2 != null) {
                    if (number2 != 0 || suffix2.isNotEmpty()) {
                        // Version one is longer than version two, and non-zero
                        return -1
                    }
                }
            } while (tokenizer2.MoveNext())

            // Version two is longer than version one, but zero
            return 0
        }
        return 0
    }
}