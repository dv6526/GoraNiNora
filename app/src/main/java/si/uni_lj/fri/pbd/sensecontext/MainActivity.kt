package si.uni_lj.fri.pbd.sensecontext

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.sensecontext.JsonObjects.Rules.Rules
import si.uni_lj.fri.pbd.sensecontext.Weather.WeatherWorker
import si.uni_lj.fri.pbd.sensecontext.data.*
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityMainBinding
import si.uni_lj.fri.pbd.sensecontext.fragments.HomeFragment
import si.uni_lj.fri.pbd.sensecontext.fragments.SensorsFragment
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), SensorsFragment.FragmentCallback {
    companion object {
        const val TAG = "MainActivity1"
        const val TRANSITION_RECEIVER_ACTION = "si.uni_lj.fri.pbd.sensecontext.RESULT_RECEIVE"
        const val CHANNEL_ID_WARNING = "si.uni_lj.fri.pbd.sensecontext.CHANNEL_ID_WARNING"
    }

    private lateinit var binding: ActivityMainBinding
    lateinit var db: ApplicationDatabase
    lateinit var dao: DatabaseDao
    val processingScope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannels()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    replaceFragment(HomeFragment())


                    //sendJobAPI()
                    true
                }
                R.id.page_2 -> {
                    replaceFragment((SensorsFragment()))

                    true
                }
                else -> false
            }
        }

        requestPermissions()
        processingScope.launch {  prepopulateDatabaseWithRules() }
    }

    override fun onStart() {
        super.onStart()
        setWeatherUpdatesTest()
    }

    override fun stopActivityTransitionUpdates() {
        removeActivityTransitionUpdates()
    }

    override fun startActivityTransitionUpdates() {
        requestActivityTransitionUpdates()
    }

    fun requestPermissions() {

        if (isPermissionTransitionRecognitionGranted()) {
            requestActivityTransitionUpdates()
            requestPermissionForLocation()
        } else {
            requestPermissionTransitionRecognition()
        }



    }

    fun setWeatherUpdatesTest() {
        val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>().build()
        WorkManager.getInstance().enqueue(workRequest)
    }

    fun setWeatherUpdates() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, 10)
        dueDate.set(Calendar.MINUTE, 13)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val delay = dueDate.timeInMillis - currentDate.timeInMillis
        val constraints =  Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<WeatherWorker>(
            24, TimeUnit.HOURS, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MILLISECONDS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setConstraints(constraints).addTag("weather api").build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("weather_request", ExistingPeriodicWorkPolicy.REPLACE, dailyWorkRequest)
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.frame.id, fragment).commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_ACTIVITY_PERMISSIONS) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission denied")
            } else {
                Log.d(TAG, "Permission granted")
                requestActivityTransitionUpdates()
                requestPermissionForLocation()
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionForLocation()
            }
        }
    }


    override fun onResume() {
        super.onResume()
    }

    fun createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.resources.getString(R.string.channel_name_warning)
            val descriptionText = applicationContext.resources.getString(R.string.channel_description_warning)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_WARNING, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    suspend fun prepopulateDatabaseWithRules() {
        val pref = getSharedPreferences("pref", MODE_PRIVATE)
        if (!pref.getBoolean("rules_populated", false)) {
            db = ApplicationDatabase.getDatabase(this)
            dao = db.dao()
            val repository = Repository(dao)
            lateinit var jsonString: String
            try {
                jsonString = applicationContext.assets.open("rules.json")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: IOException) {
                Log.d(TAG, e.toString())
            }

            val rules = Gson().fromJson(jsonString, Rules::class.java)

            for (rule in rules.rules) {
                val rule1 = repository.addRule(Rule(0L, rule.aspect, rule.min_slope, rule.max_slope, rule.elevation_min, rule.elevation_max, rule.user_hiking, rule.notification_name, rule.notification_name))
                for (wd in rule.weather_descriptions) {
                    val weather_desc1 = repository.addWeatherDescription(WeatherDescription(0L, wd!!.day_delay, wd.temp_avg_min, wd.temp_avg_max, wd.hour_min,
                        wd.hour_max, wd.oblacnost, wd.vremenski_pojav, wd.intenzivnost, wd.elevation))
                    repository.addRuleWeatherDescriptionRef(RuleWeatherDescriptionRef(rule1, weather_desc1))
                }
            }

            testRules()

            //val rule1 = dao.add_rule(Rule(0L, "S", 30.0, 45.0, null, null, true))
            //val weather_desc1 = dao.add_weather_description(WeatherDescription(0L, 0, 10.0, 15.0, 8, 12,null, null, null, "1000"))
            //dao.add_rule_weather_description_ref(RuleWeatherDescriptionRef(rule1, weather_desc1))
            /*
            with(pref.edit()) {
                putBoolean("rules_populated", true)
                apply()
            }
             */
        }

    }

    fun testRules() {
        val db = ApplicationDatabase.getDatabase(this)
        val weatherDao = db.dao()
        val repository = Repository(weatherDao)

        val rwd = repository.getRulesWithWeatherDescription()
        val rwdnh = repository.getRulesWithWeatherDescriptionNotHiking()
    }


}