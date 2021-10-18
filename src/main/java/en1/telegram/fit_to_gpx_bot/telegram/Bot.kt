package en1.telegram.fit_to_gpx_bot.telegram

import en1.telegram.fit_to_gpx_bot.telegram.commands.service.HelpCommand
import en1.telegram.fit_to_gpx_bot.utils.Utils
import en1.telegram.fit_to_gpx_bot.telegram.nonCommand.NonCommand
import en1.telegram.fit_to_gpx_bot.telegram.service.FitToGpxConverter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.File
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.InputStream

@Component
class Bot(val fitToGpxConverter: FitToGpxConverter) : TelegramLongPollingCommandBot() {
    private val logger = LoggerFactory.getLogger(Bot::class.java)
    private val botUsername: String

    override fun getBotUsername(): String {
        return botUsername
    }

    private val botToken: String
    override fun getBotToken(): String {
        return botToken
    }

    private val nonCommand: NonCommand

    /**
     * Ответ на запрос, не являющийся командой
     */
    override fun processNonCommandUpdate(update: Update) {
        val msg: Message = update.message
            if (msg.hasDocument() && msg.document.fileName.lowercase().endsWith(".fit")) {
            logger.info("msg.getDocument().getFileName() = {}", msg.document.fileName)
            val doc_id: String = update.message.document.fileId
            val doc_name: String = update.message.document.fileName
            val chatId: Long = msg.chatId
            val getFile = GetFile()
                getFile.fileId = doc_id
            try {
                val file: File = execute(getFile)
                val `is`: InputStream = downloadFileAsStream(file)
                val stream: InputStream = fitToGpxConverter.decode(`is`)
                val sendDocument = SendDocument()
                sendDocument.chatId = chatId.toString()
                sendDocument.document = InputFile(stream, String.format("%s.gpx", doc_name.substringBeforeLast(".")))
                execute(sendDocument)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val chatId: Long = msg.chatId
            val userName: String = Utils.getUserName(msg)
            //val answer: String = nonCommand.nonCommandExecute(chatId, userName, msg.getText())
            setAnswer(chatId, userName, "Sorry! Unknown command, try /help command")
        }
    }

    /**
     * Отправка ответа
     * @param chatId id чата
     * @param userName имя пользователя
     * @param text текст ответа
     */
    private fun setAnswer(chatId: Long, userName: String, text: String) {
        val answer = SendMessage()
        answer.text = text
        answer.chatId = chatId.toString()
        try {
            execute(answer)
        } catch (e: TelegramApiException) {
            logger.error(java.lang.String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.message, userName))
            e.printStackTrace()
        }
    }

    companion object {

    }

    init {
        val getenv = System.getenv()
        logger.debug("Конструктор суперкласса отработал")
        botUsername = getenv!!["BOT_NAME"]!!
        this.botToken = getenv["BOT_TOKEN"]!!
        logger.debug("Имя и токен присвоены")
        nonCommand = NonCommand()
        logger.debug("Класс обработки сообщения, не являющегося командой, создан")
        register(HelpCommand("help", "Помощь"))
        logger.debug("Команда help создана")
        logger.info("Бот создан!")
    }
}