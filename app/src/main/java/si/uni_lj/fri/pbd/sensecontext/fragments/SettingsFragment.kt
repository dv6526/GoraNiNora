package si.uni_lj.fri.pbd.sensecontext.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import si.uni_lj.fri.pbd.sensecontext.*
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentSettingsBinding
import si.uni_lj.fri.pbd.sensecontext.databinding.FragmentSettingsBinding.inflate


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding


    companion object {
        lateinit var switch0: SwitchMaterial
        lateinit var switch1: SwitchMaterial
        lateinit var switch2: SwitchMaterial
        lateinit var switch3: SwitchMaterial
        var neverAskAgainClicked1: Boolean = false
        var neverAskAgainClicked2: Boolean = false
        var neverAskAgainClicked3: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //preveri, če je dovoljenje že grantano
        if (HandlePermissions.isPermissionTransitionRecognitionGranted()) {
            switch1.setChecked(true)
            switch1.setClickable(false)
        }
        if (HandlePermissions.isPermissionForFineLocationGranted()) {
            switch2.setChecked(true)
            switch2.setClickable(false)
        }
        if (HandlePermissions.isPermissionForBackgroundLocationGranted()) {
            switch3.setChecked(true)
            switch3.setClickable(false)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        HandlePermissions.setActivityAndContext(requireContext(), requireActivity())

        switch0 = binding.switch0
        switch1 = binding.switch1
        switch2 = binding.switch2
        switch3 = binding.switch3

        setup_switch_permissions()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setup_switch_permissions() {
        //preveri, če je dovoljenje že grantano
        if (HandlePermissions.isPermissionTransitionRecognitionGranted()) {
            switch1.setChecked(true)
            switch1.setClickable(false)
        } else {
            neverAskAgainClicked1 = !shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                HandlePermissions.requestPermissionForTransitionRecognition()
            }
        }


        //preveri, če je dovoljenje že grantano
        if (HandlePermissions.isPermissionForFineLocationGranted()) {
            switch2.setChecked(true)
            switch2.setClickable(false)
        } else {
            neverAskAgainClicked2 = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                HandlePermissions.requestPermissionForFineLocation()
            }
        }


        //preveri, če je dovoljenje že grantano
        if (HandlePermissions.isPermissionForBackgroundLocationGranted()) {
            switch3.setChecked(true)
            switch3.setClickable(false)
        } else {
            neverAskAgainClicked3 = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        switch3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                HandlePermissions.requestPermissionForBackgroundLocation()
            }
        }
    }



}