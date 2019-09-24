package es.edufdezsoy.manga2kindle.service

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.network.ApiService
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class UploadChapter(val context: Context) {
    private val TAG = M2kApplication.TAG + "_UploadChap"
    private val FOLDERNAME = "chapterTemp"
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
        val zipName = manga.title + " Ch." + chapter.chapter
        zip(Uri.parse(chapter.file_path), zipName)


        // upload the chapter
//        ApiService.apiService.sendChapter(
//            manga_id = chapter.manga_id,
//            lang_id = chapter.lang_id,
//            title = chapter.title,
//            chapter = chapter.chapter,
//            volume = chapter.volume,
//            checksum = chapter.checksum,
//            mail = mail,
//            file = chapter
//        )
    }

    private suspend fun zip(uri: Uri, zipFileName: String) {
        // create the temp folder
        val path = File(context.filesDir, FOLDERNAME)
        path.mkdirs()
        // get the list of files inside that uri
        val uriList = getUriList(getFileList(uri))

        // zip the file, this is another function because yes
        zip(uriList, zipFileName)
    }

    private suspend fun zip(files: List<Uri>, zipFileName: String) {
        try {
            val BUFFER = 2048
            var origin: BufferedInputStream?
            val dest = FileOutputStream(File(File(context.filesDir, FOLDERNAME), zipFileName))
            val out = ZipOutputStream(BufferedOutputStream(dest))
            val data = ByteArray(BUFFER)

            files.forEach {
                Log.v(TAG, "Compressing. Adding: \n" + it.toString())
                val fd = context.contentResolver.openFileDescriptor(it, "r")
                val fi = FileInputStream(fd!!.fileDescriptor)
                origin = BufferedInputStream(fi, BUFFER)

                val entry = ZipEntry(getFileName(it))
                out.putNextEntry(entry)
                var count: Int

                do {
                    count = origin!!.read(data, 0, BUFFER)
                    if (count != -1) {
                        out.write(data, 0, count)
                    }
                } while (count != -1)

                origin!!.close()
                fi.close()
                fd.close()
            }
            out.close()
            dest.close()
        } catch (e: Exception) {
            Log.e(TAG, "Something goes wrong while zipping!")
            e.printStackTrace()
        }
    }

    private fun getFileList(uri: Uri): List<DocumentFile> {
        val docFileList = ArrayList<DocumentFile>()

        val badDocFile = DocumentFile.fromTreeUri(context, uri)

        if (badDocFile != null && badDocFile.isDirectory && badDocFile.canRead()) {

            // WORKAROUND: iterate folders inside that until the uri match with our one
            val docFile = getTheRightDocFile(badDocFile, uri)

            if (docFile != null) {
                docFile.listFiles().forEach {
                    docFileList.add(it)
                }
            }
        } else {
            Log.e(
                TAG,
                "Can't read the folder, is null or is not a folder. \n Folder: " + uri.toString()
            )
        }

        return docFileList
    }

    private fun getUriList(docFileList: List<DocumentFile>): List<Uri> {
        val uriList = ArrayList<Uri>()

        docFileList.forEach {
            uriList.add(it.uri)
        }

        return uriList
    }

    /**
     * WORKAROUND: iterate folders inside that until the uri match with our one
     * // TODO: WORKAROUND, need a fix if answered: https://stackoverflow.com/questions/58078606/documentfile-not-opening-the-correct-uri
     *
     * @return a docFile if it match with the uri or null it it cant find it
     */
    private fun getTheRightDocFile(docFile: DocumentFile, uri: Uri): DocumentFile? {
        docFile.listFiles().forEach {
            if (it.isDirectory) {
                if (it.uri.toString() == uri.toString()) {
                    return it
                } else {
                    val df = getTheRightDocFile(it, uri)
                    if (df != null && df.isDirectory) {
                        if (df.uri.toString() == uri.toString()) {
                            return df
                        }
                    }
                }

            }
        }
        return null
    }

    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            cursor?.close()
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}