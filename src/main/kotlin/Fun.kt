import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.gradle.api.Project
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


/**
 * <a href="https://stackoverflow.com/a/56388957">https://stackoverflow.com/a/56388957</a>
 * <code>
 *     jsonString.deserialize(gson, object : TypeToken<MutableList<ResultOffline2>>() {})
 * </code>
 */
@Suppress("unused")
fun <T> String.deserialize(gson: Gson, typeToken: TypeToken<T>): T {
    val type = typeToken.type
    return try {
        gson.fromJson<T>(this, type)
    } catch (e: JsonSyntaxException) {
        if (e.message?.contains("duplicate key") == true) {
            gson.toJson(deserialize(gson, object : TypeToken<Any>() {})).deserialize(gson, typeToken)
        } else {
            throw e
        }
    }
}

/**
 * Direct deserialize json with GSON
 */
fun <T> File.gson(typeToken: TypeToken<T>): T {
    return this.readText(Charset.defaultCharset()).deserialize(gson(), typeToken)
}

/**
 * Direct to json string of class reflection
 */
fun Class<*>.toJsonString(): String {
    return gson().toJson(this)
}

/**
 * Get file modified in seconds format
 */
fun File.modifiedSecondAgo(): Long {
    return this.lastModified() / 1000
}

fun File.modifiedMinuteAgi() {

}

fun Long.toLongDateFormat(): LongDateFormat {
    return LongDateFormat(this)
}

fun main() {
    println(File("build").modifiedSecondAgo())
}

/**
 * Google gson instance
 */
fun gson(): Gson = GsonBuilder()
    .disableHtmlEscaping()
    .setPrettyPrinting()
    .create()

/**
 * Direct print/debug of list
 */
fun List<*>.println() {
    this.forEach {
        kotlin.io.println(it ?: "NULL")
    }
}

/**
 * Direct print/debug of map
 */
fun Map<*, *>.println() {
    this.forEach {
        kotlin.io.println(it)
    }
}

fun String.println() {
    kotlin.io.println(this)
}

/**
 * Is Android Project?
 */
fun Project.isAndroid(): Boolean {
    return this.plugins.hasPlugin("com.android.application") ||
            this.plugins.hasPlugin("com.android.library")
}

object Fun {
    @JvmStatic
    fun println(vararg Obj: Any) {
        Obj.forEach {
            kotlin.io.println(it)
        }
    }
}

class LongDateFormat(l: Long) {
    var hours: Long = 0
    var days: Long = 0
    var weeks: Long = 0
    var minutes: Long = 0
    var seconds: Long = 0
    val millis = l
    val date = java.util.Date(l)

    init {
        val now = java.util.Date()
        minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - l)
        seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - l)
        hours = TimeUnit.MILLISECONDS.toHours(now.time - l)
        days = TimeUnit.MILLISECONDS.toDays(now.time - l)
        weeks = days / 7
    }

    override fun toString(): String {
        return gson().toJson(this)
    }
}