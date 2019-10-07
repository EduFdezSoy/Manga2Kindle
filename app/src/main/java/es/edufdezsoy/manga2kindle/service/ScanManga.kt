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
        // Oneshot chapters usually dont have Vol or Ch
        Pattern.compile(".*Oneshot.*"),
        // Manga Plus chapter numeration: #NNN (looks like manga plus never add Vol. to their chapters)
        Pattern.compile("[#]\\d+.*"),
        // Manga Rock, this one uses Chapter NNN, what a nightmare
        Pattern.compile(".*Chapter \\d+.*"),
        // LectorManga, uses Capítulo N.NN
        // TuMangaOnline, same
        Pattern.compile(".*Capítulo \\d+.*"),
        // NHentai, it only says Chapter
        Pattern.compile("Chapter")
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
                // if the folder path is empty we jump the entry
                if (it.path.isBlank())
                    return@forEach // continue like sentence

                val docFile = DocumentFile.fromTreeUri(this@ScanManga, Uri.parse(it.path))
                if (docFile!!.canRead()) {
                    val list = getListOfFoldersNFiles(docFile)
                    val mangas = searchForMangas(list)

                    mangas.forEach {
                        Log.d(TAG, formatName(it.name))
                    }

                    mangas.forEach {
                        val mangaName = formatName(it.name)

                        var manga = M2kDatabase(this@ScanManga).MangaDao().search(mangaName)
                        if (manga.isEmpty()) {
                            manga = ApiService.apiService.searchManga(mangaName)

                            if (manga.isEmpty()) {
                                manga = listOf(Manga(null, mangaName, null))
                            }

                            M2kDatabase(this@ScanManga).MangaDao().insert(manga[0])
                            manga = M2kDatabase(this@ScanManga).MangaDao().search(mangaName)
                        }

                        val chapters = getChapters(it)

                        chapters.forEach {
                            // this string will be something like:
                            // [Vol.N] Ch.N [- Chapter Name]
                            // N can be any number, 1, 2 or 3 digits
                            // Chapter Name can have numbers take care with it
                            val chapterName = formatName(it.name)

                            var chapterTitle: String? = ""
                            val chapterNum = pickChapter(chapterName)
                            val chapterVol = pickVolume(chapterName)


                            val str = chapterName.split(" - ")

                            // chapterName
                            if (str.size == 2) {
                                chapterTitle = str.last() + " - "
                            } else if (str.size > 2) {
                                var first = true
                                str.forEach {
                                    if (!first) {
                                        chapterTitle += it + " - "
                                    } else {
                                        first = false
                                    }
                                }
                            }
                            if (!chapterTitle.isNullOrBlank())
                                chapterTitle = chapterTitle.substring(0, chapterTitle.length - 3)

                            if (chapterTitle.isNullOrBlank()) {
                                val str = chapterName.split(": ")

                                // chapterName
                                if (str.size == 2) {
                                    chapterTitle = str.last() + ": "
                                } else if (str.size > 2) {
                                    var first = true
                                    str.forEach {
                                        if (!first) {
                                            chapterTitle += it + ": "
                                        } else {
                                            first = false
                                        }
                                    }
                                }
                                if (!chapterTitle.isNullOrBlank())
                                    chapterTitle =
                                        chapterTitle.substring(0, chapterTitle.length - 2)
                            }

                            if (chapterTitle.isNullOrBlank()) {
                                chapterTitle = null
                            }

                            try {
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
                            } catch (e: Exception) {
                                Log.e(
                                    TAG, "Error in 116, thats the data I can see useful: \n" +
                                            "manga: List<Manga> Size = " + manga.size + "\n" +
                                            "docFile name = " + chapterName + "\n" +
                                            "uri = " + it.uri
                                )
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    Log.e(
                        TAG,
                        "Can't read the folder. \n Folder: " + it.name + " (" + it.path + ")"
                    )
                    // -+-+-+-+-+-+ DEBUG +-+-+-+-+-+-
                    if (M2kApplication.debug) {
                        try {
                            docFile.listFiles()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    // -+-+-+-+-+-+ DEBUG +-+-+-+-+-+-

                }
            }
            // -+-+-+-+-+-+ DEBUG +-+-+-+-+-+-
            if (M2kApplication.debug) {
                val chaptersDebug = M2kDatabase(this@ScanManga).ChapterDao().getAll()

                Log.i(TAG, "Mangas in database: " + chaptersDebug.size)
                chaptersDebug.forEach {
                    Log.d(
                        TAG,
                        "Chapter:" + it.manga_id + " Vol." + it.volume + " Ch." + it.chapter + " - " + it.title
                    )
                }
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
        val tmpChapterRegex = arrayOf(".*_tmp", ".*_temp")

        manga.listFiles().forEach {
            if (it.isDirectory) {
                chapterRegex.forEach(fun(regex: Pattern) {
                    if (regex.matcher(it.name!!).matches()) {
                        var temporal = false
                        tmpChapterRegex.forEach(fun(regex: String) {
                            if (Pattern.compile(regex).matcher(it.name!!).matches()) {
                                temporal = true
                                return
                            }
                        })
                        if (!temporal) {
                            chapters.add(it)
                        } else {
                            if (M2kApplication.debug)
                                Log.d(TAG, "This chapter is not downloaded yet (" + it.name + ")")
                        }
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

        if (chapter.isBlank())
            chapter = "0"

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

    /**
     * Format the name to a more standard way
     * This method is not private to allow tests of it
     *
     * @return formatted string
     */
    fun formatName(name: String?): String {
        if (name.isNullOrBlank())
            return ""

        // Replace multiples white spaces
        var outName = name.replace(Regex("[ \\xa0]{2,}"), " ")

        /*
         * CAUTION: take care with this!
         * The order here is really important!
         * Use the unit test as many times as you need (also extend it if useful)
         */

        // Match: Chapter Name _ chapter something -to-make-> Chapter Name - chapter something
        outName = outName.replace(Regex("\\s[_]\\s"), " - ")

        // Match: Chapter Name_ chapter something -to-make-> Chapter Name: chapter something
        outName = outName.replace(Regex("[_]\\s"), ": ")

        return outName
    }

//#endregion
}
