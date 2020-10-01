package es.edufdezsoy.manga2kindle.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ViewPagerAdapter
import es.edufdezsoy.manga2kindle.ui.onboarding.screens.Screen01Fragment
import es.edufdezsoy.manga2kindle.ui.onboarding.screens.Screen02Fragment
import es.edufdezsoy.manga2kindle.ui.onboarding.screens.Screen03Fragment
import kotlinx.android.synthetic.main.fragment_on_boarding.view.*

/**
 * TODO: Create onboarding: tutorial, guide and config
 * Need to:
 * - explain the interface (just a little)
 * - explain the process to allow the m2k mail send to the kindle
 * - ask for the mail of the kindle device
 * - ask for permissions to read the filesystem (may check if necessary)
 *
 * Keep it as clean as possible, if its not clear better have another screen.
 * Keep it as short as possible.
 * Maybe add some waifus? Or other manga pj it doesn't matter as long as it entertains a little.
 */
class OnBoardingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_on_boarding, container, false)

        val fragmentList = arrayListOf<Fragment>(
            Screen01Fragment(),
            Screen02Fragment(),
            Screen03Fragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.viewPager.adapter = adapter

        return view
    }
}