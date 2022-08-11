package si.uni_lj.fri.pbd.sensecontext.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import si.uni_lj.fri.pbd.sensecontext.data.rules.MatchedRule
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentWarningsDetailsBinding
import si.uni_lj.fri.pbd.sensecontext.ui.HikingWarningsActivity


class WarningsDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var position: Int = 0
    private lateinit var binding: FragmentWarningsDetailsBinding
    private lateinit var rule: MatchedRule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }

        setupData(position)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWarningsDetailsBinding.inflate(layoutInflater)
        binding.textDesc.text = rule.text
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    fun setupData(position: Int) {

        val activity: HikingWarningsActivity? = activity as HikingWarningsActivity?
        rule = activity!!.getRule(position)

    }
}