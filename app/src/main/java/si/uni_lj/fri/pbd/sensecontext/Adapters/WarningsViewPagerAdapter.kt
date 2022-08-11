package si.uni_lj.fri.pbd.sensecontext.Adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import si.uni_lj.fri.pbd.sensecontext.fragments.WarningsDetailsFragment

class WarningsViewPagerAdapter (fragmentActivity: FragmentActivity, var rule_ids: List<Long>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return rule_ids.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = WarningsDetailsFragment()
        val args = Bundle()
        args.putLong("rule_id", rule_ids[position])
        fragment.arguments = args
        return fragment
    }
}