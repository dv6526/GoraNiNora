package si.uni_lj.fri.pbd.GoraNiNora.ui

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import si.uni_lj.fri.pbd.GoraNiNora.*
import si.uni_lj.fri.pbd.GoraNiNora.Adapters.OnboardingViewPagerAdapter
import si.uni_lj.fri.pbd.GoraNiNora.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var mViewPager: ViewPager2
    private lateinit var btnNextStep: TextView
    private var request_permissions: Boolean = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewPager = binding.viewPager
        btnNextStep = binding.next
        request_permissions = intent.getBooleanExtra("request_permissions", false)

        mViewPager.adapter = OnboardingViewPagerAdapter(this, this)

        TabLayoutMediator(binding.pageIndicator, mViewPager) {_, _ ->}.attach()

        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 6) {
                    btnNextStep.text = "Začni"
                } else {
                    btnNextStep.text = "Naprej"
                }
            }

        })

        btnNextStep.setOnClickListener {
            val child_count = mViewPager.childCount
            val cur_item = mViewPager.currentItem
            if (getItem() > 5) {
                if (request_permissions) {
                    HandlePermissions.setActivityAndContext(this, this)
                    HandlePermissions.requestPermissionForTransitionRecognition()
                } else {
                    finish()
                }

            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }


    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_ACTIVITY_PERMISSIONS) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.TAG, "Permission denied")
                finish()
            } else {
                Log.d(MainActivity.TAG, "Permission granted")
                TrackingHelper.requestActivityTransitionUpdates(this)
                HandlePermissions.requestPermissionForFineLocation()
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                HandlePermissions.requestPermissionForBackgroundLocation()
            } else {
                finish()
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSIONS_CODE) {
            finish()
        }
    }


}