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
import android.widget.Toast
import androidx.fragment.app.Fragment
import si.uni_lj.fri.pbd.sensecontext.MainActivity
import si.uni_lj.fri.pbd.sensecontext.Receivers.DetectedTransitionReceiver
import si.uni_lj.fri.pbd.sensecontext.Services.LocationUpdatesService
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentSensorsBinding
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentSensorsBinding.inflate


class SensorsFragment : Fragment() {

    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!

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
        val switch = binding.switch1
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        switch.setChecked(sharedPreferences.getBoolean("override", false))

        switch.setOnCheckedChangeListener { _, isChecked ->
            val editor: SharedPreferences.Editor = requireActivity().getSharedPreferences("pref", MODE_PRIVATE).edit()
            if (isChecked) {
                editor.putBoolean("override", true)
                editor.apply()
                startLocationUpdates()
            } else {
                editor.putBoolean("override", false)
                editor.apply()
                stopLocationUpdates()
            }
        }
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
    }



    fun startLocationUpdates() {
        activityCallback?.stopActivityTransitionUpdates()
        LocationUpdatesService.user_is_hiking = true
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
        if (LocationUpdatesService.IS_RUNNING) {
            val i = Intent(context, LocationUpdatesService::class.java)
            i.action = LocationUpdatesService.ACTION_STOP
            requireActivity().applicationContext.startService(i)
        }
    }

}