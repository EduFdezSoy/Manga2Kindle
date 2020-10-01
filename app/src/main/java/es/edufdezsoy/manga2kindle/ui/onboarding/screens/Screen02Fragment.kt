package es.edufdezsoy.manga2kindle.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import es.edufdezsoy.manga2kindle.R
import kotlinx.android.synthetic.main.fragment_screen01.view.*

class Screen02Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_screen02, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.nextBtn.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return view
    }
}