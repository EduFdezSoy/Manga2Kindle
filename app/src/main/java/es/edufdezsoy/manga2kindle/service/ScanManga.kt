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
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

class ScanManga : Service(), CoroutineScope {
    private val TAG = M2kApplication.TAG + "_ScanManga"
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

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
        launch {
            val folders = M2kDatabase(this@ScanManga).FolderDao().getAll()
            folders.forEach {
                val docFile = DocumentFile.fromTreeUri(this@ScanManga, Uri.parse(it.path))
                if (docFile!!.canRead()) {
                    val list = getListOfFoldersNFiles(docFile)
                    val mangas = searchForMangas(list)
                    val series = getSeries(list, mangas)

                    if (mangas.size == 1) {
                        Log.i(
                            TAG,
                            "Only one manga found for this chapters (thats the right thing!)"
                        )
                        val manga = M2kDatabase(this@ScanManga).MangaDao().search(mangas[0])

                        mangas.forEach {
                            // this string will be something like:
                            // [Vol.N] Ch.N [- Chapter Name]
                            // N can be any number, 1, 2 or 3 digits
                            // Chapter Name can have numbers take care with it

                            var chapterName = ""
                            var chapterNum: Int
                            var chapterVol: Int

                            val str = it.split(" - ")

                            // chapterName
                            if (str.size == 2) {
                                chapterName = str.last()
                            } else if (str.size > 2) {
                                var first = true
                                str.forEach {
                                    if (!first) {
                                        chapterName += it
                                    } else {
                                        first = false
                                    }
                                }
                            }

                            // chapterNum
                            chapterNum =

                            // chapterVol


                            if (M2kDatabase(this@ScanManga).ChapterDao().search(manga[0].id,))
                        }
                    } else {
                        Log.e(TAG, "More than one manga found for this chapters (uuh, thats bad!)")
                    }

                    list.forEach {
                        Log.d(TAG, it)
                    }

                    mangas.forEach {
                        Log.d(TAG, it)
                    }

                    series.forEach {
                        Log.d(TAG, it)
                    }
                } else {
                    Log.e(
                        TAG,
                        "Can't read the folder. \n Folder: " + it.name + "\n (" + it.path + ")"
                    )
                }
            }
            stopSelf(startId)
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

                tree.add(it.name!!)
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
        val mangas = ArrayList<String>()
        val mangaPageRegex = Pattern.compile("[0-9]{3}\\.(png|jpg)")
        var canBe = false

        tree.asReversed().forEach {
            if (mangaPageRegex.matcher(it).matches()) {
                canBe = true
            } else {
                if (canBe)
                    mangas.add(it)

                canBe = false
            }
        }

        return mangas
    }

    /**
     *
     * @return a list of series based on the list of mangas
     */
    private fun getSeries(tree: List<String>, mangas: List<String>): List<String> {
        val series = ArrayList<String>()
        var thisOne = false

        mangas.forEach { manga ->
            tree.asReversed().forEach {
                if (thisOne) {
                    series.add(it)
                    thisOne = false
                }

                if (it == manga) {
                    thisOne = true
                }
            }
        }

        return series
    }
}
