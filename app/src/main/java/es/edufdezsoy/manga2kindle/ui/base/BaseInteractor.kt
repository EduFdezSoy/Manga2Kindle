package es.edufdezsoy.manga2kindle.ui.base

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import es.edufdezsoy.manga2kindle.service.intentService.ScanMangaIntentService
import es.edufdezsoy.manga2kindle.service.intentService.UploadChapterIntentService
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver
import java.util.concurrent.atomic.AtomicBoolean

class BaseInteractor(val controller: Controller) {
    interface Controller {

    }

    private lateinit var scanMangaReceiver: BroadcastReceiver
    private lateinit var uploadChapterReceiver: BroadcastReceiver
    private var scanning = AtomicBoolean(false)
    private var uploading = AtomicBoolean(false)
    private val handler = Handler()

    fun scanMangas(context: Context) {
        if (scanning.compareAndSet(false, true)) {
            ScanMangaIntentService.enqueueWork(context, Intent())
        }

        // register receiver
        if (!::scanMangaReceiver.isInitialized) {
            val filter = IntentFilter(BroadcastReceiver.ACTION_SCAN_MANGA)
            filter.addCategory(Intent.CATEGORY_DEFAULT)
            scanMangaReceiver = BroadcastReceiver(BroadcastReceiver.ACTION_SCAN_MANGA) {
                scanning.set(false)

                val broadcastIntent = Intent()
                broadcastIntent.action = BroadcastReceiver.ACTION_UPDATED_CHAPTER_LIST
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
                context.sendBroadcast(broadcastIntent)

                handler.postDelayed(
                    { scanMangas(context) },
                    1000 * 60
                ) // every minute after finished
            }
            context.registerReceiver(scanMangaReceiver, filter)
        }
    }

    fun uploadChapter(context: Context) {
        if (uploading.compareAndSet(false, true)) {
            UploadChapterIntentService.enqueueWork(context, Intent())
        }

        // register receiver
        if (!::uploadChapterReceiver.isInitialized) {
            val filter = IntentFilter(BroadcastReceiver.ACTION_UPLOADED_CHAPTER)
            filter.addCategory(Intent.CATEGORY_DEFAULT)
            uploadChapterReceiver = BroadcastReceiver(BroadcastReceiver.ACTION_UPLOADED_CHAPTER) {
                uploading.set(false)

                handler.postDelayed(
                    { uploadChapter(context) },
                    1000 * 60
                ) // every minute after finished
            }
            context.registerReceiver(uploadChapterReceiver, filter)
        }
    }

    fun close(context: Context) {
        handler.removeCallbacksAndMessages(null)

        if (::scanMangaReceiver.isInitialized)
            context.unregisterReceiver(scanMangaReceiver)

        if (::uploadChapterReceiver.isInitialized)
            context.unregisterReceiver(uploadChapterReceiver)
    }

    fun isScanning(): Boolean {
        return scanning.get()
    }

    fun getMail(context: Context): String {
        return context.getSharedPreferences("es.edufdezsoy.manga2kindle_preferences", Context.MODE_PRIVATE)
            .getString("kindle_mail", "")!!
    }
}