package en1.telegram.bot.telegram.exceptions

import java.lang.IllegalArgumentException

/**
 * Исключение, пробрасываемое в случае получения невалидных настроек выгружаемого файла
 */
class IllegalSettingsException(s: String?) : IllegalArgumentException(s)