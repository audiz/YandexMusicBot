package en1.telegram.bot.telegram.service

import en1.common.ResultOf

interface CaptchaService {
    fun put(userId: Int, captcha: ResultOf.Captcha)
    fun contains(userId: Int): Boolean
    fun remove(userId: Int)
    fun get(userId: Int): ResultOf.Captcha
}