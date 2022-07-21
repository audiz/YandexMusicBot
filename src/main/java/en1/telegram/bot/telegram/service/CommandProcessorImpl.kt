package en1.telegram.bot.telegram.service

import en1.common.ERROR_UNKNOWN_CALLBACK
import en1.common.EnvConfiguration
import en1.common.ResultOf
import en1.telegram.bot.telegram.callback.*
import en1.telegram.bot.telegram.music.CallbackMusicActions
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

/**
 * Process bot text messages, files, callbacks...
 * */
@Component
class CommandProcessorImpl(val envConfiguration: EnvConfiguration, private val musicService: CallbackMusicActions, private val captchaService: CaptchaService,
                           private val messageSender: MessageSender) : CommandProcessor {
    private val logger = LoggerFactory.getLogger(CommandProcessorImpl::class.java)

    /**
     * Handle non-command request
     * */
    override fun processNonCommand(update: Update, absSender: DefaultAbsSender) {
        if (update.hasCallbackQuery()) {
            val userId = update.callbackQuery.from.id
            val chatId = update.callbackQuery.message.chatId.toString()
            val callback = update.callbackQuery.data.decodeCallback()
            val keyboardList = update.callbackQuery.message.replyMarkup.keyboard
            if (envConfiguration.getAllowedUsers().contains(userId.toString())) {
                processCallback(callback, userId, chatId, absSender, keyboardList)
            } else {
                messageSender.sendCommandUnknown(chatId, absSender)
            }
        } else {
            val msg: Message = update.message ?: update.editedMessage
            val userId = msg.from.id
            val chatId = msg.chatId.toString()

            if (msg.hasDocument() && msg.document.fileName.lowercase().endsWith(".fit")) {
                messageSender.sendFitDoc(msg, update, absSender)
            } else {
                processStringMsg(userId, chatId, msg.text, absSender)
            }
        }
    }

    /**
     * Process direct user input to search tracks or answer captcha.
     * */
    private fun processStringMsg(userId: Long, chatId: String, text: String, absSender: DefaultAbsSender) {
        try {
            if (envConfiguration.getAllowedUsers().contains(userId.toString())) {
                val result = if (captchaService.containsKey(userId)) {
                    logger.info("Process captcha msg = {}", text)
                    val captchaResult = captchaService.get(userId)
                    captchaService.remove(userId)
                    musicService.answerCaptcha(chatId, text, captchaResult)
                } else {
                    musicService.searchByString(chatId, text)
                }
                messageSender.sendMessageAnswer(userId, chatId, result, absSender)
            } else {
                messageSender.sendCommandUnknown(chatId, absSender)
            }
        } catch (e: Exception) {
            logger.error("processStringMsg exception: {}", e.stackTraceToString())
            messageSender.sendInternalError(chatId, absSender)
        }
    }

    /**
     * Answer on different callback types for user
     * */
    private fun processCallback(callback: Callbacks, userId: Long, chatId: String, absSender: DefaultAbsSender, keyboardList: List<List<InlineKeyboardButton>>? = null) {
        val callbackResult = when (callback) {
            is TrackCallback -> musicService.document(userId, chatId, callback, keyboardList!!)
            is SearchTrackWithPagesCallback -> musicService.searchWithPagesMsg(userId, chatId, callback)
            is ArtistTrackWithPagesCallback -> musicService.artistWithPagesMsg(userId, chatId, callback)
            is SimilarCallback -> musicService.similarMsg(userId, chatId, callback)
            is PlaylistCallback -> musicService.playlist(chatId, callback)
            is UnknownCallback -> ResultOf.Failure("None callback", ERROR_UNKNOWN_CALLBACK)
        }
        messageSender.sendMessage(userId, chatId, callbackResult, absSender)
    }
}