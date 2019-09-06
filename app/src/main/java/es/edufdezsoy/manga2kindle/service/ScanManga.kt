package es.edufdezsoy.manga2kindle.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ScanManga : Service(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_ScanManga"
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate() {
        job = Job()
        Log.i(TAG, "Service created")
    }

    override fun onDestroy() {
        job.cancel()
        Log.i(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")
        launch {
            val folders = M2kDatabase(this@ScanManga).FolderDao().getAll()
            folders.forEach {
                val docFile = DocumentFile.fromTreeUri(this@ScanManga, Uri.parse(it.path))
                val list = getListOfFoldersNFiles(docFile!!)

                list.forEach {
                    Log.d("AAAA", it)
                }
            }
        }
        return START_STICKY
    }

    /**
     *
     * @return an ordered list of folders and files
     */
    private fun getListOfFoldersNFiles(doc: DocumentFile): List<String> {
        val tree = ArrayList<String>()
        var supTree: List<String>

        doc.listFiles().forEach {
            if (it.isDirectory) {
                supTree = getListOfFoldersNFiles(it)

                tree.add(it.name!! + '/')
                tree.addAll(supTree)
            } else {
                tree.add(it.name!!)
            }
        }

        return tree
    }

    /**
     *
     * @return a list of mangas
     */
    private fun searchForMangas(tree: List<String>): List<String> {

        return tree
    }
}
