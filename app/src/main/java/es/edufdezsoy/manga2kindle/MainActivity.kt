package es.edufdezsoy.manga2kindle

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import es.edufdezsoy.manga2kindle.data.model.UploadChapter
import es.edufdezsoy.manga2kindle.service.UploadChapterService

class MainActivity : AppCompatActivity() {
    //region vars and vals

    var intentGoToFragment: Int? = null
    private lateinit var uploadChapterService: UploadChapterService
    private var serviceBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as UploadChapterService.UploadChapterBinder
            uploadChapterService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }

    //endregion
    //region override methods

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        intentGoToFragment = intent.extras?.getInt("fragment")
    }

    override fun onStart() {
        super.onStart()
        if (!serviceBound) {
            Intent(applicationContext, UploadChapterService::class.java).also {
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            unbindService(connection)
            serviceBound = false
        }
    }

    //endregion
    //region public methods

    fun uploadChapter(chapter: UploadChapter) {
        if (!serviceBound) {
            val intent = Intent(applicationContext, UploadChapterService::class.java)
            intent.putExtra(UploadChapterService.UPLOAD_CHAPTER_INTENT_KEY, chapter)

            startService(intent)
        } else {
            uploadChapterService.addUploadChapter(chapter)
        }
    }


    fun uploadChapter(list: ArrayList<UploadChapter>) {
        list.forEach { uploadChapter(it) }
    }

//endregion
}