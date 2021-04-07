package vsukharew.vkclient.common.extension

fun String.Companion.randomAlphanumericString(length: Int): String =
    (('A'..'Z') + ('a'..'z') + ('0'..'9'))
        .shuffled()
        .take(length)
        .joinToString(separator = "")

val String.Companion.EMPTY: String
    get() = ""
val String.Companion.COMMA: String
    get() = ","