package en1.telegram.fit_to_gpx_bot.telegram.service

import java.io.InputStream

interface FitToGpxConverter {
    fun decode(`in`: InputStream): InputStream
}