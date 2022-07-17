package en1.telegram.bot.telegram.service

import en1.common.ResultOf

interface CaptchaService {
    fun put(userId: Long, captcha: ResultOf.Captcha)
    fun containsKey(userId: Long): Boolean
    fun remove(userId: Long)
    fun get(userId: Long): ResultOf.Captcha
}