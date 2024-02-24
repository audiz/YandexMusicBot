package bot.yandex.dto

import bot.yandex.dto.domain.CaptchaInfo

data class CaptchaDTO(val type: String, val captcha: CaptchaInfo)