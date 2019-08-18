package es.edufdezsoy.manga2kindle

import android.app.Application
import es.edufdezsoy.manga2kindle.data.M2kDatabase


class M2kApplication : Application() {
    companion object {
        const val TAG = "MANGA2KINDLE"
        const val BASE_URL = "https://manga2kindle.edufdezsoy.es"
    }

    // TODO: remove this from here once the database is set
    val database = M2kDatabase(this)

    override fun onCreate() {
        super.onCreate()
    }
}