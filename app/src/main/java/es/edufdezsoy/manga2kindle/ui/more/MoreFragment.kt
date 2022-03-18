package es.edufdezsoy.manga2kindle.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMoreBinding.inflate(inflater, container, false)

        setButtonsClickActions(binding)
        setSettings(binding)

        return binding.root
    }

    //region private functions

    private fun setButtonsClickActions(binding: FragmentMoreBinding) {
        binding.layoutEmailConstraintLayout.setOnClickListener {
            val dialog = KindleEmailDialog(
                requireContext(),
                viewLifecycleOwner,
                requireActivity().supportFragmentManager
            )
            dialog.onDismiss {
                setSettings(binding)
            }
            dialog.show()
        }

        binding.layoutWifiConstraintLayout.setOnClickListener {
            binding.wifiSwitch.toggle()
            SharedPreferencesHandler(requireContext()).uploadOnlyOnWifi = binding.wifiSwitch.isChecked
        }

        binding.layoutAutoDeleteConstraintLayout.setOnClickListener {
            binding.deleteSwitch.toggle()
            SharedPreferencesHandler(requireContext()).deleteAfterUpload = binding.deleteSwitch.isChecked
        }

        binding.layoutHiddenLinearLayout.setOnClickListener {
            // TODO: open the hidden chapters view
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(binding.layoutHiddenLinearLayout, false)

        binding.layoutSettingsConstraintLayout.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
        }

        binding.layoutAboutConstraintLayout.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_aboutFragment)
        }

        binding.layoutHelpConstraintLayout.setOnClickListener {
            // TODO: open the help (link to the web?)
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(binding.layoutHelpConstraintLayout, false)

        // Switch buttons
        binding.wifiSwitch.setOnClickListener {
            SharedPreferencesHandler(requireContext()).uploadOnlyOnWifi = binding.wifiSwitch.isChecked
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(binding.layoutWifiConstraintLayout, false)

        binding.deleteSwitch.setOnClickListener {
            SharedPreferencesHandler(requireContext()).deleteAfterUpload = binding.deleteSwitch.isChecked
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(binding.layoutAutoDeleteConstraintLayout, false)
    }

    private fun setSettings(binding: FragmentMoreBinding) {
        val pref = SharedPreferencesHandler(requireContext())

        binding.descriptionEmailTextView.text = pref.kindleEmail

        binding.wifiSwitch.isChecked = pref.uploadOnlyOnWifi
        binding.deleteSwitch.isChecked = pref.deleteAfterUpload

        if (pref.hideHiddenList) {
            binding.layoutHiddenLinearLayout.visibility = View.GONE
        }
    }

    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }

    //endregion
}