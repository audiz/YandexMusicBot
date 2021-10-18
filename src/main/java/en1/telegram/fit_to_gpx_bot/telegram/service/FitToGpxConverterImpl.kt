package en1.telegram.fit_to_gpx_bot.telegram.service

import com.garmin.fit.*
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

@Component
class FitToGpxConverterImpl : FitToGpxConverter {

    override fun decode(`in`: InputStream): InputStream {
        val decode = Decode()
        //decode.skipHeader();        // Use on streams with no header and footer (stream contains FIT defn and data messages only)
        //decode.incompleteStream();  // This suppresses exceptions with unexpected eof (also incorrect crc)
        val mesgBroadcaster = MesgBroadcaster(decode)
        val sb = StringBuilder()
        sb.append(out_gpx_head.replace("{creator}", "Bot"))
        sb.append(out_gpx_head1.replace("{time}", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Date())))
        sb.append(out_gpx_head2.replace("{FTIFile}", "test Name").replace("{serialnumber}", 0L.toString()))
        val listener = Listener(sb)
        /* try {
            if (!decode.checkFileIntegrity(in)) {
                throw new RuntimeException("FIT file integrity failed.");
            }
        } catch (RuntimeException e) {
            log.error("Exception Checking File Integrity: ");
            log.error(e.getMessage());
            log.error("Trying to continue...");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/mesgBroadcaster.addListener(listener)
        try {
            decode.read(`in`, mesgBroadcaster, mesgBroadcaster)
        } catch (e: FitRuntimeException) {
            if (decode.invalidFileDataSize) {
                decode.nextFile()
                decode.read(`in`, mesgBroadcaster, mesgBroadcaster)
            } else {
                log.error("Exception decoding file: ")
                log.error(e.message)
                e.printStackTrace()
                try {
                    `in`.close()
                } catch (f: IOException) {
                    throw RuntimeException(f)
                }
            }
        }
        try {
            `in`.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        sb.append(out_gpx_tail1)
        sb.append(out_gpx_tail2)
        return ByteArrayInputStream(sb.toString().toByteArray())
    }

    private class Listener(var activity: StringBuilder) : RecordMesgListener {
        override fun onMesg(mesg: RecordMesg) {
            if (mesg.positionLat != null && mesg.positionLong != null) {
                val lat = mesg.positionLat.toFloat() / CONVERT_GEO_COORDS
                val lon = mesg.positionLong.toFloat() / CONVERT_GEO_COORDS
                activity.append("<trkpt lat=\"{lat}\"".replace("{lat}", java.lang.Float.toString(lat)))
                        .append(" lon=\"{lon}\">".replace("{lon}", java.lang.Float.toString(lon)))
            } else {
                activity.append("<trkpt lat=\"\"" + " lon=\"\">")
            }
            activity.append("<time>{time}</time>".replace("{time}", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(mesg.timestamp.date)))
            if (mesg.altitude != null) {
                activity.append("<ele>{enhanced_altitude}</ele>".replace("{enhanced_altitude}",
                        mesg.altitude.toString()))
            }

            /*boolean extention = mesg.getPower() != null || mesg.getEnhancedSpeed() != null;
            boolean tpextention = mesg.getTemperature() != null || mesg.getHeartRate() != null || mesg.getCadence() != null
                    || mesg.getEnhancedSpeed() != null || mesg.getDistance() != null;

            if(extention || tpextention) {
                activity.append("<extensions>");
                if(mesg.getPower() != null) { activity.append("<power>{power}</power>".replace("{power}", mesg.getPower().toString())); }
                if(mesg.getEnhancedSpeed() != null) { activity.append("<nmea:speed>{enhanced_speed}</nmea:speed>".replace("{enhanced_speed}", mesg.getEnhancedSpeed().toString() ));}
                if(tpextention) { activity.append("<gpxtpx:TrackPointExtension>");
                    if(mesg.getTemperature() != null) {activity.append("<gpxtpx:atemp>{temperature}</gpxtpx:atemp>".replace("{temperature}", mesg.getTemperature().toString()));}
                    if(mesg.getHeartRate() != null) {activity.append("<gpxtpx:hr>{heart_rate}</gpxtpx:hr>".replace("{heart_rate}", mesg.getHeartRate().toString()));}
                    if(mesg.getCadence() != null) {activity.append("<gpxtpx:cad>{cadence}</gpxtpx:cad>".replace("{cadence}", mesg.getCadence().toString()));}
                    if(mesg.getEnhancedSpeed() != null) {activity.append("<gpxtpx:speed>{enhanced_speed}</gpxtpx:speed>".replace("{enhanced_speed}", mesg.getEnhancedSpeed().toString()));}
                    if(mesg.getDistance() != null) {activity.append("<gpxtpx:course>{distance}</gpxtpx:course>".replace("{distance}", mesg.getDistance().toString()));}
                    activity.append("</gpxtpx:TrackPointExtension>"); }
                activity.append("    </extensions>");
            }*/
            activity.append("</trkpt>").append("\n")
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(FitToGpxConverterImpl::class.java)
        val CONVERT_GEO_COORDS = 11930465
        val out_gpx_head = "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"Oregon 400t\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">"
        val out_gpx_head1 = " <metadata>\n  <time>{time}</time>\n </metadata>"
        val out_gpx_head2 = " <trk>\n  <name>{FTIFile}</name>\n  <number>{serialnumber}</number>\n  <trkseg>"
        val out_gpx_tail1 = "  </trkseg>\n </trk>"
        val out_gpx_tail2 = "</gpx>"

        @JvmStatic
        fun main(args: Array<String>) {
            val fitFile = "/home/max/Downloads/20210720212239.fit"
            val `in`: FileInputStream
            `in` = try {
                FileInputStream(fitFile)
            } catch (e: IOException) {
                throw RuntimeException("Error opening file $fitFile [1]")
            }
            val decoder = FitToGpxConverterImpl();
            val `is` = decoder.decode(`in`)
            val targetFile = File("$fitFile.gpx")
            FileUtils.copyInputStreamToFile(`is`, targetFile)
            log.info("Decoded FIT file to '{}'", "$fitFile.gpx")
            Thread.sleep(100)
            `in`.close()
            `is`.close()
        }
    }
}