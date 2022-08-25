package si.uni_lj.fri.pbd.GoraNiNora.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import si.uni_lj.fri.pbd.GoraNiNora.*
import si.uni_lj.fri.pbd.GoraNiNora.databinding.FragmentSettingsBinding
import si.uni_lj.fri.pbd.GoraNiNora.databinding.FragmentSettingsBinding.inflate
import si.uni_lj.fri.pbd.GoraNiNora.ui.OnboardingActivity


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navodila: CardView


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

        setupSwitchState()
        //preveri, če je dovoljenje že grantano

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

        setupSwitchState()
        setupSwitchOnClick()

        navodila = binding.navodila
        navodila.setOnClickListener { _ ->
            val intent =
                Intent(context, OnboardingActivity::class.java)
            startActivity(intent)
        }


    }

    fun setupSwitchState() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (sp.getBoolean("power_saving", false)) {
            switch0.setChecked(true)
        }

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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setupSwitchOnClick() {
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        switch0.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with (sp.edit()) {
                    putBoolean("power_saving", true)
                    apply()
                }
            } else {
                with (sp.edit()) {
                    putBoolean("power_saving", false)
                    apply()
                }
            }
        }

        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                HandlePermissions.requestPermissionForTransitionRecognition()
            }
        }

        switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                HandlePermissions.requestPermissionForFineLocation()
            }
        }

        switch3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                HandlePermissions.requestPermissionForBackgroundLocation()
            }
        }
    }



}