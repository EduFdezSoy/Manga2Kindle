package es.edufdezsoy.manga2kindle.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.ApiService
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
                    val chapters = searchForMangas(list)
                    val series = getSeries(list, chapters)

                    series.forEach {
                        Log.d(TAG, it)
                    }

                    if (series.size == 1) {
                        var manga = M2kDatabase(this@ScanManga).MangaDao().search(series[0])
                        if (manga.isEmpty()) {
                            manga = ApiService.apiService.searchManga(series[0])

                            if (manga.isEmpty()) {
                                manga = manga as ArrayList
                                val mangaAux = M2kDatabase(this@ScanManga).MangaDao().getLastManga()
                                var id = 1
                                if (mangaAux != null) {
                                    id = mangaAux.id + 1
                                }
                                manga.add(Manga(id, series[0], null))
                            }

                            M2kDatabase(this@ScanManga).MangaDao().insert(manga[0])
                        }

                        chapters.forEach {
                            // this string will be something like:
                            // [Vol.N] Ch.N [- Chapter Name]
                            // N can be any number, 1, 2 or 3 digits
                            // Chapter Name can have numbers take care with it

                            var chapterName: String? = ""
                            val chapterNum = pickChapter(it[0])
                            val chapterVol = pickVolume(it[0])


                            val str = it[0].split(" - ")

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

                            if (chapterName == "") {
                                chapterName = null
                            }


                            val chapterExists = M2kDatabase(this@ScanManga).ChapterDao()
                                .search(manga[0].id, chapterNum)
                            if (chapterExists == null) {
                                val chapter = Chapter(
                                    null,
                                    manga[0].id,
                                    null,
                                    chapterVol,
                                    chapterNum,
                                    chapterName,
                                    it[1],
                                    null,
                                    false,
                                    false,
                                    null,
                                    true
                                )
                                M2kDatabase(this@ScanManga).ChapterDao().insert(chapter)
                            }
                        }
                    } else {
                        Log.e(TAG, "More than one manga found for this chapters (uuh, thats bad!)")
                    }
                } else {
                    Log.e(
                        TAG,
                        "Can't read the folder. \n Folder: " + it.name + " (" + it.path + ")"
                    )
                }
            }
            // -+-+-+-+-+-+ DEBUG +-+-+-+-+-+-
            val chaptersDebug = M2kDatabase(this@ScanManga).ChapterDao().getAll()

            Log.i(TAG, "Mangas in database: " + chaptersDebug.size)
            chaptersDebug.forEach {
                Log.d(
                    TAG,
                    "Chapter:" + it.manga_id + " Vol." + it.volume + " Ch." + it.chapter + " - " + it.title
                )
            }
            // -+-+-+-+-+-+ DEBUG +-+-+-+-+-+-

            stopSelf(startId)
        }
        return START_STICKY
    }

    /**
     *
     * @return an ordered list of folders and files with their paths
     */
    private fun getListOfFoldersNFiles(doc: DocumentFile): List<Array<String>> {
        val tree = ArrayList<Array<String>>()

        doc.listFiles().forEach {
            if (it.isDirectory) {
                val supTree = getListOfFoldersNFiles(it)

                tree.add(arrayOf(it.name!!, it.uri.toString()))
                tree.addAll(supTree)
            } else {
                tree.add(arrayOf(it.name!!, it.uri.toString()))
            }
        }

        return tree
    }

    /**
     *
     * @return a list of mangas
     */
    private fun searchForMangas(tree: List<Array<String>>): List<Array<String>> {
        val mangas = ArrayList<Array<String>>()
        val mangaPageRegex = Pattern.compile("[0-9]{3}\\.(png|jpg)")
        var canBe = false

        tree.asReversed().forEach {
            if (mangaPageRegex.matcher(it[0]).matches()) {
                canBe = true
            } else {
                if (canBe)
                    mangas.add(arrayOf(it[0], it[1]))

                canBe = false
            }
        }

        return mangas
    }

    /**
     *
     * @return a list of series based on the list of mangas
     */
    private fun getSeries(tree: List<Array<String>>, mangas: List<Array<String>>): List<String> {
        val series = ArrayList<String>()
        var thisOne = false
        val mangaPageRegex = Pattern.compile("[0-9]{3}\\.(png|jpg)")

        mangas.forEach { manga ->
            tree.asReversed().forEach {
                if (thisOne) {
                    if (!mangaPageRegex.matcher(it[0]).matches()) {
                        series.add(it[0])
                    }
                    thisOne = false
                }

                if (it[0] == manga[0]) {
                    thisOne = true
                }
            }
        }

        return series
    }

    /**
     *
     * @return the chapter number
     */
    private fun pickChapter(name: String): Float {
        val chapterRegex = Pattern.compile("[+-]?\\d+(?:\\.\\d+)?")
        var chapter: String = ""

        // the chapter name can have numbers, we dont want that numbers so we split it
        val part = name.split("-")[0]
        val matcher = chapterRegex.matcher(part)

        while (matcher.find()) {
            chapter = matcher.group()
        }

        return chapter.toFloat()
    }

    /**
     *
     * @return the volume number
     */
    private fun pickVolume(name: String): Int? {
        val volumeRegex = Pattern.compile("[V-v][O-o][L-l].[+-]?\\d+(?:\\.\\d+)?")
        var volume: String = ""

        // the chapter name can have numbers, we dont want that numbers so we split it
        val part = name.split("-")[0]
        var matcher = volumeRegex.matcher(part)

        while (matcher.find()) {
            volume = matcher.group()
        }

        val numRegex = Pattern.compile("\\d+")
        matcher = numRegex.matcher(volume)

        while (matcher.find()) {
            volume = matcher.group()
        }

        if (volume.isBlank())
            return null
        else
            return volume.toInt()
    }


}
