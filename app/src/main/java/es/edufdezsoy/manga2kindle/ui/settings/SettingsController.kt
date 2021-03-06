package es.edufdezsoy.manga2kindle.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceController
import androidx.preference.PreferenceManager
import cn.pedant.SweetAlert.SweetAlertDialog
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.ui.about.AboutActivity
import es.edufdezsoy.manga2kindle.ui.appIntro.IntroActivity
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity
import es.edufdezsoy.manga2kindle.ui.hiddenChapters.HiddenChaptersActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsController : PreferenceController() {
    lateinit var prefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, s ->
            checkDebug(sharedPreferences, s)
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        addPreferencesFromResource(R.xml.settings)

        setClickListeners()
    }

    override fun handleBack(): Boolean {
        (activity as BaseActivity).getMenu()!!.setSelection(1)
        return true
    }

    private fun setClickListeners() {
        val prefHiddenList = findPreference("hiddenChapters")
        prefHiddenList?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            showHiddenList()
            return@OnPreferenceClickListener true
        }

        val prefResetChapter = findPreference("resetChapters")
        prefResetChapter?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            resetChapters()
            return@OnPreferenceClickListener true
        }

        val prefAbout = findPreference("about")
        prefAbout?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startAbout()
            return@OnPreferenceClickListener true
        }

        val prefTuto = findPreference("showTutorial")
        prefTuto?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startAppIntro()
            return@OnPreferenceClickListener true
        }
    }

    private fun checkDebug(sharedPreferences: SharedPreferences, key: String) {
        if (key == "switchDebug") {
            GlobalScope.launch(Dispatchers.IO) {
                M2kApplication.debug = sharedPreferences.getBoolean(key, false)
            }
        }
    }

    private fun resetChapters() {
        val dialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
        dialog
            .setTitleText(activity!!.getString(R.string.reset_chapters_confirm_title))
            .setContentText(activity!!.getString(R.string.reset_chapters_confirm_msg))
            .setCancelButton(activity!!.getString(R.string.reset_chapters_negative_btn), null)
            .setConfirmButton(activity!!.getString(R.string.reset_chapters_positive_btn)) {
                GlobalScope.launch(Dispatchers.IO) {
                    ChapterRepository.invoke(activity!!).clearNotSended().also {
                        if (M2kApplication.debug)
                            Log.d(
                                M2kApplication.TAG,
                                "Non uploaded chapters removed from the database."
                            )
                    }
                }.also {
                    dialog
                        .setTitleText(activity!!.getString(R.string.reset_chapters_done_title))
                        .setContentText(activity!!.getString(R.string.reset_chapters_done_msg))
                        .setConfirmText(activity!!.getString(R.string.reset_chapters_done_btn))
                        .setConfirmClickListener(null)
                        .showCancelButton(false)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                }
            }
            .show()
    }

    private fun showHiddenList() {
        val intent = Intent(activity, HiddenChaptersActivity::class.java)
        startActivity(intent)
    }

    private fun startAbout() {
        val intent = Intent(activity, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun startAppIntro() {
        val intent = Intent(activity, IntroActivity::class.java)
        startActivity(intent)
    }
}