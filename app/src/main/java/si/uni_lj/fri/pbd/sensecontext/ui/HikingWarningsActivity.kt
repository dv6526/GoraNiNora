package si.uni_lj.fri.pbd.sensecontext.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import si.uni_lj.fri.pbd.sensecontext.Adapters.WarningsViewPagerAdapter
import si.uni_lj.fri.pbd.sensecontext.MainActivity
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityHikingWarningsBinding

class HikingWarningsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var binding: ActivityHikingWarningsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHikingWarningsBinding.inflate(layoutInflater)
        mViewPager = binding.pager
        setContentView(binding.root)
        tabLayout = binding.tabLayout
        setupViewPager()

        binding.buttonLayout.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }


    }

    fun setupViewPager() {
        var list = listOf<Long>(1, 2, 3, 4)
        var list1 = listOf<String>("NAPIHAN SNEG", "STRMO POBOČJE", "TALJENJE SNEGA", "PRISOJNO POBOČJE")
        mViewPager.adapter = WarningsViewPagerAdapter(this, list)

        TabLayoutMediator(binding.tabLayout, mViewPager) {tab, position->
            tab.text = list1[position]
        }.attach()
    }
}