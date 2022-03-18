package es.edufdezsoy.manga2kindle.ui.main

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBindings
import androidx.viewpager2.widget.ViewPager2
import es.edufdezsoy.manga2kindle.MainActivity
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ViewPagerAdapter
import es.edufdezsoy.manga2kindle.databinding.FragmentMainBinding
import es.edufdezsoy.manga2kindle.ui.more.MoreFragment
import es.edufdezsoy.manga2kindle.ui.newChapters.NewChaptersFragment
import es.edufdezsoy.manga2kindle.ui.uploaded.UploadedChaptersFragment
import es.edufdezsoy.manga2kindle.ui.watchedFolders.WatchedFoldersFragment
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        (activity as AppCompatActivity).setSupportActionBar(ViewBindings.findChildViewById(R.id.new_chapters))
        (activity as AppCompatActivity).supportActionBar?.show()


        val binding = FragmentMainBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf<Fragment>(
            NewChaptersFragment(),
            UploadedChaptersFragment(),
            WatchedFoldersFragment(),
            MoreFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            childFragmentManager,
            lifecycle
        )

        // Swipe events
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    // check the button in the BottomNavigationView on page changed
                    binding.bottomNavigationView.menu[position].isChecked = true
                }
            }
        })

        // Bottom Navigation View Buttons
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            for (i in 0 until binding.bottomNavigationView.menu.size()) {
                if (binding.bottomNavigationView.menu[i].itemId == it.itemId) {
                    binding.viewPager.currentItem = i
                    break
                }
            }
            true
        }

        binding.viewPager.adapter = adapter


        // Go To Fragment if needed (to navigate from notifications)
        if ((activity as MainActivity).intentGoToFragment != null) {
            binding.viewPager.post {
                binding.viewPager.currentItem = (activity as MainActivity).intentGoToFragment!!
            }
        }

        askForPermissions()

        return binding.root
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun askForPermissions() {
        Toast.makeText(context, "Dickbutt", Toast.LENGTH_SHORT).show()
    }
}