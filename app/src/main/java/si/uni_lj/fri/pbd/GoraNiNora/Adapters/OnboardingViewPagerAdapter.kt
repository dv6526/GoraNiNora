package si.uni_lj.fri.pbd.GoraNiNora.Adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import si.uni_lj.fri.pbd.GoraNiNora.fragments.OnboardingFragment1

class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    var fragmentActivity = fragmentActivity

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("position", position)
        val fragment = OnboardingFragment1()
        fragment.arguments = bundle

        return fragment
    }

    override fun getItemCount(): Int {
        return 7
    }
}