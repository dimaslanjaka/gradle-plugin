package com.dimaslanjaka.gradle.versioning

class VersionCompare(version: String?) : Comparable<VersionCompare?> {
    private val version: String?
    fun get(): String? {
        return version
    }

    override fun compareTo(that: VersionCompare?): Int {
        if (that == null) {
            return 1
        }
        val thisParts: Array<String?> = this.get()!!.split("\\.".toRegex()).toTypedArray()
        val thatParts: Array<String?> = that.get()!!.split("\\.".toRegex()).toTypedArray()
        val length = Math.max(thisParts.size, thatParts.size)
        for (i in 0 until length) {
            val thisPart = if (i < thisParts.size) thisParts[i]!!.toInt() else 0
            val thatPart = if (i < thatParts.size) thatParts[i]!!.toInt() else 0
            if (thisPart < thatPart) return -1
            if (thisPart > thatPart) return 1
        }
        return 0
    }

    override fun equals(that: Any?): Boolean {
        if (this === that) return true
        if (that == null) return false
        return if (this.javaClass != that.javaClass) false else this.compareTo(that as VersionCompare?) == 0
    }

    init {
        requireNotNull(version) { "Version can not be null" }
        require(version.matches(Regex("[0-9]+(\\.[0-9]+)*"))) { "Invalid version format" }
        this.version = version
    }
}