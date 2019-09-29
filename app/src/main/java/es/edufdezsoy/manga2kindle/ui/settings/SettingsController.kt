package es.edufdezsoy.manga2kindle.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceController
import androidx.preference.PreferenceManager
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingsController : PreferenceController(), CoroutineScope {
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    lateinit var prefs: SharedPreferences


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        job = Job()

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, s ->
            checkDebug(sharedPreferences, s)
            checkResetChapters(sharedPreferences, s)
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun checkDebug(sharedPreferences: SharedPreferences, key: String) {
        if (key == "switchDebug") {
            launch { M2kApplication.debug = sharedPreferences.getBoolean(key, false) }
        }
    }

    private fun checkResetChapters(sharedPreferences: SharedPreferences, key: String) {
        if (key == "etPrefResetChapters") {
            launch {
                if (sharedPreferences.getString(key, "")!!.toLowerCase() == "yes, please") {
                    M2kDatabase.invoke(activity!!).ChapterDao().clearNotSended()
                    if (M2kApplication.debug)
                        Log.d(
                            M2kApplication.TAG,
                            "Non uploaded chapters removed from the database."
                        )
                }
                sharedPreferences.edit().putString(key, "dont").apply()
            }
        }
    }
}