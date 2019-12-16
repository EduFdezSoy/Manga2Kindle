package es.edufdezsoy.manga2kindle.ui.appIntro

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import es.edufdezsoy.manga2kindle.ui.appIntro.slides.FirstSlideFragment
import es.edufdezsoy.manga2kindle.ui.appIntro.slides.ForthSlideFragment
import es.edufdezsoy.manga2kindle.ui.appIntro.slides.SecondSlideFragment
import es.edufdezsoy.manga2kindle.ui.appIntro.slides.ThirdSlideFragment


class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showSkipButton(false)
        showStatusBar(false)

        addSlide(FirstSlideFragment())
        addSlide(SecondSlideFragment())
        addSlide(ThirdSlideFragment())
        addSlide(ForthSlideFragment())
    }

    // Skip button is disable up there, if you complete this method remember to enable it
    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // TODO: if skip is pressed show a dialog to confirm the action
        //  a misconfiguration will cause mangas not being delivered

        // finish activity
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        // finish activity
        finish()
    }
}