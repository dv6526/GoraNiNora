package si.uni_lj.fri.pbd.sensecontext

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import si.uni_lj.fri.pbd.sensecontext.Receivers.DetectedTransitionReceiver
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val TRANSITION_RECEIVER_ACTION = "si.uni_lj.fri.pbd.sensecontext.RESULT_RECEIVE"
    }

    private lateinit var navigation: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private var serviceStarted : Boolean = false
    private lateinit var receiverDetected: DetectedTransitionReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        receiverDetected = DetectedTransitionReceiver()

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
                    //check for permissions
                    if (isPermissionGranted()) {
                        setActivityTransitionDetection()
                    } else {
                        requestPermission()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_ACTIVITY_PERMISSIONS) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission denied")
            } else {
                Log.d(TAG, "Permission granted")
                setActivityTransitionDetection()
            }
        }
    }

    private fun setActivityTransitionDetection() {
        Log.d(TAG, "Starting activity recognition...")
        requestActivityTransitionUpdates()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiverDetected, IntentFilter(TRANSITION_RECEIVER_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiverDetected)
    }

}