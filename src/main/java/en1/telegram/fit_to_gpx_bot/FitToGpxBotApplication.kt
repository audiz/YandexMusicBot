package en1.telegram.fit_to_gpx_bot

import com.garmin.fit.Decode
import en1.telegram.fit_to_gpx_bot.telegram.Bot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@SpringBootApplication
open class FitToGpxBotApplication

private val getenv = System.getenv()

fun main(args: Array<String>) {
	runApplication<FitToGpxBotApplication>(*args)
	val decode = Decode()
	val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
	botsApi.registerBot(Bot(getenv!!.get("BOT_NAME")!!, getenv.get("BOT_TOKEN")!!))
}
