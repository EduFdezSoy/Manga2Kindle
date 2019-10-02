package es.edufdezsoy.manga2kindle

import android.app.Application
import android.content.Context
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class M2kApplication : Application() {
    companion object {
        const val TAG = "MANGA2KINDLE"
        const val BASE_URL = "https://manga2kindle.edufdezsoy.es"
        var debug = false
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(M2kApplication.TAG, "All systems online.")

        // This async work checks if the debug mode is activated
        GlobalScope.launch {
            M2kApplication.debug =
                getSharedPreferences(
                    "es.edufdezsoy.manga2kindle_preferences",
                    Context.MODE_PRIVATE
                ).getBoolean("switchDebug", false)

            if (M2kApplication.debug)
                Log.i(M2kApplication.TAG, "Debug activated.")
        }
    }
}