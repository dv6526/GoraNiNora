package si.uni_lj.fri.pbd.GoraNiNora.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import si.uni_lj.fri.pbd.GoraNiNora.R
import si.uni_lj.fri.pbd.GoraNiNora.databinding.FragmentOnboarding1Binding

class OnboardingFragment1 : Fragment() {

    lateinit var binding: FragmentOnboarding1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnboarding1Binding.inflate(layoutInflater)
        arguments?.let {
            val position = it.getInt("position")
            if (position != null) {
                when (position) {
                    0 -> binding.imageExplain.setImageResource(R.drawable.obs1)
                    1 -> binding.imageExplain.setImageResource(R.drawable.obs2)
                    2 -> binding.imageExplain.setImageResource(R.drawable.obs3)
                    3 -> binding.imageExplain.setImageResource(R.drawable.obs4)
                    4 -> binding.imageExplain.setImageResource(R.drawable.obs5)
                    5 -> binding.imageExplain.setImageResource(R.drawable.obs6)
                    6 -> binding.imageExplain.setImageResource(R.drawable.obs7)
                }

            }
        }
        return binding.root
    }

}