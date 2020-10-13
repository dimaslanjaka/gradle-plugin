package com.dimaslanjaka.gradle.versioning

import com.dimaslanjaka.gradle.versioning.VersionType

class Semver implements Serializable {

    public int major, minor, build, code = 0
    public String pre, patch = ""

    Semver(String version) {
        def versionParts = version.tokenize('.')
        println versionParts
        if (versionParts.size != 3) {
            throw new IllegalArgumentException("Wrong version format - expected MAJOR.MINOR.PATCH - got ${version}")
        }
        this.major = versionParts[0].toInteger()
        this.minor = versionParts[1].toInteger()

        if (!versionParts.get(2).empty) {
            if (versionParts.get(2).isNumber()) {
                this.patch = versionParts[2].toString()
            } else {
                if (!versionParts[2].matches("[A-Za-z0-9]+")) {
                    String[] tokens = versionParts[2].split("[-_]")
                    this.patch = tokens[0]
                    this.pre = tokens[1]
                } else {
                    this.patch = versionParts[2]
                }
            }
        } else {
            this.patch = "0"
        }

        if (!versionParts.get(3).empty) {
            this.pre = versionParts[3].toString()
        } else {
            this.pre = ""
        }
    }

    Semver(int major, int minor, String patch) {
        this.major = major
        this.minor = minor
        this.patch = patch
    }

    Semver bump(VersionType patchLevel) {
        switch (patchLevel) {
            case VersionType.MAJOR:
                return new Semver(major + 1, 0, "0")
                break
            case VersionType.MINOR:
                return new Semver(major, minor + 1, "0")
                break
            case VersionType.PATCH:
                return new Semver(major, minor, patch.isNumber() ? patch + 1 : patch)
                break
        }
        return new Semver()
    }

    String toString() {
        return "${major}.${minor}.${patch}"
    }

}


/*
def version = new Semver("0.0.1")
println(version.bump(PatchLevel.MAJOR).toString())
println(version.bump(PatchLevel.MINOR).toString())
println(version.bump(PatchLevel.PATCH).toString())
  will output

  1.0.0
  0.1.0
  0.0.2
*/