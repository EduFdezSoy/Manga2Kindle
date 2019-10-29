package es.edufdezsoy.manga2kindle.ui.settings

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceController
import androidx.preference.PreferenceManager
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SettingsController : PreferenceController() {
    lateinit var prefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, s ->
            checkDebug(sharedPreferences, s)
            checkResetChapters(sharedPreferences, s)
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        addPreferencesFromResource(R.xml.settings)
    }

    private fun checkDebug(sharedPreferences: SharedPreferences, key: String) {
        if (key == "switchDebug") {
            GlobalScope.launch(Dispatchers.IO) {
                M2kApplication.debug = sharedPreferences.getBoolean(key, false)
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun checkResetChapters(sharedPreferences: SharedPreferences, key: String) {
        if (key == "etPrefResetChapters") {
            if (sharedPreferences.getString(key, "")!!.toLowerCase(Locale.ENGLISH) == "yes, please"
            ) {
                GlobalScope.launch(Dispatchers.IO) {
                    ChapterRepository.invoke(activity!!).clearNotSended().also {
                        if (M2kApplication.debug)
                            Log.d(
                                M2kApplication.TAG,
                                "Non uploaded chapters removed from the database."
                            )
                    }
                }
            }
            // yes, we want it to be immediately write to disk
            sharedPreferences.edit().putString(key, "dont").commit()
        }
    }
}