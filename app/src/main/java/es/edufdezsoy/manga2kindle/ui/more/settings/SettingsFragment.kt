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
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        setButtonsClickActions(view)
        setSettings(view)

        return view
    }

    private fun setButtonsClickActions(view: View) {
        val debugOptionsListener = View.OnClickListener {
            if (it !is SwitchCompat) {
                enable_debug_options_switch.toggle()
            }

            if (enable_debug_options_switch.isChecked) {
                enableDebug(view)
            } else {
                disableDebug(view)
            }
        }

        view.layout_enable_debug_constraintLayout.setOnClickListener(debugOptionsListener)
        view.enable_debug_options_switch.setOnClickListener(debugOptionsListener)

        view.layout_debug_log_constraintLayout.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_logCatFragment)

        }
    }

    private fun setSettings(view: View) {
//        val pref = SharedPreferencesHandler(requireContext())

        if (BuildConfig.DEBUG) {
            enableDebug(view)
        } else {
            view.layout_enable_debug_constraintLayout.isEnabled = false
            disableDebug(view)
        }
    }

    private fun enableDebug(view: View) {
        view.enable_debug_options_switch.isChecked = true
        view.layout_debug_log_constraintLayout.visibility = View.VISIBLE
    }

    private fun disableDebug(view: View) {
        view.layout_debug_log_constraintLayout.visibility = View.GONE
    }
}