package bot.common

enum class ErrorKind(val description: String) {
    YANDEX_JSON_PARSE("YDX_JSN_PRS"),
    YANDEX_PLAYLIST_PARSE("YDX_PLST_PRS"),
    YANDEX_REQUEST_FAILED("YDX_REQ"),
    APP_INTERNAL("APP_INT");
}