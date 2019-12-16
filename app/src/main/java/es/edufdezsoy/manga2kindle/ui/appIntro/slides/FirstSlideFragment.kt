package es.edufdezsoy.manga2kindle.ui.appIntro.slides


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R
import kotlinx.android.synthetic.main.fragment_first_slide.view.*

/**
 * This fragment do... nothing, just a slide
 */
class FirstSlideFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_first_slide, container, false)

        v.tvHello.text = resources.getStringArray(R.array.slide_greetings).random()

        return v
    }


}
