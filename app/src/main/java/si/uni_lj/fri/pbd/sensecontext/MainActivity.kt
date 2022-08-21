package si.uni_lj.fri.pbd.sensecontext

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import si.uni_lj.fri.pbd.sensecontext.Weather.AvalancheBulletinWorker
import si.uni_lj.fri.pbd.sensecontext.Weather.WeatherWorker
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.DatabaseDao
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import si.uni_lj.fri.pbd.sensecontext.data.rules.*
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityMainBinding
import si.uni_lj.fri.pbd.sensecontext.fragments.HistoryFragment
import si.uni_lj.fri.pbd.sensecontext.fragments.SettingsFragment
import si.uni_lj.fri.pbd.sensecontext.fragments.SettingsFragment.Companion.neverAskAgainClicked1
import si.uni_lj.fri.pbd.sensecontext.fragments.SettingsFragment.Companion.neverAskAgainClicked2
import si.uni_lj.fri.pbd.sensecontext.fragments.SettingsFragment.Companion.neverAskAgainClicked3
import si.uni_lj.fri.pbd.sensecontext.fragments.SettingsFragment.Companion.switch1
import si.uni_lj.fri.pbd.sensecontext.fragments.SettingsFragment.Companion.switch2
import si.uni_lj.fri.pbd.sensecontext.fragments.SettingsFragment.Companion.switch3
import si.uni_lj.fri.pbd.sensecontext.fragments.WarningsFragment
import si.uni_lj.fri.pbd.sensecontext.ui.OnboardingActivity
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity1"
        const val TRANSITION_RECEIVER_ACTION = "si.uni_lj.fri.pbd.sensecontext.RESULT_RECEIVE"
        const val CHANNEL_ID_WARNING = "si.uni_lj.fri.pbd.sensecontext.CHANNEL_ID_WARNING"
        const val CHANNEL_ID_NOTIFY = "si.uni_lj.fri.pbd.sensecontext.CHANNEL_ID_NOTIFY"
        const val CHANNEL_ID_ACTIVITY_TRANSITION = "si.uni_lj.fri.pbd.sensecontext.CHANNEL_ID_ACTIVITY_TRANSITION"

    }

    private lateinit var binding: ActivityMainBinding
    lateinit var db: ApplicationDatabase
    lateinit var dao: DatabaseDao
    val processingScope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel1()
        createNotificationChannel2()
        createNotificationChannel3()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HistoryFragment())
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    replaceFragment(HistoryFragment())
                    //sendJobAPI()
                    val intent =
                        Intent(applicationContext, OnboardingActivity::class.java)
                    startActivity(intent)

                    true
                }
                R.id.page_2 -> {
                    replaceFragment((WarningsFragment()))

                    true
                }
                R.id.page_3 -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        // show onboarding screen first time user opens app
        val pref = getSharedPreferences("pref", MODE_PRIVATE)
        if (!pref.getBoolean("onboarding", false)) {
            val intent = Intent(applicationContext, OnboardingActivity::class.java).putExtra("request_permissions", true)
            startActivity(intent)

            with(pref.edit()) {
                putBoolean("onboarding", true)
                apply()
            }
        }

        //requestPermissions()
        processingScope.launch { prepopulateDatabaseWithRules() }
    }



    override fun onStart() {
        super.onStart()
        setWeatherUpdatesTest()
        TrackingHelper.requestActivityTransitionUpdates(this)
        MatchRules.matchRules(this, false)
    }




    fun setWeatherUpdatesTest() {
        val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>().build()
        val avalancheBulletinRequest = OneTimeWorkRequestBuilder<AvalancheBulletinWorker>().build()
        WorkManager.getInstance().enqueue(workRequest)
        WorkManager.getInstance().enqueue(avalancheBulletinRequest)
    }

    fun setWeatherUpdates() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, 17)
        dueDate.set(Calendar.MINUTE, 49)
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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("SelectedItemId", binding.bottomNavigationView.getSelectedItemId())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selectedItemId = savedInstanceState.getInt("SelectedItemId")
        binding.bottomNavigationView.setSelectedItemId(selectedItemId)
    }

    fun createNotificationChannel1() {
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

    fun createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.resources.getString(R.string.channel_name_notification)
            val descriptionText = applicationContext.resources.getString(R.string.channel_description_notification)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_NOTIFY, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannel3() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.resources.getString(R.string.channel_name)
            val descriptionText = applicationContext.resources.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_ACTIVITY_TRANSITION, name, importance).apply {
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
                val rule1 = repository.addRule(Rule(0L, rule.aspect, rule.min_slope, rule.max_slope, rule.elevation_min, rule.elevation_max, rule.hour_min, rule.hour_max, rule.user_hiking, rule.av_area_id, rule.notification_name, rule.notification_text))
                for (wd in rule.weather_descriptions) {
                    repository.addWeatherDescription(
                        WeatherDescription(0L, rule1, wd!!.day_delay, wd.temp_avg_min, wd.temp_avg_max, wd.hour_min,
                        wd.hour_max, wd.oblacnost, wd.vremenski_pojav, wd.intenzivnost, wd.elevation
                    )
                    )
                }
                for (pattern in rule.patterns) {
                    repository.addPatternRule(PatternRule(0L, rule1, pattern!!.day_delay, pattern.hour_max, pattern.hour_min, pattern.pattern_id))
                }

                for (problem in rule.problems) {
                    repository.addProblemRule(ProblemRule(0L, rule1,  problem!!.check_elevation, problem.day_delay, problem.hour_max, problem.hour_min, problem.problem_id))
                }

                for (danger in rule.dangers) {
                    repository.addDangerRule(DangerRule(0L, rule1, danger!!.check_elevation, danger.day_delay, danger.am, danger.value))
                }
            }

            //testRules()


            //val rule1 = dao.add_rule(Rule(0L, "S", 30.0, 45.0, null, null, true))
            //val weather_desc1 = dao.add_weather_description(WeatherDescription(0L, 0, 10.0, 15.0, 8, 12,null, null, null, "1000"))
            //dao.add_rule_weather_description_ref(RuleWeatherDescriptionRef(rule1, weather_desc1))

            with(pref.edit()) {
                putBoolean("rules_populated", true)
                apply()
            }

        }

    }

    fun testRules() {
        val db = ApplicationDatabase.getDatabase(this)
        val weatherDao = db.dao()
        val repository = Repository(weatherDao)

        val rwd = repository.getRulesWithLists()
        val rwdnh = repository.getRulesNotHiking()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_ACTIVITY_PERMISSIONS) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.TAG, "Permission denied")
                val neverAskAgain = !shouldShowRequestPermissionRationale(permissions[0]) // neverAskAgain == true when user denied with never ask again
                if (neverAskAgain && !neverAskAgainClicked1) { // user previously did not clicked neverAskAgain
                    neverAskAgainClicked1 = true
                } else if (neverAskAgain && neverAskAgainClicked1) { // user has previously denied with never ask again
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                }
                switch1.setChecked(false)
            } else {
                Log.d(MainActivity.TAG, "Permission granted")
                TrackingHelper.requestActivityTransitionUpdates(this)
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.TAG, "Permission denied")
                val neverAskAgain = !shouldShowRequestPermissionRationale(permissions[0]) // neverAskAgain == true when user denied with never ask again
                if (neverAskAgain && !neverAskAgainClicked2) { // user previously did not clicked neverAskAgain
                    neverAskAgainClicked2 = true
                } else if (neverAskAgain && neverAskAgainClicked2) { // user has previously denied with never ask again
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                }
                switch2.setChecked(false)
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSIONS_CODE) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.TAG, "Permission denied")
                val neverAskAgain = !shouldShowRequestPermissionRationale(permissions[0]) // neverAskAgain == true when user denied with never ask again
                if (neverAskAgain && !neverAskAgainClicked3) { // user previously did not clicked neverAskAgain
                    neverAskAgainClicked3 = true
                } else if (neverAskAgain && neverAskAgainClicked3) { // user has previously denied with never ask again
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                }
                switch3.setChecked(false)
            }
        }
    }






}