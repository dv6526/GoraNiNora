package si.uni_lj.fri.pbd.GoraNiNora.Adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import si.uni_lj.fri.pbd.GoraNiNora.fragments.OnboardingFragment1

class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment1()
            else -> OnboardingFragment1()
        }
    }

    override fun getItemCount(): Int {
        return 7
    }
}