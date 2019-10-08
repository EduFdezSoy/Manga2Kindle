package es.edufdezsoy.manga2kindle.service

import android.content.Context
import android.net.Uri
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
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

class ScanManga : CoroutineScope {
    //#region vars and vals

    private val TAG = M2kApplication.TAG + "_ScanManga"
    private val job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    // Those regex are from different sources, with them we find the chapters
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

    //#endregion
    //#region constructor and destructor

    init {
        job = Job()
    }

    protected fun finalize() {
        job.cancel()
    }

    //#endregion
    //#region public methods

    fun performScan(context: Context, done: () -> Unit) {
        launch {
            Log.i(TAG, "performing manga scan")

            val finishedCounter = AtomicInteger()
            val database = M2kDatabase(context)

            val folders = database.FolderDao().getAll()
            folders.forEach {
                launch {
                    // if the folder path is empty we stop here
                    if (it.path.isNotBlank()) {
                        val uri = Uri.parse(it.path)
                        val docFile = DocumentFile.fromTreeUri(context, uri)
                        if (docFile!!.canRead()) {
                            val list = getListOfFoldersNFiles(docFile)
                            val mangas = searchForMangas(list)

                            if (M2kApplication.debug)
                                mangas.forEach { Log.d(TAG, formatName(it.name)) }

                            mangas.forEach {
                                val mangaName = formatName(it.name)

                                var manga = database.MangaDao().search(mangaName)
                                if (manga.isEmpty()) {
                                    manga = ApiService.apiService.searchManga(mangaName)
                                    if (manga.isEmpty())
                                        manga = listOf(Manga(null, mangaName, null))

                                    database.MangaDao().insert(manga[0])
                                    manga = database.MangaDao().search(mangaName)
                                }

                                val chapters = getChapters(it)
                                chapters.forEach {
                                    val chapterName = formatName(it.name)
                                    var chapterTitle: String? = getChapterTitle(chapterName)
                                    val chapterNum = pickChapter(chapterName)
                                    val chapterVol = pickVolume(chapterName)

                                    if (chapterTitle.isNullOrBlank())
                                        chapterTitle = null

                                    val chapterExists = database.ChapterDao()
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
                                        database.ChapterDao().insert(chapter)
                                    } else {
                                        if (M2kApplication.debug)
                                            Log.d(
                                                TAG,
                                                formatName(
                                                    "Looks like " + manga[0].identifier
                                                            + " Ch. " + chapterNum + " already exists."
                                                )
                                            )

                                        // if exists check the uri and rewrite it if needed
                                        if (Uri.parse(chapterExists.file_path) != it.uri) {
                                            if (M2kApplication.debug)
                                                Log.d(
                                                    TAG, formatName(
                                                        "Chapter uri missmatch: \n" +
                                                                "Old: " + chapterExists.file_path + "\n" +
                                                                "New: " + it.uri.toString()
                                                    )
                                                )

                                            chapterExists.file_path = it.uri.toString()
                                            if (M2kApplication.debug)
                                                Log.d(TAG, formatName("Chapter uri rewrited."))

                                            database.ChapterDao().update(chapterExists)
                                        }
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

                    if (finishedCounter.incrementAndGet() == folders.size) {
                        done()
                        Log.i(TAG, "Done scanning mangas")
                    }
                }
            }
        }
    }

    //#endregion
    //#region private methods

    /**
     *  Digs in the DocumentFile to read all the folder structure
     *
     * @param doc must be a folder
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
     * Searches mangas in an ordered list of folders and files
     *
     * @param tree an ordered list of folders and files, like the one getListOfFoldersNFiles() does
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
     * Get the chapters from a manga folder
     * This method ignores temporally folders
     *
     * @param manga a manga folder
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
     * Pick the chapter number from the folder name pased
     *
     * @param name a folder name from a chapter
     * @return the chapter number or 0 if none
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
     * Pick the volume number from the folder name pased
     *
     * @param name a folder name from a chapter
     * @return the volume number or null if none
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
     * @param name a chapter name, a folder name, something that we want to parse from folder characters to all characters
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

    private fun getChapterTitle(chapterName: String): String {
        val str = chapterName.split(" - ")
        var chapterTitle = ""

        if (str.size == 2) {
            chapterTitle = str.last() + " - "
        } else if (str.size > 2) {
            var first = true
            str.forEach {
                if (!first) {
                    chapterTitle += "$it - "
                } else {
                    first = false
                }
            }
        }
        if (!chapterTitle.isBlank())
            chapterTitle = chapterTitle.substring(0, chapterTitle.length - 3)

        if (chapterTitle.isBlank()) {
            val str = chapterName.split(": ")

            // chapterName
            if (str.size == 2) {
                chapterTitle = str.last() + ": "
            } else if (str.size > 2) {
                var first = true
                str.forEach {
                    if (!first) {
                        chapterTitle += "$it: "
                    } else {
                        first = false
                    }
                }
            }
            if (!chapterTitle.isBlank())
                chapterTitle =
                    chapterTitle.substring(0, chapterTitle.length - 2)
        }

        return chapterTitle
    }

//#endregion

}