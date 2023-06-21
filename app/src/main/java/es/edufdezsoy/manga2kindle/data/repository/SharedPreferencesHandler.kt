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

    /**
     * On convert how do we read it? Like a manga or like a comic?
     * manga: RTL (japan style)
     * comic: LTR (western style)
     *
     * Valid values are: manga, comic
     */
    var readMode: String
        get() = pref.getString("chapter_read_mode", "manga")!!
        set(value) = pref.edit {
            when (value) {
                "manga",
                "comic" -> pref.edit { putString("chapter_read_mode", value) }

                else -> throw IllegalArgumentException("Valid values are: \"manga\" and \"comic\"")
            }
        }

    /**
     * How do we show double pages?
     *
     * split -> cut the page in two pages
     * rotate -> shows the page in landscape
     * split and rotate -> rotate first then shows both sides (3 pages total)
     */
    var splitType: String
        get() = pref.getString("chapter_split_type", "split and rotate")!!
        set(value) {
            when (value) {
                "split",
                "rotate",
                "split and rotate" -> pref.edit { putString("chapter_split_type", value) }

                else -> throw IllegalArgumentException("Valid values are: \"split\", \"rotate\" and \"split and rotate\"")
            }
        }
}