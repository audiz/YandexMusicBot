package en1.common

import en1.telegram.bot.telegram.callback.Callback
import yandex.dto.domain.CaptchaInfo

/**
 * Response States for requests
 * */
sealed class ResultOf<out T> {
    data class Success<out R>(val value: R, val header: Map<String, String>? = null): ResultOf<R>()
    data class Failure(val message: String, val code: Int): ResultOf<Nothing>()
    data class Captcha(val captcha: CaptchaInfo, var callback: Callback? = null): ResultOf<Nothing>()
}


/**
 * Tun [block] to return non success result instantly
 * */
public inline fun <reified T> ResultOf<T>.returnNok(block: (ResultOf<Nothing>) -> Unit) {
    if (this is ResultOf.Failure) {
        return block(this)
    }
    if (this is ResultOf.Captcha) {
        return block(this)
    }
}