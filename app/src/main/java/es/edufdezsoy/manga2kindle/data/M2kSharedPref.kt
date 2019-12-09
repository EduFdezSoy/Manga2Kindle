package es.edufdezsoy.manga2kindle.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object M2kSharedPref {
    @Volatile
    private var instance: SharedPreferences? = null
    private val LOCK = Any()

    operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
        instance ?: context.getSharedPreferences(
            "es.edufdezsoy.manga2kindle_preferences",
            Context.MODE_PRIVATE
        ).also { instance = it }
    }
}