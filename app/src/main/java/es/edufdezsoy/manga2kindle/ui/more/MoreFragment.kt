package es.edufdezsoy.manga2kindle.ui.more

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R

class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        setSettings()

        return view
    }

    //region private functions

    private fun setSettings() {
        val pref = context?.getSharedPreferences("more_preferences", MODE_PRIVATE)

    }

    //endregion
}