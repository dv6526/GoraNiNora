package si.uni_lj.fri.pbd.sensecontext.Weather

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.TAG
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import si.uni_lj.fri.pbd.sensecontext.data.WeatherHour

class ParseWeatherXML {

    private val weather = ArrayList<WeatherHour>()
    private var weatherHour: WeatherHour? = null
    private var text: String? = null
    private var date: Date? = null
    private lateinit var oblacnost: String
    private var vremenski_pojav: String? = null
    private var intenzivnost: String? = null
    private var t_500: Int = 0
    private var t_1000: Int = 0
    private var t_1500: Int = 0
    private var t_2000: Int = 0
    private var t_2500: Int = 0
    private var t_3000: Int = 0
    private var meja_snezenja: Int = 0
    private var w_500: Int = 0
    private var w_1000: Int = 0
    private var w_1500: Int = 0
    private var w_2000: Int = 0
    private var w_2500: Int = 0
    private var w_3000: Int = 0
    private var obmocje: String? = null


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
                    XmlPullParser.START_TAG ->
                        if (tagname.equals("metData")) {
                        //weatherHour = WeatherHour()
                        } else if (tagname.equals("rr_decodeText")) {
                            Log.d(TAG, "here")
                        }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> {
                        if (tagname.equals("metData")) {
                            if (obmocje != null) {
                                weatherHour = si.uni_lj.fri.pbd.sensecontext.data.WeatherHour(0, date!!, oblacnost, vremenski_pojav, intenzivnost,t_500, t_1000, t_1500, t_2000, t_2500, t_3000, meja_snezenja,w_500, w_1000, w_1500, w_2000, w_2500, w_3000,
                                    obmocje!!
                                )
                                weatherHour?.let { weather.add(it) }
                            }
                        } else if (tagname.equals("valid")) {
                            val formatter = SimpleDateFormat("dd.MM.yyyy H:mm")
                            date = formatter.parse(text!!.replace(" CEST", ""))
                        } else if (tagname.equals("nn_icon")) { // količina oblačnosti
                            oblacnost = text!!
                        } else if (tagname.equals("wwsyn_icon")) {
                            vremenski_pojav = text
                        } else if (tagname.equals("rr_decodeText")) {
                            intenzivnost = text
                        } else if (tagname.equals("t_level_3000_m")) {
                            t_3000 = text!!.toInt()
                        } else if (tagname.equals("t_level_2500_m")) {
                            t_2500 = text!!.toInt()
                        } else if (tagname.equals("t_level_2000_m")) {
                            t_2000 = text!!.toInt()
                        } else if (tagname.equals("t_level_1500_m")) {
                            t_1500 = text!!.toInt()
                        } else if (tagname.equals("t_level_1000_m")) {
                            t_1000 = text!!.toInt()
                        } else if (tagname.equals("t_level_500_m")) {
                            t_500 = text!!.toInt()
                        } else if (tagname.equals("ffVal_level_3000_m")) {
                            w_3000 = text!!.toInt()
                        } else if (tagname.equals("ffVal_level_2500_m")) {
                            w_2500 = text!!.toInt()
                        } else if (tagname.equals("ffVal_level_2000_m")) {
                            w_2000 = text!!.toInt()
                        } else if (tagname.equals("ffVal_level_1500_m")) {
                            w_1500 = text!!.toInt()
                        } else if (tagname.equals("ffVal_level_1000_m")) {
                            w_1000 = text!!.toInt()
                        } else if (tagname.equals("ffVal_level_500_m")) {
                            w_500 = text!!.toInt()
                        } else if (tagname.equals("sl_alt")) {
                            meja_snezenja = text!!.toInt()
                        } else if (tagname.equals("domain_meteosiId")) {
                            when (text) {
                                "SI_KAMNIK-SAVINJA-ALPS_" -> obmocje = text
                                "SI_KARAVANKE-ALPS_" -> obmocje = text
                                "SI_JULIAN-ALPS_SOUTH-WEST_" -> obmocje = text
                                "SI_JULIAN-ALPS_" -> obmocje = text
                                else -> obmocje = null
                            }
                        }
                        text = null
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