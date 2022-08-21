package si.uni_lj.fri.pbd.sensecontext.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import si.uni_lj.fri.pbd.sensecontext.Adapters.HistoryRecyclerViewAdapter
import si.uni_lj.fri.pbd.sensecontext.Adapters.RecyclerViewAdapter
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentHistoryBinding
import si.uni_lj.fri.pbd.sensecontext.ui.MainViewModel


class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private var mViewModel: MainViewModel? = null
    private var adapter: HistoryRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        observerSetup()
        recyclerSetup()
    }

    private fun observerSetup() {
        mViewModel?.matchedRulesHistory?.observe(viewLifecycleOwner) { matchedRulesHistory ->
            adapter?.setItemList(matchedRulesHistory)
        }
    }

    private fun recyclerSetup() {
        adapter = mViewModel?.let {HistoryRecyclerViewAdapter(it, requireContext())}
        val recyclerView: RecyclerView = binding.historyRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

}