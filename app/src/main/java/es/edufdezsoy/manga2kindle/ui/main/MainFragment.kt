package es.edufdezsoy.manga2kindle.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import es.edufdezsoy.manga2kindle.MainActivity
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ViewPagerAdapter
import es.edufdezsoy.manga2kindle.ui.more.MoreFragment
import es.edufdezsoy.manga2kindle.ui.newChapters.NewChaptersFragment
import es.edufdezsoy.manga2kindle.ui.uploaded.DashboardFragment
import es.edufdezsoy.manga2kindle.ui.watchedFolders.NotificationFragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val fragmentList = arrayListOf<Fragment>(
            NewChaptersFragment(),
            DashboardFragment(),
            NotificationFragment(),
            MoreFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            childFragmentManager,
            lifecycle
        )

        // Swipe events
        view.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    // check the button in the BottomNavigationView on page changed
                    view.bottomNavigationView.menu[position].isChecked = true
                }
            }
        })

        // Bottom Navigation View Buttons
        view.bottomNavigationView.setOnNavigationItemSelectedListener {
            for (i in 0 until view.bottomNavigationView.menu.size()) {
                if (view.bottomNavigationView.menu[i].itemId == it.itemId) {
                    viewPager?.currentItem = i
                    break
                }
            }
            true
        }

        view.viewPager.adapter = adapter


        // Go To Fragment if needed (to navigate from notifications)
        if ((activity as MainActivity).intentGoToFragment != null) {
            view.viewPager.post {
                viewPager?.currentItem = (activity as MainActivity).intentGoToFragment!!
            }
        }

        return view
    }
}