package yandex.dto

import yandex.dto.domain.CaptchaInfo

data class CaptchaDTO(val type: String, val captcha: CaptchaInfo)