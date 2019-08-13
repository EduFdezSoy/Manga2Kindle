package es.edufdezsoy.manga2kindle

import android.app.Application
import androidx.room.Room
import es.edufdezsoy.manga2kindle.data.M2kDatabase

class M2kApplication : Application() {
    val TAG = "MANGA2KINDLE"

    // TODO: remove this from here once the database is set
    val database = Room.databaseBuilder(applicationContext, M2kDatabase::class.java, "Manga2Kindle").build()

    override fun onCreate() {
        super.onCreate()
    }
}