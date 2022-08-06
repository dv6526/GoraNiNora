package si.uni_lj.fri.pbd.sensecontext.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import si.uni_lj.fri.pbd.sensecontext.R
import si.uni_lj.fri.pbd.sensecontext.Receivers.DetectedTransitionReceiver
import si.uni_lj.fri.pbd.sensecontext.RecyclerViewAdapter
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.sensecontext.data.ApplicationDatabase
import si.uni_lj.fri.pbd.sensecontext.data.Repository
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentWarningsBinding.inflate
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentWarningsBinding
import si.uni_lj.fri.pbd.sensecontext.ui.MainViewModel


class WarningsFragment : Fragment() {

    private var _binding: FragmentWarningsBinding? = null
    private val binding get() = _binding!!
    private var mViewModel: MainViewModel? = null
    private var adapter: RecyclerViewAdapter? = null
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        observerSetup()
        recyclerSetup()
    }

    private fun observerSetup() {
        mViewModel?.matchedRules?.observe(viewLifecycleOwner) { matchedRules ->
            print(matchedRules)
            adapter?.setItemList(matchedRules)
        }
    }

    private fun recyclerSetup() {
        adapter = mViewModel?.let { RecyclerViewAdapter(it, this) }
        val recyclerView: RecyclerView = binding.warningsRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }


    var activityCallback: FragmentCallback? = null

    interface FragmentCallback {
        fun stopActivityTransitionUpdates()
        fun startActivityTransitionUpdates()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            activityCallback = context as FragmentCallback
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement ToolbarListener")
        }
        val db = ApplicationDatabase.getDatabase(context)
        val dao = db.dao()
        repository = Repository(dao)
    }



    fun startLocationUpdates() {
        activityCallback?.stopActivityTransitionUpdates()
        LocationUpdatesService.user_is_hiking = true
        repository.user_hiking.postValue(true)
        if (!LocationUpdatesService.IS_RUNNING) {
            val i = Intent(context, LocationUpdatesService::class.java)
            i.putExtra("locationUpdatesInterval",
                DetectedTransitionReceiver.locationUpdatesInterval
            )
            i.action = LocationUpdatesService.ACTION_START

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireActivity().applicationContext.startForegroundService(i)
            } else {
                requireActivity().applicationContext.startService(i)
            }
        }

    }

    fun stopLocationUpdates() {
        activityCallback?.startActivityTransitionUpdates()
        LocationUpdatesService.user_is_hiking = false
        repository.user_hiking.postValue(false)
        if (LocationUpdatesService.IS_RUNNING) {
            val i = Intent(context, LocationUpdatesService::class.java)
            i.action = LocationUpdatesService.ACTION_STOP
            requireActivity().applicationContext.startService(i)
        }
    }

}