package yandex.dto.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class CaptchaInfo(
    @JsonProperty("img-url")
    val imgUrl: String,
    val key: String,
    val status: String,
    @JsonProperty("captcha-page")
    val captchaPage: String)