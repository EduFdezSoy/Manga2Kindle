package es.edufdezsoy.manga2kindle.ui.more

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ViewPagerAdapter
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.ui.more.kindleEmailScreens.*
import kotlinx.android.synthetic.main.view_kindle_email_dialog.view.*

class KindleEmailDialog(
    private val context: Context,
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
            .customView(R.layout.view_kindle_email_dialog)
            .title(R.string.kindle_email_dialog_title)

        view = dialog.getCustomView()
        viewHolder(view)
    }

    //endregion
    //region public functions

    fun show() {
        dialog.show()
    }

    fun onDismiss(callback: DialogCallback) {
        dialog.onDismiss(callback)
    }

    //endregion
    //region private functions

    private fun viewHolder(view: View) {
        val fragmentList = arrayListOf<Fragment>(
            KindleEmailScreen01Fragment(),
            KindleEmailScreen02Fragment(),
            KindleEmailScreen03Fragment(),
            KindleEmailScreen04Fragment(),
            KindleEmailScreen05Fragment()
        )

        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            fragmentManager,
            owner.lifecycle
        )

        view.kindle_email_viewPager.adapter = viewPagerAdapter
        view.kindle_email_textInputLayout.editText?.setText(SharedPreferencesHandler(context).kindleEmail)

        // set buttons actions
        view.cancel_button.setOnClickListener { dialog.cancel() }
        view.save_button.setOnClickListener {
            val email = view.kindle_email_textInputLayout.editText?.text.toString()
            if (isEmailValid(email)) {
                SharedPreferencesHandler(context).kindleEmail = email
                dialog.cancel()
            } else {
                view.kindle_email_textInputLayout.error =
                    context.getString(R.string.kindle_email_dialog_error_msg)
            }
        }

    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    //endregion
}