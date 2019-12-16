package es.edufdezsoy.manga2kindle.ui.appIntro.slides


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.ISlidePolicy
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kSharedPref
import kotlinx.android.synthetic.main.fragment_third_slide.view.*


/**
 * This fragment do... nothing, just a slide
 */
class ThirdSlideFragment : ISlidePolicy, Fragment() {
    private lateinit var etKindleEmail: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_third_slide, container, false)

        etKindleEmail = v.etKindleEmail

        return v
    }

    fun saveEmail(): Boolean {
        val email = etKindleEmail.text.toString()

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // save to sharedPref
            M2kSharedPref.invoke(requireContext()).edit().putString("kindle_mail", email).apply()

            return true
        } else {
            // put an error
            etKindleEmail.error = getString(R.string.slide_3_email_error)
            etKindleEmail.requestFocus()

            return false
        }
    }

    override fun isPolicyRespected(): Boolean {
        return saveEmail()
    }

    override fun onUserIllegallyRequestedNextPage() {
        // we dont want to do anything in here i think
    }
}

