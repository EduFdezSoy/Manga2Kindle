package es.edufdezsoy.manga2kindle.ui.more.kindleEmailScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R
import kotlinx.android.synthetic.main.fragment_kindle_email_screen02.view.*

class KindleEmailScreen02Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_kindle_email_screen02, container, false)

        view.dot1_imageView.isEnabled = false
        view.dot2_imageView.isEnabled = true
        view.dot3_imageView.isEnabled = false
        view.dot4_imageView.isEnabled = false
        view.dot5_imageView.isEnabled = false

        return view
    }
}