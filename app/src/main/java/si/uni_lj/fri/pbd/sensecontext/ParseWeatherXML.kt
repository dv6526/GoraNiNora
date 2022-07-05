package si.uni_lj.fri.pbd.sensecontext

import android.os.Build
import androidx.annotation.RequiresApi
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ParseWeatherXML {

    private val weather = ArrayList<WeatherHour>()
    private var weatherHour: WeatherHour? = null
    private var text: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    fun parse(input: InputStream): ArrayList<WeatherHour> {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(input, null)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals("metData")) {
                        weatherHour = WeatherHour()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG ->
                        if (tagname.equals("metData")) {
                            weatherHour?.let { weather.add(it) }
                        } else if (tagname.equals("valid")) {
                            val formatter = SimpleDateFormat("dd.MM.yyyy H:mm")
                            val date = formatter.parse(text?.replace(" CEST", ""))
                            weatherHour?.date = date
                        } else if (tagname.equals("nn_icon-wwsyn_icon")) {
                            weatherHour?.oblacnost_pojav = text
                        } else if (tagname.equals("t_level_3000_m")) {
                            weatherHour?.t_3000 = text?.toInt()
                        } else if (tagname.equals("t_level_2500_m")) {
                            weatherHour?.t_2500 = text?.toInt()
                        } else if (tagname.equals("t_level_2000_m")) {
                            weatherHour?.t_2000 = text?.toInt()
                        } else if (tagname.equals("t_level_1500_m")) {
                            weatherHour?.t_1500 = text?.toInt()
                        } else if (tagname.equals("t_level_1000_m")) {
                            weatherHour?.t_1000 = text?.toInt()
                        } else if (tagname.equals("ffVal_level_3000_m")) {
                            weatherHour?.w_3000 = text?.toInt()
                        } else if (tagname.equals("ffVal_level_2500_m")) {
                            weatherHour?.w_2500 = text?.toInt()
                        } else if (tagname.equals("ffVal_level_2000_m")) {
                            weatherHour?.w_2000 = text?.toInt()
                        } else if (tagname.equals("ffVal_level_1500_m")) {
                            weatherHour?.w_1500 = text?.toInt()
                        } else if (tagname.equals("ffVal_level_1000_m")) {
                            weatherHour?.w_1000 = text?.toInt()
                        } else if (tagname.equals("sl_alt")) {
                            weatherHour?.meja_snezenja = text?.toInt()
                        }
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return weather
    }
}