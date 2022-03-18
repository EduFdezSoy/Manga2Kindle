package es.edufdezsoy.manga2kindle.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.databinding.FragmentScreen02Binding

class Screen02Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentScreen02Binding.inflate(inflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        binding.nextBtn.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return binding.root
    }
}