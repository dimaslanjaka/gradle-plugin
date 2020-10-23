package com.dimaslanjaka.gradle.versioning

import org.gradle.internal.impldep.org.apache.maven.artifact.versioning.DefaultArtifactVersion

object VersionCheck {
    private val cmp: VersionComparator? = VersionComparator()
    fun decimalOnly(currentVersion: String?, newerVersion: String?): Boolean {
        val current = DefaultArtifactVersion(currentVersion)
        val newer = DefaultArtifactVersion(newerVersion)
        val isNewerAvailable = current.compareTo(newer) > 0
        println("Newer Version Is: $isNewerAvailable")
        return isNewerAvailable
    }

    @JvmStatic
    fun test(versions: Array<String?>?) {
        if (versions != null) {
            for (i in versions.indices) {
                for (j in i until versions.size) {
                    test(versions[i], versions[j])
                }
            }
        }
    }

    /**
     * ```
     * String result = VersionCheck.test("1.0.0", "1.1.1");
     * System.out.println("Current Version Is " + result);
     * ```
     *
     * @param v1 Current Version
     * @param v2 Other Version
     * @return LESS GREATER EQUALS
     */
    @JvmStatic
    fun test(v1: String?, v2: String?): String? {
        val result = cmp!!.compare(v1, v2)
        var res = "EQUALS"
        var op = "=="
        if (result < 0) {
            op = "<"
            res = "LESS"
        }
        if (result > 0) {
            op = ">"
            res = "GREATER"
        }
        System.out.printf("%s %s %s is %s\n", v1, op, v2, res)
        return res
    }
}