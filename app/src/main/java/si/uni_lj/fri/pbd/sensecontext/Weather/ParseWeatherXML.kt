package si.uni_lj.fri.pbd.sensecontext.Weather

import android.os.Build
import androidx.annotation.RequiresApi
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import si.uni_lj.fri.pbd.sensecontext.data.WeatherHour

class ParseWeatherXML {

    private val weather = ArrayList<WeatherHour>()
    private var weatherHour: WeatherHour? = null
    private lateinit var text: String
    private var date: Date? = null
    private lateinit var oblacnost: String
    private var vremenski_pojav: String? = null
    private var intenzivnost: String? = null
    private var t_1000: Int = 0
    private var t_1500: Int = 0
    private var t_2000: Int = 0
    private var t_2500: Int = 0
    private var t_3000: Int = 0
    private var meja_snezenja: Int = 0
    private var w_1000: Int = 0
    private var w_1500: Int = 0
    private var w_2000: Int = 0
    private var w_2500: Int = 0
    private var w_3000: Int = 0


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
                        //weatherHour = WeatherHour()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG ->
                        if (tagname.equals("metData")) {
                            weatherHour = si.uni_lj.fri.pbd.sensecontext.data.WeatherHour(0, date!!, oblacnost, vremenski_pojav, intenzivnost, t_1000, t_1500, t_2000, t_2500, t_3000, meja_snezenja, w_1000, w_1500, w_2000, w_2500, w_3000)
                            weatherHour?.let { weather.add(it) }
                        } else if (tagname.equals("valid")) {
                            val formatter = SimpleDateFormat("dd.MM.yyyy H:mm")
                            date = formatter.parse(text.replace(" CEST", ""))
                        } else if (tagname.equals("nn_icon-wwsyn_icon")) {
                            val list = text.split("_")
                            oblacnost = list[0]
                            if (list.size == 2) {
                                val niz = list[1].replace("SH", "")
                                val idx = firstCapital(niz)
                                intenzivnost = niz.substring(0, idx)
                                vremenski_pojav = niz.substring(idx)
                            }
                        } else if (tagname.equals("t_level_3000_m")) {
                            t_3000 = text.toInt()
                        } else if (tagname.equals("t_level_2500_m")) {
                            t_2500 = text.toInt()
                        } else if (tagname.equals("t_level_2000_m")) {
                            t_2000 = text.toInt()
                        } else if (tagname.equals("t_level_1500_m")) {
                            t_1500 = text.toInt()
                        } else if (tagname.equals("t_level_1000_m")) {
                            t_1000 = text.toInt()
                        } else if (tagname.equals("ffVal_level_3000_m")) {
                            w_3000 = text.toInt()
                        } else if (tagname.equals("ffVal_level_2500_m")) {
                            w_2500 = text.toInt()
                        } else if (tagname.equals("ffVal_level_2000_m")) {
                            w_2000 = text.toInt()
                        } else if (tagname.equals("ffVal_level_1500_m")) {
                            w_1500 = text.toInt()
                        } else if (tagname.equals("ffVal_level_1000_m")) {
                            w_1000 = text.toInt()
                        } else if (tagname.equals("sl_alt")) {
                            meja_snezenja = text.toInt()
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

    fun firstCapital(niz: String): Int {
        var idx = 0
        for (letter in niz) {
            if (letter.isUpperCase())
                break
            idx += 1
        }
        return idx
    }
}