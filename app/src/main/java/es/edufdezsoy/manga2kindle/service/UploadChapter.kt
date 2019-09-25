package es.edufdezsoy.manga2kindle.service

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.network.ApiService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


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

        // compress the file
        val zipName = manga.title + " Ch." + trimTrailingZero(chapter.chapter.toString()) + ".zip"
        zip(Uri.parse(chapter.file_path), zipName)

        // get the checksum (md5)
        val chapFile = File(context.filesDir, zipName)
        chapter.checksum = calculateMD5(FileInputStream(chapFile))

        // prepare others fields
        var title = chapter.title
        if (title == null)
            title = ""

        try {
            val reqFile = RequestBody.create(
                MediaType.parse("zip"), // TODO: this may not be hardcoded
                chapFile
            )
            val part = MultipartBody.Part.createFormData("file", chapFile.name, reqFile)

            // upload the chapter
            ApiService.apiService.sendChapter(
                manga_id = manga.id!!,
                lang_id = chapter.lang_id!!,
                title = title,
                chapter = chapter.chapter,
                volume = chapter.volume,
                checksum = chapter.checksum!!,
                mail = mail,
                file = part
            ).also {
                chapter.id = it[0].id
                chapter.sended = true
                database.ChapterDao().update(chapter)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Something's bad with the upload!")
            e.printStackTrace()

        } finally {
            // remove the chapter
            chapFile.delete()
        }
    }

    private suspend fun zip(uri: Uri, zipFileName: String) {
        // get the list of files inside that uri
        val uriList = getUriList(getFileList(uri))

        // zip the file, this is another function because yes
        zip(uriList, zipFileName)
    }

    private suspend fun zip(files: List<Uri>, zipFileName: String) {
        try {
            val BUFFER = 8192
            var origin: BufferedInputStream?
            val dest = FileOutputStream(File(context.filesDir, zipFileName))
            val out = ZipOutputStream(BufferedOutputStream(dest))
            val data = ByteArray(BUFFER)

            files.forEach {
//                Log.v(TAG, "Compressing. Adding: \n" + it.toString())
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

    private suspend fun getFileList(uri: Uri): List<DocumentFile> {
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

    private suspend fun getUriList(docFileList: List<DocumentFile>): List<Uri> {
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
    private suspend fun getTheRightDocFile(docFile: DocumentFile, uri: Uri): DocumentFile? {
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

    private suspend fun getFileName(uri: Uri): String {
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

    private suspend fun calculateMD5(iStr: InputStream): String {
        val BUFFER = 8192
        val digest = MessageDigest.getInstance("MD5")
        val data = ByteArray(BUFFER)

        var count: Int
        do {
            count = iStr.read(data)
            if (count > 0) {
                digest.update(data, 0, count)
            }
        } while (count > 0)

        val md5sum = digest.digest()
        val bigInt = BigInteger(1, md5sum)
        var output = bigInt.toString(16)
        output = String.format("%32s", output).replace(' ', '0')

        return output
    }

    private fun trimTrailingZero(value: String?): String? {
        return if (!value.isNullOrEmpty()) {
            if (value.indexOf(".") < 0) {
                value

            } else {
                value.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
            }

        } else {
            value
        }
    }
}