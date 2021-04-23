import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.nio.charset.Charset

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