package es.edufdezsoy.manga2kindle.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*

class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        setButtonsClickActions(view)
        setSettings(view)

        return view
    }

    //region private functions

    private fun setButtonsClickActions(view: View) {
        view.layout_email_constraintLayout.setOnClickListener {
            // TODO: open email box (with the explanation where to find)
        }

        view.layout_wifi_constraintLayout.setOnClickListener {
            wifi_switch.toggle()
            SharedPreferencesHandler(requireContext()).uploadOnlyOnWifi = wifi_switch.isChecked
        }

        view.layout_autoDelete_constraintLayout.setOnClickListener {
            delete_switch.toggle()
            SharedPreferencesHandler(requireContext()).deleteAfterUpload = delete_switch.isChecked
        }

        view.layout_hidden_linearLayout.setOnClickListener {
            // TODO: open the hidden chapters view
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(view.layout_hidden_linearLayout, false)

        view.layout_settings_constraintLayout.setOnClickListener {
            // TODO: open the settings
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(view.layout_settings_constraintLayout, false)

        view.layout_about_constraintLayout.setOnClickListener {
            // TODO: open the about
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(view.layout_about_constraintLayout, false)

        view.layout_help_constraintLayout.setOnClickListener {
            // TODO: open the help (link to the web?)
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(view.layout_help_constraintLayout, false)

        // Switch buttons
        view.wifi_switch.setOnClickListener {
            SharedPreferencesHandler(requireContext()).uploadOnlyOnWifi = wifi_switch.isChecked
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(view.layout_wifi_constraintLayout, false)

        view.delete_switch.setOnClickListener {
            SharedPreferencesHandler(requireContext()).deleteAfterUpload = delete_switch.isChecked
        }
        // since this is on TO DO, lets disable this
        setViewAndChildrenEnabled(view.layout_autoDelete_constraintLayout, false)
    }

    private fun setSettings(view: View) {
        val pref = SharedPreferencesHandler(requireContext())

        view.description_email_textView.text = pref.kindleEmail

        view.wifi_switch.isChecked = pref.uploadOnlyOnWifi
        view.delete_switch.isChecked = pref.deleteAfterUpload

        if (pref.hideHiddenList) {
            view.layout_hidden_linearLayout.visibility = View.GONE
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