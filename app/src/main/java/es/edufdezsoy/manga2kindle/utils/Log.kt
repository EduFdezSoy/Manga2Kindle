package es.edufdezsoy.manga2kindle.utils

import android.util.Log
import es.edufdezsoy.manga2kindle.BuildConfig

object Log {
    fun v(tag: String, msg: String) {
        Log.v(tag, msg)

        if (BuildConfig.DEBUG)
            LogRegistry.getInstance(null).log.add("V: $tag: $msg")
    }

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)

        if (BuildConfig.DEBUG)
            LogRegistry.getInstance(null).log.add("D: $tag: $msg")
    }

    fun i(tag: String, msg: String) {
        Log.i(tag, msg)

        if (BuildConfig.DEBUG)
            LogRegistry.getInstance(null).log.add("I: $tag: $msg")
    }

    fun w(tag: String, msg: String) {
        Log.w(tag, msg)

        if (BuildConfig.DEBUG)
            LogRegistry.getInstance(null).log.add("W: $tag: $msg")
    }

    fun e(tag: String, msg: String) {
        Log.e(tag, msg)

        if (BuildConfig.DEBUG)
            LogRegistry.getInstance(null).log.add("E: $tag: $msg")
    }
}