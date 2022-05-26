package en1.common

/**
 * Response States for requests
 * */
sealed class ResultOf<out T> {
    data class success<out R>(
        val value: R,
        val header: Map<String, String>? = null
    ): ResultOf<R>()
    data class failure(
        val message: String,
        val code: Int
    ): ResultOf<Nothing>()
}
