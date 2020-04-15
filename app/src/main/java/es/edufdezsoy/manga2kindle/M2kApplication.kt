package es.edufdezsoy.manga2kindle

import android.app.Application
import android.util.Log
import es.edufdezsoy.manga2kindle.data.M2kSharedPref
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class M2kApplication : Application() {
    companion object {
        const val TAG = "MANGA2KINDLE"
        const val BASE_URL = "https://manga2kindle.com/api/"
//        const val BASE_URL = "https://test.manga2kindle.com/"
        var debug = false
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(M2kApplication.TAG, "All systems online.")

        // This async work checks if the debug mode is activated
        GlobalScope.launch {
            M2kApplication.debug =
                M2kSharedPref.invoke(this@M2kApplication).getBoolean("switchDebug", false)

            if (M2kApplication.debug)
                Log.i(M2kApplication.TAG, "Debug activated.")
        }
    }
}