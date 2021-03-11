package es.edufdezsoy.manga2kindle.ui.more.kindleEmailScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R
import kotlinx.android.synthetic.main.fragment_kindle_email_screen05.view.*

class KindleEmailScreen05Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_kindle_email_screen05, container, false)

        view.dot1_imageView.isEnabled = false
        view.dot2_imageView.isEnabled = false
        view.dot3_imageView.isEnabled = false
        view.dot4_imageView.isEnabled = false
        view.dot5_imageView.isEnabled = true

        return view
    }
}