package si.uni_lj.fri.pbd.sensecontext.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentWarningsDetailsBinding


class WarningsDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var rule_id: Long = 0
    private lateinit var binding: FragmentWarningsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            rule_id = it.getLong("rule_id")
        }
        // pridobi rule in ga prikazi
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWarningsDetailsBinding.inflate(layoutInflater)
        // pridobi rule in ga prikazi

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun setupData() {
        //nastavi naslov in opis nevarnosti
    }
}