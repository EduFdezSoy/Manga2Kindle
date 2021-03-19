package es.edufdezsoy.manga2kindle.ui.watchedFolders.helpDialogScreens

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R
import kotlinx.android.synthetic.main.fragment_watched_folders_help_dialog_02.view.*

class WatchedFoldersHelpDialog02Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_watched_folders_help_dialog_02, container, false)
        // sets the <a> link in that field clickable
        v.tvText2.movementMethod = LinkMovementMethod.getInstance()

        return v
    }
}