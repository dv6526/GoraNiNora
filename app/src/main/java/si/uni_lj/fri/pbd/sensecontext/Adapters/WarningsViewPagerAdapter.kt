package si.uni_lj.fri.pbd.sensecontext.Adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.fragments.WarningsDetailsFragment

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