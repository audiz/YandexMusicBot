package en1.telegram.bot.telegram.service

import java.io.InputStream

interface FitToGpxConverter {
    fun decode(`in`: InputStream): InputStream
}