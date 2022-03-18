package es.edufdezsoy.manga2kindle.ui.watchedFolders.helpDialogScreens

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.databinding.FragmentWatchedFoldersHelpDialog02Binding

class WatchedFoldersHelpDialog02Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentWatchedFoldersHelpDialog02Binding.inflate(inflater, container, false)
        // sets the <a> link in that field clickable
        binding.tvText2.movementMethod = LinkMovementMethod.getInstance()

        return binding.root
    }
}