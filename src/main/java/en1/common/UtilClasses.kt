package en1.common

import en1.telegram.bot.telegram.callback.Callback
import en1.telegram.bot.telegram.exceptions.ErrorBuilder
import yandex.dto.domain.CaptchaInfo
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Response States for requests
 * */
sealed class ResultOf<out T> {
    data class Success<out R>(val value: R, val header: Map<String, String>? = null): ResultOf<R>()
    data class Failure(val errorBuilder: ErrorBuilder): ResultOf<Nothing>()
    data class Captcha(val captcha: CaptchaInfo, var callback: Callback? = null): ResultOf<Nothing>()
}


/**
 * Tun [block] to return non success result instantly
 * */
inline fun <reified T> ResultOf<T>.returnNok(block: (ResultOf<Nothing>) -> Unit) {
    if (this is ResultOf.Failure) {
        return block(this)
    }
    if (this is ResultOf.Captcha) {
        return block(this)
    }
}

fun String.urlEncode(): String = URLEncoder.encode(this, java.nio.charset.StandardCharsets.UTF_8.toString())
fun String.urlDecode(): String = URLDecoder.decode(this, java.nio.charset.StandardCharsets.UTF_8.toString())