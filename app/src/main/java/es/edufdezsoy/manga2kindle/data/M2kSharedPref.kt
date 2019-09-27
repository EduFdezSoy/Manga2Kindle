package es.edufdezsoy.manga2kindle.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object M2kSharedPref {
    @Volatile
    private var instance: SharedPreferences? = null
    private val LOCK = Any()

    operator fun invoke(activity: Activity) = instance ?: synchronized(LOCK) {
        instance ?: activity.getSharedPreferences(
            "manga2kindle",
            Context.MODE_PRIVATE
        ).also { instance = it }
    }
}