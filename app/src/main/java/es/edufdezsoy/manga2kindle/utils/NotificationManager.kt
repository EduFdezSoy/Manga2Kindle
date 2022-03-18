package es.edufdezsoy.manga2kindle.utils

import android.content.Context

/**
 * TODO: Manager incomplete
 * This singleton class will manage our notifications.
 * Well have two channels: one to notify new chapters and the other one to show the upload statuses
 * The first one will be able to show a list of new chapters, this list can be expanded or shrinked,
 * the second needs to be able to show a status bar with the progress of the upload/uploads
 * something like:
 *      +----------------------------+
 *      |  (*) Manga2Kindle          |
 *      |  Uploading chapter 2 of 7  |
 *      |  =============-----------  |
 *      +----------------------------+
 */
class NotificationManager {
    companion object : SingletonHolder<NotificationManager, Context>(
        {
            NotificationManager()
        }
    )
}