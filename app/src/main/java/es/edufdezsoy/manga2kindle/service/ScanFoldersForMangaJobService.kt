package es.edufdezsoy.manga2kindle.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.repository.ChapterRepository
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

class ScanFoldersForMangaJobService : JobService(), CoroutineScope {
    //region vars and vals
    private val TAG = this::class.java.simpleName
    private var jobCancelled = false
    private val chapterRegex = arrayOf(
        // General Regex, usually works with all apps (Ch.NNN)
        Pattern.compile(".*Ch.\\d+.*"),
        // Oneshot chapters usually dont have Vol or Ch
        Pattern.compile(".*Oneshot.*"),
        // Manga Plus chapter numeration: #NNN (looks like manga plus never add Vol. to their chapters)
        // Guya, scanlator_N - Title
        Pattern.compile(".*[_#]\\d+.*"),
        // Manga Rock, this one uses Chapter NNN, what a nightmare
        Pattern.compile(".*Chapter \\d+.*"),
        // LectorManga, uses Capítulo N.NN
        // TuMangaOnline, same
        Pattern.compile(".*Capítulo \\d+.*"),
        // NHentai, it only says Chapter
        Pattern.compile("Chapter"),
        // HeavenManga, Chap NN
        Pattern.compile(".*Chap \\d+.*"),
        // Others starting with NN
        Pattern.compile("\\d+.*"),
        // MangaLife, something NNNN
        Pattern.compile(".*\\d\\d\\d\\d")
    )

    //endregion
    //region override methods

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

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
        var wantsReschedule = false

        launch Service@{
            try {
                Log.i(TAG, "performing manga scan")

                val finishedCounter = AtomicInteger()
                val chapterRepository = ChapterRepository(application)
                val mangaRepository = MangaRepository(application)
                val folderRepository = FolderRepository(application)

                val folders = folderRepository.getStaticFolderList()
                if (folders.isEmpty()) {
                    Log.i(TAG, "No folders to scan")
                    return@Service
                }

                folders.forEach {
                    launch Folder@{
                        if (!it.active)
                            return@Folder

                        if (it.path.isBlank())
                            return@Folder

                        val uri = Uri.parse(it.path)
                        val docFile = DocumentFile.fromTreeUri(applicationContext, uri)

                        if (!docFile!!.canRead()) {
                            Log.e(TAG, "Cant read the folder \n" + it.name + " (" + it.path + ")")
                            return@Folder
                        }

                        val list = getListOfFoldersAndFiles(docFile)
                        val mangaList = searchForMangas(list)

                        mangaList.forEach {
                            val mangaName = formatName(it.name)
                            val manga = mangaRepository.searchOrCreate(mangaName)
                            val chapters = getChapters(it)

                            chapters.forEach {
                                val chName = formatName(it.name)
                                var chTitle: String? = getChapterTitle(chName)
                                val chNum = pickChapter(chName)
                                val chVol = pickVolume(chName)

                                if (chTitle.isNullOrBlank())
                                    chTitle = null

                                val chExists = chapterRepository.search(manga.manga.mangaId, chNum)

                                if (chExists == null) {
                                    chapterRepository.insert(
                                        Chapter(
                                            chTitle,
                                            chNum,
                                            chVol,
                                            it.uri.toString(),
                                            manga.manga.mangaId
                                        )
                                    )
                                } else {
                                    // TODO: check when a chapter already exists
                                }

                            }

                            Log.v(TAG, mangaName)

                            if (jobCancelled)
                                throw InterruptedException("Service was interrupted by the system")
                        }

                        if (finishedCounter.incrementAndGet() == folders.size) {
                            Log.i(TAG, "Done scanning manga folders")
                            Log.d(TAG, "doBackgroundWork: Job Finished")

                            jobFinished(params, wantsReschedule)
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "Error: " + e.message)
                return@Service
            } catch (e: Exception) {
                wantsReschedule = true
                Log.e(TAG, "Error: " + e.message)
            }
        }
    }

    /**
     *  Digs in the DocumentFile to read all the folder structure
     *
     * @param doc must be a folder
     * @return an ordered list of folders and files
     */
    private fun getListOfFoldersAndFiles(doc: DocumentFile): List<DocumentFile> {
        val tree = ArrayList<DocumentFile>()

        doc.listFiles().forEach {
            if (it.isDirectory) {
                val supTree = getListOfFoldersAndFiles(it)

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
        val mangas = ArrayList<DocumentFile>()
        val chapters = ArrayList<DocumentFile>()

        // it first goes all in and finds the chapters
        tree.forEach {
            if (it.isFile && it.name != ".nomedia")
                if (it.parentFile!!.name != ".thumb")
                    chapters.add(it.parentFile!!)
        }

        // then it pics their parents
        chapters.distinct().forEach {
            mangas.add(it.parentFile!!)
        }

        // and returns the unique ones (distinct removes duplicates)
        return mangas.distinct()
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
            // it first check if it is a chapter, a folder with files inside (no more folders)
            if (it.isDirectory) {
                var hasFolders = false

                it.listFiles().forEach {
                    if (it.isDirectory && it.name != ".thumb")
                        hasFolders = true
                }

                if (hasFolders) {
                    it.listFiles().forEach {
                        chapters.addAll(getChapters(it))
                    }
                } else {
                    // return if it is empty
                    if (it.listFiles().isEmpty())
                        return@forEach
                    // return if it is only .nomedia
                    if (it.listFiles().size == 1)
                        if (it.listFiles()[0].name == ".nomedia")
                            return@forEach
                    // return if it is .thumb
                    if (it.name == ".thumb")
                        return@forEach

                    // then it finds the chapter name and that craps
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
                                Log.d(TAG, "This chapter is not downloaded yet (" + it.name + ")")
                            }
                            return
                        }
                    })
                }
            }
        }

        return chapters
    }


    /**
     * Pick the chapter number from the folder name pased
     * (this fun is public in order to perform tests)
     *
     * @param name a folder name from a chapter
     * @return the chapter number or 0 if none
     */
    fun pickChapter(name: String): Float {
        val chapterRegex = Pattern.compile("[+-]?\\d+(?:\\.\\d+)?")
        var chapter: String = ""

        // the chapter name can have numbers, we dont want that numbers so we split it
        val part = name.split(" - ")[0]
        val matcher = chapterRegex.matcher(part)


        while (matcher.find()) {
            chapter = matcher.group()
        }

        if (chapter.isBlank())
            chapter = "0"

        return chapter.toFloat()
    }

    /**
     * Pick the volume number from the folder name passed
     * (this fun is public in order to perform tests)
     *
     * @param name a folder name from a chapter
     * @return the volume number or null if none
     */
    fun pickVolume(name: String): Int? {
        val volumeRegex = Pattern.compile("[V-v][O-o][L-l].[+-]?\\d+(?:\\.\\d+)?")
        var volume: String = ""

        // the chapter name can have numbers, we dont want that numbers so we split it
        val part = name.split(" - ")[0]
        var matcher = volumeRegex.matcher(part)

        while (matcher.find()) {
            volume = matcher.group()
        }

        val numRegex = Pattern.compile("\\d+")
        matcher = numRegex.matcher(volume)

        while (matcher.find()) {
            volume = matcher.group()
        }

        // we are picking chapters as volumes in MangaLife, this solves that
        if (volume.length > 2)
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

        return outName
    }

    /**
     * Splits the vol/chapter info from the chapter title
     *
     * @param chapterName
     * @return the chapter title
     */
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
        if (chapterTitle.isNotBlank())
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
            if (chapterTitle.isNotBlank())
                chapterTitle =
                    chapterTitle.substring(0, chapterTitle.length - 2)
        }

        return chapterTitle
    }

    //endregion
}