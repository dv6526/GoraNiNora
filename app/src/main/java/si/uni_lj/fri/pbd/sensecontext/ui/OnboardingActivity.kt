package si.uni_lj.fri.pbd.sensecontext.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import si.uni_lj.fri.pbd.sensecontext.Adapters.OnboardingViewPagerAdapter
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityMainBinding
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var mViewPager: ViewPager2
    private lateinit var btnNextStep: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mViewPager = binding.viewPager
        btnNextStep = binding.next


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
                finish()
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }


    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }
}