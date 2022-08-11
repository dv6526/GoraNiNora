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
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.databinding.ActivityHikingWarningsBinding
import java.util.*

class HikingWarningsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var mViewPager: ViewPager2
    private lateinit var binding: ActivityHikingWarningsBinding
    private lateinit var repository: Repository
    lateinit var list: List<MatchedRule>

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
        val db = ApplicationDatabase.getDatabase(applicationContext)
        val dao = db.dao()
        repository = Repository(dao)
        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.HOUR_OF_DAY, 0)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        cal1.set(Calendar.MILLISECOND, 0)
        list = repository.getMatchedRulesByDate(cal1.time, true)
        //var list1 = listOf<String>("NAPIHAN SNEG", "STRMO POBOČJE", "TALJENJE SNEGA", "PRISOJNO POBOČJE")
        mViewPager.adapter = WarningsViewPagerAdapter(this, list)

        TabLayoutMediator(binding.tabLayout, mViewPager) {tab, position->
            tab.text = list[position].name
        }.attach()
    }



    fun getRule(position: Int): MatchedRule {
        return list[position]
    }
}