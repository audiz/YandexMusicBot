package en1.telegram.fit_to_gpx_bot.telegram

import en1.telegram.fit_to_gpx_bot.telegram.commands.service.HelpCommand
import en1.telegram.fit_to_gpx_bot.utils.MyConverterTest
import en1.telegram.fit_to_gpx_bot.utils.Utils
import en1.telegram.fit_to_gpx_bot.telegram.nonCommand.NonCommand
import org.slf4j.LoggerFactory
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

class Bot(botName: String, botToken: String) : TelegramLongPollingCommandBot() {
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
        val msg: Message = update.getMessage()
            if (msg.hasDocument() && msg.getDocument().getFileName().lowercase().endsWith(".fit")) {
            logger.info("msg.getDocument().getFileName() = {}", msg.getDocument().getFileName())
            val doc_id: String = update.getMessage().getDocument().getFileId()
            val doc_name: String = update.getMessage().getDocument().getFileName()
            val chatId: Long = msg.getChatId()
            val getFile = GetFile()
            getFile.setFileId(doc_id)
            try {
                val file: File = execute(getFile)
                val `is`: InputStream = downloadFileAsStream(file)
                val stream: InputStream = MyConverterTest.decode(`is`)
                val sendDocument = SendDocument()
                sendDocument.setChatId(chatId.toString())
                sendDocument.setDocument(InputFile(stream, String.format("%s.gpx", doc_name.substringBeforeLast("."))))
                execute(sendDocument)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val chatId: Long = msg.getChatId()
            val userName: String = Utils.getUserName(msg)
            //val answer: String = nonCommand.nonCommandExecute(chatId, userName, msg.getText())
            setAnswer(chatId, userName, "Sorry! Unknown command")
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
        answer.setText(text)
        answer.setChatId(chatId.toString())
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
        logger.debug("Конструктор суперкласса отработал")
        botUsername = botName
        this.botToken = botToken
        logger.debug("Имя и токен присвоены")
        nonCommand = NonCommand()
        logger.debug("Класс обработки сообщения, не являющегося командой, создан")
        register(HelpCommand("help", "Помощь"))
        logger.debug("Команда help создана")
        logger.info("Бот создан!")
    }
}