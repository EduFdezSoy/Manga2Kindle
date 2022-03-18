package es.edufdezsoy.manga2kindle.ui.more.kindleEmailScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.databinding.FragmentKindleEmailScreen02Binding

class KindleEmailScreen02Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentKindleEmailScreen02Binding.inflate(inflater, container, false)

        binding.dot1ImageView.isEnabled = false
        binding.dot2ImageView.isEnabled = true
        binding.dot3ImageView.isEnabled = false
        binding.dot4ImageView.isEnabled = false
        binding.dot5ImageView.isEnabled = false

        return binding.root
    }
}