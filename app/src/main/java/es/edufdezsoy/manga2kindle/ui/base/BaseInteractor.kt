package es.edufdezsoy.manga2kindle.ui.base

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import es.edufdezsoy.manga2kindle.service.intentService.ScanMangaIntentService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import java.util.concurrent.atomic.AtomicBoolean

class BaseInteractor(val controller: Controller) {
    interface Controller {

    }

    private lateinit var receiver: BroadcastReceiver
    private var scanning = AtomicBoolean(false)

    fun scanMangas(context: Context) {
        if (scanning.compareAndSet(false, true)) {
            ScanMangaIntentService.enqueueWork(context, Intent())
        }

        // register receiver
        if (!::receiver.isInitialized) {
            val filter = IntentFilter(BroadcastReceiver.ACTION_SCAN_MANGA)
            filter.addCategory(Intent.CATEGORY_DEFAULT)
            receiver = BroadcastReceiver(BroadcastReceiver.ACTION_SCAN_MANGA) {
                scanning.set(false)

                val broadcastIntent = Intent()
                broadcastIntent.action = BroadcastReceiver.ACTION_UPDATED_CHAPTER_LIST
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
                context.sendBroadcast(broadcastIntent)
            }
            context.registerReceiver(receiver, filter)
        }
    }

    fun close(context: Context) {
        if (::receiver.isInitialized)
            context.unregisterReceiver(receiver)
    }

    fun isScanning() : Boolean {
        return scanning.get()
    }
}