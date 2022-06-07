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
