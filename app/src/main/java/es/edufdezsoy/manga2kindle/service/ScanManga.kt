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

    private val chapterRegex = arrayOf(
        // General Regex, usually works with all apps (Ch.NNN)
        Pattern.compile(".*Ch.\\d+.*"),
        // Manga Plus chapter numeration: #NNN (looks like manga plus never add Vol. to their chapters)
        Pattern.compile("[#]\\d+.*")
    )


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

                    mangas.forEach {
                        Log.d(TAG, it.name!!)
                    }

                    mangas.forEach {
                        var manga = M2kDatabase(this@ScanManga).MangaDao().search(it.name!!)
                        if (manga.isEmpty()) {
                            manga = ApiService.apiService.searchManga(it.name!!)

                            if (manga.isEmpty()) {
                                manga = listOf(Manga(null, it.name!!, null))
                            }

                            M2kDatabase(this@ScanManga).MangaDao().insert(manga[0])
                            manga = M2kDatabase(this@ScanManga).MangaDao().search(it.name!!)
                        }

                        val chapters = getChapters(it)

                        chapters.forEach {
                            // this string will be something like:
                            // [Vol.N] Ch.N [- Chapter Name]
                            // N can be any number, 1, 2 or 3 digits
                            // Chapter Name can have numbers take care with it

                            var chapterTitle: String? = ""
                            val chapterNum = pickChapter(it.name!!)
                            val chapterVol = pickVolume(it.name!!)


                            val str = it.name!!.split(" - ")

                            // chapterName
                            if (str.size == 2) {
                                chapterTitle = str.last()
                            } else if (str.size > 2) {
                                var first = true
                                str.forEach {
                                    if (!first) {
                                        chapterTitle += it
                                    } else {
                                        first = false
                                    }
                                }
                            }

                            if (chapterTitle == "") {
                                chapterTitle = null
                            }


                            val chapterExists = M2kDatabase(this@ScanManga).ChapterDao()
                                .search(manga[0].identifier, chapterNum)
                            if (chapterExists == null) {
                                val chapter = Chapter(
                                    id = null,
                                    manga_id = manga[0].identifier,
                                    lang_id = null,
                                    volume = chapterVol,
                                    chapter = chapterNum,
                                    title = chapterTitle,
                                    file_path = it.uri.toString(),
                                    checksum = null,
                                    delivered = false,
                                    error = false,
                                    reason = null,
                                    visible = true
                                )
                                M2kDatabase(this@ScanManga).ChapterDao().insert(chapter)
                            }
                        }
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

    //#region private methods

    /**
     *
     * @return an ordered list of folders and files
     */
    private fun getListOfFoldersNFiles(doc: DocumentFile): List<DocumentFile> {
        val tree = ArrayList<DocumentFile>()

        doc.listFiles().forEach {
            if (it.isDirectory) {
                val supTree = getListOfFoldersNFiles(it)

                tree.add(it)
                tree.addAll(supTree)
            } else {
                tree.add(it)
            }
        }

        return tree
    }

    /**
     *
     * @return a list of mangas (those folders may have chapters or may not)
     */
    private fun searchForMangas(tree: List<DocumentFile>): List<DocumentFile> {
        val chapterRegex = chapterRegex
        val mangas = ArrayList<DocumentFile>()
        val mangasAux = ArrayList<DocumentFile>()

        tree.forEach {
            if (it.isDirectory) {
                mangasAux.add(it)
            }
        }

        mangasAux.forEach {
            var matches = false
            chapterRegex.forEach(fun(regex: Pattern) {
                if (regex.matcher(it.name!!).matches()) {
                    matches = true
                    return
                }
            })

            if (!matches) {
                mangas.add(it)
            }
        }

        return mangas
    }

    /**
     * TODO: this method may check if the folder is _tmp
     *
     * @return a list of chapters (in folders) for the given manga
     */
    private fun getChapters(manga: DocumentFile): List<DocumentFile> {
        val chapterRegex = chapterRegex
        val chapters = ArrayList<DocumentFile>()

        manga.listFiles().forEach {
            if (it.isDirectory) {
                chapterRegex.forEach(fun(regex: Pattern) {
                    if (regex.matcher(it.name!!).matches()) {
                        chapters.add(it)
                        return
                    }
                })
            }
        }

        return chapters
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

    //#endregion
}
