package es.edufdezsoy.manga2kindle.ui.more

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.adapter.ViewPagerAdapter
import es.edufdezsoy.manga2kindle.data.repository.SharedPreferencesHandler
import es.edufdezsoy.manga2kindle.databinding.ViewKindleEmailDialogBinding
import es.edufdezsoy.manga2kindle.ui.more.kindleEmailScreens.*

class KindleEmailDialog(
    private val context: Context,
    private val owner: LifecycleOwner,
    private val fragmentManager: FragmentManager
) {
    //region vars and vals

    private val dialog = MaterialDialog(context)
    private val binding: ViewKindleEmailDialogBinding

    //endregion
    //region constructor

    init {
        dialog
            .lifecycleOwner(owner)
            .title(R.string.kindle_email_dialog_title)

        binding = ViewKindleEmailDialogBinding.inflate(dialog.layoutInflater)

        dialog.customView(view = binding.root)

        viewHolder(binding)
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

    private fun viewHolder(binding: ViewKindleEmailDialogBinding) {
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

        binding.kindleEmailViewPager.adapter = viewPagerAdapter
        binding.kindleEmailTextInputLayout.editText?.setText(SharedPreferencesHandler(context).kindleEmail)

        // set buttons actions
        binding.cancelButton.setOnClickListener { dialog.cancel() }
        binding.saveButton.setOnClickListener {
            val email = binding.kindleEmailTextInputLayout.editText?.text.toString()
            if (isEmailValid(email)) {
                SharedPreferencesHandler(context).kindleEmail = email
                dialog.cancel()
            } else {
                binding.kindleEmailTextInputLayout.error =
                    context.getString(R.string.kindle_email_dialog_error_msg)
            }
        }

    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    //endregion
}