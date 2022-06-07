package si.uni_lj.fri.pbd.sensecontext

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import si.uni_lj.fri.pbd.sensecontext.ForegroundService.Companion.ACTION_START
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private var serviceStarted : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.page_2 -> {
                    replaceFragment((SensorsFragment()))
                    //Start service
                    val i = Intent(applicationContext, ForegroundService::class.java)
                    i.action = ACTION_START
                    if (!ForegroundService.IS_RUNNING) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(i)
                        } else {
                            startService(i)
                        }
                    }

                    true
                }
                else -> false
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.frame.id, fragment).commit()
    }






}