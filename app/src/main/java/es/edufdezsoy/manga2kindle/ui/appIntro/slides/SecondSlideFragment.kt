package es.edufdezsoy.manga2kindle.ui.appIntro.slides


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R

/**
 * This fragment do... nothing, just a slide
 */
class SecondSlideFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_slide, container, false)
    }


}
