package es.edufdezsoy.manga2kindle.ui.more.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import es.edufdezsoy.manga2kindle.BuildConfig
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setButtonsClickActions(binding)
        setSettings(binding)

        return binding.root
    }

    private fun setButtonsClickActions(binding: FragmentSettingsBinding) {
        val debugOptionsListener = View.OnClickListener {
            if (it !is SwitchCompat) {
                binding.enableDebugOptionsSwitch.toggle()
            }

            if (binding.enableDebugOptionsSwitch.isChecked) {
                enableDebug(binding)
            } else {
                disableDebug(binding)
            }
        }

        binding.layoutEnableDebugConstraintLayout.setOnClickListener(debugOptionsListener)
        binding.enableDebugOptionsSwitch.setOnClickListener(debugOptionsListener)

        binding.layoutDebugLogConstraintLayout.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_logCatFragment)

        }
    }

    private fun setSettings(binding: FragmentSettingsBinding) {
//        val pref = SharedPreferencesHandler(requireContext())

        if (BuildConfig.DEBUG) {
            enableDebug(binding)
        } else {
            binding.layoutEnableDebugConstraintLayout.isEnabled = false
            disableDebug(binding)
        }
    }

    private fun enableDebug(binding: FragmentSettingsBinding) {
        binding.enableDebugOptionsSwitch.isChecked = true
        binding.layoutDebugLogConstraintLayout.visibility = View.VISIBLE
    }

    private fun disableDebug(binding: FragmentSettingsBinding) {
        binding.layoutDebugLogConstraintLayout.visibility = View.GONE
    }
}