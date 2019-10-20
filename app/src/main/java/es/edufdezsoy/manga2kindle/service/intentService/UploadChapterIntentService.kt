package es.edufdezsoy.manga2kindle.service.intentService

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.service.UploadChapter
import es.edufdezsoy.manga2kindle.service.util.BroadcastReceiver

class UploadChapterIntentService : JobIntentService() {
    private val broadcastIntent = Intent()

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, UploadChapterIntentService::class.java, 0, intent)
        }
    }

    init {
        broadcastIntent.action = BroadcastReceiver.ACTION_UPLOADED_CHAPTER
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT)
    }

    override fun onHandleWork(intent: Intent) {
        Log.v(M2kApplication.TAG, "Service UploadChapterIntentService created")

        val uploadChapter = UploadChapter(this)
        val chapter = intent.getIntExtra(UploadChapter.CHAPTER_ID_KEY,0)
        val mail = intent.getStringExtra(UploadChapter.MAIL_KEY)
        uploadChapter.upload(chapter, mail!!) {
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onDestroy() {
        Log.v(M2kApplication.TAG, "Service UploadChapterIntentService destroyed")
    }
}