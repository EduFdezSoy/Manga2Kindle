package es.edufdezsoy.manga2kindle.ui.watchedFolders

import android.content.Context
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ViewPagerAdapter
import es.edufdezsoy.manga2kindle.ui.watchedFolders.helpDialogScreens.*
import kotlinx.android.synthetic.main.view_watched_folders_help_dialog.view.*

class WatchedFoldersHelpDialog(
    context: Context,
    private val owner: LifecycleOwner,
    private val fragmentManager: FragmentManager
) {
    //region vars and vals

    private val dialog = MaterialDialog(context)
    private val view: View

    //endregion
    //region constructor

    init {
        dialog
            .lifecycleOwner(owner)
            .customView(R.layout.view_watched_folders_help_dialog)
            .title(text = "Folders Help")

        view = dialog.getCustomView()
        viewHolder(view)
    }

    //endregion
    //region public functions

    fun show() {
        dialog.show()
    }

    //endregion
    //region private functions

    private fun viewHolder(view: View) {
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

        view.help_viewPager.adapter = viewPagerAdapter
        view.close_button.setOnClickListener { dialog.dismiss() }
        view.help_viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setProgressDot(position)
            }
        })
    }

    private fun setProgressDot(activeDot: Int) {
        var counter = 0
        view.progress_dots_linearLayout.children.forEach {
            it.isEnabled = counter++ == activeDot
        }
    }

    //endregion
}