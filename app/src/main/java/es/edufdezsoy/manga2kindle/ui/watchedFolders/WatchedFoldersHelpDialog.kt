package es.edufdezsoy.manga2kindle.ui.watchedFolders

import android.content.Context
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.adapter.ViewPagerAdapter
import es.edufdezsoy.manga2kindle.databinding.ViewWatchedFoldersHelpDialogBinding
import es.edufdezsoy.manga2kindle.ui.watchedFolders.helpDialogScreens.*

class WatchedFoldersHelpDialog(
    context: Context,
    private val owner: LifecycleOwner,
    private val fragmentManager: FragmentManager
) {
    //region vars and vals

    private val dialog = MaterialDialog(context)
    private val binding: ViewWatchedFoldersHelpDialogBinding

    //endregion
    //region constructor

    init {
        dialog
            .lifecycleOwner(owner)
            .title(text = "Folders Help")

        binding = ViewWatchedFoldersHelpDialogBinding.inflate(dialog.layoutInflater)

        dialog.customView(view = binding.root)

        viewHolder(binding)
    }

    //endregion
    //region public functions

    fun show() {
        dialog.show()
    }

    //endregion
    //region private functions

    private fun viewHolder(binding: ViewWatchedFoldersHelpDialogBinding) {
        val fragmentList = arrayListOf<Fragment>(
            WatchedFoldersHelpDialog01Fragment(),
            WatchedFoldersHelpDialog02Fragment(),
            WatchedFoldersHelpDialog03Fragment(),
            WatchedFoldersHelpDialog04Fragment(),
            WatchedFoldersHelpDialog05Fragment()
        )

        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            fragmentManager,
            owner.lifecycle
        )

        binding.helpViewPager.adapter = viewPagerAdapter
        binding.closeButton.setOnClickListener { dialog.dismiss() }
        binding.helpViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setProgressDot(position)
            }
        })
    }

    private fun setProgressDot(activeDot: Int) {
        var counter = 0
        binding.progressDotsLinearLayout.children.forEach {
            it.isEnabled = counter++ == activeDot
        }
    }

    //endregion
}