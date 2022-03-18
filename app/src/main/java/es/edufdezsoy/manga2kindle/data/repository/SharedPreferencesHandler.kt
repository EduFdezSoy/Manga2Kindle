package es.edufdezsoy.manga2kindle.data.repository

import android.content.Context
import androidx.core.content.edit

class SharedPreferencesHandler(context: Context) {
    private val pref =
        context.getSharedPreferences("manga2kindle_preferences", Context.MODE_PRIVATE)

    var onBoarding: Boolean
        get() = pref.getBoolean("on_boarding", false)
        set(value) = pref.edit { putBoolean("on_boarding", value) }

    var kindleEmail: String?
        get() = pref.getString("kindle_email", null)
        set(value) = pref.edit { putString("kindle_email", value) }

    var uploadOnlyOnWifi: Boolean
        get() = pref.getBoolean("upload_only_on_wifi", false)
        set(value) = pref.edit { putBoolean("upload_only_on_wifi", value) }

    var deleteAfterUpload: Boolean
        get() = pref.getBoolean("delete_after_upload", false)
        set(value) = pref.edit { putBoolean("delete_after_upload", value) }

    /**
     * Value in minutes to delete the chapter after upload
     */
    var deleteAfterUploadWaitTime: Int
        get() = pref.getInt("delete_after_upload_wait_time", 0)
        set(value) = pref.edit { putInt("delete_after_upload_wait_time", value) }

    var hideHiddenList: Boolean
        get() = pref.getBoolean("hide_hidden_list", false)
        set(value) = pref.edit { putBoolean("hide_hidden_list", value) }

    /**
     * Theme override:
     * 0 - auto, not set, let the system decide
     * 1 - clear/day theme
     * 2 - dark/night theme
     */
    var theme: Int
        get() = pref.getInt("app_theme", 0)
        set(value) = pref.edit { putInt("app_theme", value) }

    /**
     * Order:
     * 0 - Manga title, then chapter number ASC
     * 1 - Manga title, then chapter number DESC
     * 2 - Chapter number ASC
     * 3 - Chapter number DESC
     */
    var order: Int
        get() = pref.getInt("chapter_order", 0)
        set(value) = pref.edit { putInt("chapter_order", value) }
}