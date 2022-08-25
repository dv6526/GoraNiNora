package si.uni_lj.fri.pbd.GoraNiNora.Adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import si.uni_lj.fri.pbd.GoraNiNora.data.rules.MatchedRule
import si.uni_lj.fri.pbd.GoraNiNora.fragments.WarningsDetailsFragment

class WarningsViewPagerAdapter (fragmentActivity: FragmentActivity, var rules: List<MatchedRule>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return rules.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = WarningsDetailsFragment()
        val args = Bundle()
        args.putInt("position", position)
        fragment.arguments = args
        return fragment
    }
}