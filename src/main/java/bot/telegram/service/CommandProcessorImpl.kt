package bot.telegram.service

import bot.common.*
import bot.telegram.callback.PlaylistActions
import bot.telegram.callback.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.concurrent.ConcurrentHashMap

/**
 * Process bot text messages, files, callbacks...
 * */
@Component
class CommandProcessorImpl(val envConfiguration: EnvConfiguration,
                           private val playlistActions: PlaylistActions,
                           private val musicDownloadActions: MusicDownloadActions,
                           private val captchaService: CaptchaService,
                           private val messageSender: MessageSender
) {
    private val logger = LoggerFactory.getLogger(CommandProcessorImpl::class.java)
    // TODO improve double executions
    private val lockDoubleCallbacks = ConcurrentHashMap<Long, String>()

    /**
     * Handle non-command request
     * */
    fun processNonCommand(update: Update, absSender: DefaultAbsSender) {
        val (userId, chatId) = getUserAndChatId(update)
        if (update.hasCallbackQuery()) {
            if (lockDoubleCallbacks.containsKey(userId) && (lockDoubleCallbacks[userId] == update.callbackQuery.data)) {
                messageSender.sendCommandLocked(chatId, absSender)
                return
            }
            lockDoubleCallbacks[userId] = update.callbackQuery.data

            val callback = update.callbackQuery.data.decodeCallback()
            val keyboardList = update.callbackQuery.message.replyMarkup.keyboard
            if (envConfiguration.getAllowedUsers().contains(userId.toString())) {
                processCallback(callback, userId, chatId, absSender, keyboardList)
            } else {
                messageSender.sendNotAllowed(chatId, absSender)
            }
        } else {
            val msg: Message = update.message ?: update.editedMessage
            processStringMsg(userId, chatId, msg.text, absSender)
        }
    }

    /**
     * Must be refactored
     * */
    private fun getUserAndChatId(update: Update): Pair<Long, String> {
        if (update.hasCallbackQuery()) {
            val userId = update.callbackQuery.from.id
            val chatId = update.callbackQuery.message.chatId.toString()
            return Pair(userId, chatId)
        } else {
            val msg: Message = update.message ?: update.editedMessage
            val userId = msg.from.id
            val chatId = msg.chatId.toString()
            return Pair(userId, chatId)
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
                    playlistActions.answerCaptcha(chatId, text, captchaResult)
                } else {
                    playlistActions.searchByString(userId, chatId, text)
                }
                messageSender.sendMessageAnswer(userId, chatId, result, absSender)
            } else {
                messageSender.sendNotAllowed(chatId, absSender)
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
            is TrackCallback -> musicDownloadActions.downloadMp3(userId, chatId, callback, keyboardList!!)
            is SearchTrackWithPagesCallback -> playlistActions.searchWithPagesMsg(userId, chatId, callback)
            is ArtistTrackWithPagesCallback -> playlistActions.artistWithPagesMsg(userId, chatId, callback)
            is SimilarCallback -> playlistActions.similarMsg(userId, chatId, callback)
            is PlaylistCallback -> playlistActions.playlist(userId, chatId, callback)
            is DownloadPlaylistCallback -> {
                val result = musicDownloadActions.downloadMp3List(userId, chatId, callback, absSender)
                if (result is ResultOf.Failure) {
                    result
                } else {
                    playlistActions.showDownloadPlaylist(userId, chatId)
                }
            }
            is CancelDownloadPlaylistCallback -> musicDownloadActions.cancelDownloadMp3List(userId, chatId)
            is UnknownCallback -> ResultOf.Failure(ErrorBuilder.newBuilder(ErrorKind.APP_INTERNAL).withCode(404).withDescription("Unknown callback"))
        }
        messageSender.sendMessage(userId, chatId, callbackResult, absSender)
    }
}