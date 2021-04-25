package collections

import gson

fun Map<*, *>.toJson(): String {
    return gson().toJson(this) ?: "{}"
}