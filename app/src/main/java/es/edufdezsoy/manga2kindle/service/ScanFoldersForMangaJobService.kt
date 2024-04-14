package es.edufdezsoy.manga2kindle.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import es.edufdezsoy.manga2kindle.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class ScanFoldersForMangaJobService : JobService() {
    //region vars and vals
    private val TAG = this::class.java.simpleName
    private var jobCancelled = false
    private val chapterRegex = arrayOf(
        // General Regex, usually works with all apps (Ch.NNN)
        // Mangadex: scanlator_Vol.N Ch.N
        Pattern.compile(".*Ch.\\d+.*"),
        Pattern.compile(".*Ch. \\d+.*"),
        // Oneshot chapters usually dont have Vol or Ch
        // Danbooru, Pixiv
        Pattern.compile(".*Oneshot.*"),
        // Manga Plus: #NNN (looks like manga plus never add Vol. to their chapters)
        // Guya: scanlator_N - Title
        Pattern.compile(".*[_#]\\d+.*"),
        // Bato.to: Chapter NN
        // Manga Rock: Chapter NNN
        Pattern.compile(".*Chapter \\d+.*"),
        // Read Kaguya-sama Manga Online: Chapter N - title
        Pattern.compile("Chapter \\d+.*"),
        // LectorManga: scanlator_Capítulo N.NN
        // TuMangaOnline, same
        Pattern.compile(".*[_]Capítulo \\d+.*"),
        // Ninemanga: Capítulo NN
        Pattern.compile(".*Capítulo \\d+.*"),
        // NHentai: scanlator_Chapter (no number)
        Pattern.compile(".*[_]Chapter"),
        // NHentai (unoriginal): Chapter (no number)
        Pattern.compile("Chapter"),
        // HeavenManga: Chap NN
        Pattern.compile(".*Chap \\d+.*"),
        // Webtoons.com: EP NN_ title
        Pattern.compile(".*EP \\d+.*"),
        // Others starting with NN
        Pattern.compile("\\d+.*"),
        // MangaLife: something N+
        Pattern.compile(".*\\d+"),
        // Unknown, no volume nor chapter numbers, only a title (keep always as the last one)
        Pattern.compile(".*[_].*"),
    )

    //endregion
    //region override methods

    private val job = Job()
    private val coroutuneScope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: job started")

        doBackgroundWork(params)

        return true // true means we are doing things in the background
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob: Job Cancelled before completion")
        jobCancelled = true
        return true // true if we need to run it again (if something fails, for example)
    }

    //endregion
    //region private methods

    private fun doBackgroundWork(params: JobParameters?) {
        val wantsReschedule = false

        coroutuneScope.launch Service@{
            Log.i(TAG, "doBackgroundWork: perform the manga scan")

            //region repositories
            val chapterRepository = ChapterRepository(application)
            val mangaRepository = MangaRepository(application)
            val folderRepository = FolderRepository(application)
            //endregion

            val folders = folderRepository.getStaticFolderList()
            if (folders.isEmpty()) {
                Log.i(TAG, "No folders to scan")
                return@Service
            }

            folders.forEach { folder ->
                if (!folder.active)
                    return@forEach

                if (folder.path.isBlank())
                    return@forEach

                val folderUri = Uri.parse(folder.path)
                val cbzList = findCbzFiles(baseContext, folderUri.toString())

                cbzList.forEach FileLoop@{ cbz ->
                    val mangaFile = DocumentFile.fromSingleUri(baseContext, cbz)
                    //#region parse manga info
                    if (mangaFile!!.name.isNullOrBlank())
                        return@FileLoop

                    // get manga title (the folder)
                    val mangaSeries = getParentName(mangaFile)
                    Log.d(TAG, "Manga: $mangaSeries")

                    val mangaName = formatName(mangaFile.name)

                    var mangaChapterTitle: String? = formatName(getChapterTitle(mangaName))
                    val mangaChapter = pickChapter(mangaName)
                    val mangaVolume = pickVolume(mangaName, mangaChapter)

                    if (mangaChapterTitle.isNullOrBlank())
                        mangaChapterTitle = null

                    //#endregion
                    //#region get extra data from MangaDex API
                    // TODO: we want to call the MangaDex API to fill the author and maybe the cover
                    //#endregion
                    //#region add manga to database
                    // first add manga series
                    val mangaOb = mangaRepository.searchOrCreate(mangaSeries)
                    // then check if manga chapter exists
                    val chapterOb = chapterRepository.search(mangaOb.mangaId, mangaChapter)
                    if (chapterOb != null && chapterOb.path == cbz.toString()) {
                        return@FileLoop
                    } else {
                        chapterRepository.insert(
                            Chapter(
                                mangaChapterTitle,
                                mangaChapter,
                                mangaVolume,
                                mangaFile.toString(),
                                mangaOb.mangaId
                            )
                        )
                    }
                    //#endregion

                    Log.i(
                        TAG,
                        "Manga: $mangaSeries - Vol.$mangaVolume Ch.$mangaChapter - $mangaChapterTitle"
                    )
                }
            }

            jobFinished(params, wantsReschedule)
        }
    }

    /**
     * Find all the cbz files in the folder passed down to 3 levels deep
     * @param context the context
     * @param folderUri the folder uri
     * @return a list of cbz file uris
     *
     * Why is it like this? Cause some weird shit happens with the f*king DocumentFiles
     * and when making it recursive it just loops the same folder forever
     */
    private fun findCbzFiles(context: Context, folderUri: String): List<Uri> {
        val cbzFiles = mutableListOf<Uri>()

        val parentFolder = DocumentFile.fromTreeUri(context, Uri.parse(folderUri))

        Log.d(TAG, "File: ${parentFolder?.uri}")

        // I f*king hate this.
        parentFolder?.listFiles()?.forEach {
            if (it.name?.endsWith(".cbz") == true) {
                cbzFiles.add(it.uri)
            } else if (it.isDirectory) {
                it.listFiles().forEach { file ->
                    if (file.name?.endsWith(".cbz") == true) {
                        cbzFiles.add(file.uri)
                    } else if (file.isDirectory) {
                        file.listFiles().forEach { file2 ->
                            if (file2.name?.endsWith(".cbz") == true) {
                                cbzFiles.add(file2.uri)
                            } else if (file2.isDirectory) {
                                file2.listFiles().forEach { file3 ->
                                    if (file3.name?.endsWith(".cbz") == true) {
                                        cbzFiles.add(file3.uri)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        cbzFiles.forEach {
            Log.d(TAG, "File: $it")
        }

        return cbzFiles
    }

    /**
     * Pick the chapter number from the folder name passed
     * (this fun is public in order to perform tests)
     *
     * @param name a folder name from a chapter
     * @return the chapter number or 0 if none
     */
    fun pickChapter(name: String): Float {
        val chapterRegex = Pattern.compile("[+-]?\\d+(?:\\.\\d+)?")
        var chapter = ""

        // the chapter name can have numbers, we dont want that numbers so we split it
        val part = name.split(" - ")[0]
        val matcher = chapterRegex.matcher(part)


        while (matcher.find()) {
            chapter = matcher.group()
        }

        if (chapter.isBlank()) {
            // try with the full chapter name
            val matcher2 = chapterRegex.matcher(name)

            if (matcher2.find()) {
                chapter = matcher2.group()
            }

            // if there is no coincidences we put a 0
            if (chapter.isBlank()) {
                chapter = "0"
            }
        }

        return chapter.toFloat()
    }

    /**
     * Pick the volume number from the folder name passed
     * (this fun is public in order to perform tests)
     *
     * @param name chapter name or full filename
     * @param chNum the chapter number, can be null
     * @return the volume number or null if none
     */
    fun pickVolume(name: String, chNum: Float?): Int? {
        val volumeRegex = Pattern.compile("[V-v][O-o][L-l].[+-]?\\d+(?:\\.\\d+)?")
        var volume = ""

        // the chapter name can have numbers, we dont want that numbers so we split it
        val parts = name.split(" - ")
        val firstPart = parts[0].replace(" ", "")
        var matcher = volumeRegex.matcher(firstPart)

        while (matcher.find()) {
            volume = matcher.group()
        }

        val numRegex = Pattern.compile("\\d+")
        matcher = numRegex.matcher(volume)

        while (matcher.find()) {
            volume = matcher.group()
        }

        // we are picking chapters as volumes in MangaLife, this solves that
        if (volume.isNotBlank() && ((parts.size == 1 && chNum == volume.toInt()
                .toFloat()) || volume.length > 2)
        )
            volume = ""

        return if (volume.isBlank())
            null
        else
            volume.toInt()
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

        // remove _ at start
        outName = outName.replace(Regex("^[_]"), "")

        // Match: Chapter Name_, chapter something -to-make-> Chapter Name!, chapter something
        outName = outName.replace(Regex("_,\\s"), "!, ")

        return outName
    }

    /**
     * Splits the vol/chapter info from the chapter title
     *
     * @param chapterName
     * @return the chapter title
     */
    fun getChapterTitle(chapterName: String): String {
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
        if (chapterTitle.isNotBlank())
            chapterTitle = chapterTitle.substring(0, chapterTitle.length - 3)

        if (chapterTitle.isBlank()) {
            val chNameSplit = chapterName.split(": ")

            // chapterName
            if (chNameSplit.size == 2) {
                chapterTitle = chNameSplit.last() + ": "
            } else if (chNameSplit.size > 2) {
                var first = true
                chNameSplit.forEach {
                    if (!first) {
                        chapterTitle += "$it: "
                    } else {
                        first = false
                    }
                }
            }
            if (chapterTitle.isNotBlank())
                chapterTitle = chapterTitle.substring(0, chapterTitle.length - 2)
        }

        chapterTitle = chapterTitle.removeSuffix(".cbz")

        return chapterTitle
    }

    /**
     * A f workaround to get the parent uri of a file cause DocumentFile is broken
     * Google, fix your shit.
     */
    private fun getParentName(file: DocumentFile): String {
        val pathSegments = file.uri.pathSegments
        val splitPath = pathSegments.last().split("/")

        return splitPath[splitPath.size - 2]
    }

    //endregion
}