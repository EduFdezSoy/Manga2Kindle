package es.edufdezsoy.manga2kindle.service

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.network.ApiService
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

class UploadChapter(val context: Context) {
    private val TAG = M2kApplication.TAG + "_UploadChap"
    private val database = M2kDatabase.invoke(context)


    suspend fun upload(chapter: Chapter, mail: String) {
        var manga = database.MangaDao().getMangaById(chapter.manga_id)

        if (!manga.synchronized) { // if the manga does not exist in the server...
            // check if the manga is alredy there
            val mangaList = ApiService.apiService.searchManga(manga.title)

            if (mangaList.isEmpty()) { // if it is really not in the server...
                // check the author
                if (manga.author_id != null) { // if we have an author, we add the manga
                    val mangaList = ApiService.apiService.addManga(manga.title, manga.author_id!!)
                    manga.synchronized = true
                    manga.id = mangaList[0].id
                    database.MangaDao().update(manga)

                } else { // if we dont have an author we will throw an exception
                    throw IllegalArgumentException("There is no author in this manga")
                }
            } else {
                manga = mangaList[0]
            }
        }

        // manga exists now and is in sync with the server and all that crap.
        // check other chapter stuff
        if (chapter.lang_id == null) {
            // TODO("launch an exception") // for now it will be EN always
            chapter.lang_id = 1 // 1 EN, 2 ES == https://manga2kindle.edufdezsoy.es/languages
        }

        if (chapter.file_path == null) {
            throw IllegalArgumentException("There is no path to this chapter, probably it was deleted from the disk")
        }

        // compress the file and get the checksum (md5)
        val inputStream = context.contentResolver.openInputStream(Uri.parse(chapter.file_path))
        val tempFile = File.createTempFile("M2K_", "")
        tempFile.deleteOnExit()
        val outputStream = FileOutputStream(tempFile)
        IOUtils.copy(inputStream, outputStream)
        // upload the chapter
        ApiService.apiService.sendChapter(
            manga_id = chapter.manga_id,
            lang_id = chapter.lang_id,
            title = chapter.title,
            chapter = chapter.chapter,
            volume = chapter.volume,
            checksum = chapter.checksum,
            mail = mail,
            file = chapter
        )
    }

    private suspend fun zip(uri: Uri) {
        // create the temp folder
        val path = File(context.filesDir, "chapterTemp")
        path.mkdirs()

        // get the list of files inside that uri
        val docList = getFileList(uri)

    }

    private fun getFileList(uri: Uri): List<DocumentFile> {
        val docFileList = ArrayList<DocumentFile>()

        val docFile = DocumentFile.fromTreeUri(context, uri)
        if (docFile != null && docFile.isDirectory && docFile.canRead()) {
            docFile.listFiles().forEach {
                docFileList.add(it)
            }

        } else {
            Log.e(TAG, "Can't read the folder, is null or is not a folder. \n Folder: " + uri.toString())
        }

        return docFileList
    }
}