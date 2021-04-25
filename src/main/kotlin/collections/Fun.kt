import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.gradle.api.Project
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

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
 * Appends all elements that are not `null` to the given [destination].
 * <a href="https://stackoverflow.com/a/38535537">https://stackoverflow.com/a/38535537</a>
 */
public fun <C : MutableCollection<in T>, T : Any> Iterable<T?>.filterNotNullTo(destination: C): C {
    for (element in this) if (element != null) destination.add(element)
    return destination
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

fun List<*>.getElementFromLast(i: Int): Any? {
    return this[this.size - i];
}

fun URL.fixPath(): URL {
    val ori = this.path
    val pattern: Pattern = Pattern.compile("/{2,}", Pattern.MULTILINE)
    val matcher: Matcher = pattern.matcher(ori)
    val newPath = matcher.replaceAll("/")
    val auth = null
    val fragment = null
    val uri = URI(protocol, auth, host, port, newPath, query, fragment)
    return uri.toURL()
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

fun URL.getHtml(): UrlGetHtmlResult {
    val connection = this.fixPath().openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()
// open the stream and put it into BufferedReader
    // open the stream and put it into BufferedReader
    val br = BufferedReader(
        InputStreamReader(connection.inputStream)
    )
    val html = StringBuilder()
    var inputLine: String?
    while (br.readLine().also { inputLine = it } != null) {
        html.append(inputLine)
    }
    br.close()
    val code: Int = connection.responseCode
    return UrlGetHtmlResult().apply {
        this.code = code
        this.html = html.toString()
    }
}

class UrlGetHtmlResult {
    var code: Int = 0
    lateinit var html: String
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

fun main() {
    println(File("build").modifiedSecondAgo())
    val url = URL("http://google.com/dsd/sds///sds//ds/dt?hellow=Hax&sgtu#hashx=ert").fixPath()
    println(url)
}