package bot.telegram.service

import bot.common.ResultOf

interface CaptchaService {
    fun put(userId: Long, captcha: ResultOf.Captcha)
    fun containsKey(userId: Long): Boolean
    fun remove(userId: Long)
    fun get(userId: Long): ResultOf.Captcha
}