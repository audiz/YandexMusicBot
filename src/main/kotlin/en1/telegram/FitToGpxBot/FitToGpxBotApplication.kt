package en1.telegram.FitToGpxBot

import en1.telegram.FitToGpxBot.telegram.Bot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@SpringBootApplication
class FitToGpxBotApplication

private val getenv = System.getenv()

fun main(args: Array<String>) {
	runApplication<FitToGpxBotApplication>(*args)
	val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
	botsApi.registerBot(Bot(getenv!!.get("BOT_NAME")!!, getenv.get("BOT_TOKEN")!!))
}
