package cn.renyuzhuo.easygradle.updater

class GradleItem {

    var version: String? = null
    var buildTime: String? = null
    var rcFor: String? = null
    var milestoneFor: String? = null
    var downloadUrl: String? = null
    var checksumUrl: String? = null
    var wrapperChecksumUrl: String? = null

    var current: Boolean = false
    var snapshot: Boolean = false
    var nightly: Boolean = false
    var releaseNightly: Boolean = false
    var activeRc: Boolean = false
    var broken: Boolean = false

    fun toShowString(): String {
        return "最新版本 Gradle：" + version!!
    }

    override fun toString(): String {
        return "GradleItem{" +
                "version='" + version + '\''.toString() +
                ", buildTime='" + buildTime + '\''.toString() +
                ", rcFor='" + rcFor + '\''.toString() +
                ", milestoneFor='" + milestoneFor + '\''.toString() +
                ", downloadUrl='" + downloadUrl + '\''.toString() +
                ", checksumUrl='" + checksumUrl + '\''.toString() +
                ", wrapperChecksumUrl='" + wrapperChecksumUrl + '\''.toString() +
                ", current=" + current +
                ", snapshot=" + snapshot +
                ", nightly=" + nightly +
                ", releaseNightly=" + releaseNightly +
                ", activeRc=" + activeRc +
                ", broken=" + broken +
                '}'.toString()
    }
}
