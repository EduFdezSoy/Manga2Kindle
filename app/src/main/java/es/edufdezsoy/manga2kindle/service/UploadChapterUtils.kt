package es.edufdezsoy.manga2kindle.service

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.data.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object UploadChapterUtils {
    const val TAG = M2kApplication.TAG + "_UpChapUtil"

    /**
     * Zips a file from its uri
     *
     * @param uri uri where the file to be zipped is
     * @param zipFileName string name the file will have
     * @param context the context
     */
    suspend fun zip(uri: Uri, zipFileName: String, context: Context) {
        // get the list of files inside that uri
        val uriList = getUriList(getFileList(uri, context))

        // zip the file, this is another function because yes
        zip(uriList, zipFileName, context)
    }

    /**
     * Zips files from its uris
     *
     * @param files uri list with all the files to be compressed
     * @param zipFileName string name the file will have
     * @param context the context
     */
    suspend fun zip(files: List<Uri>, zipFileName: String, context: Context): String =
        withContext(Dispatchers.Default) {
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

                    val entry = ZipEntry(getFileName(it, context))
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
            return@withContext "alsa"
        }

    /**
     * Calculates the MD5 checksum
     *
     * @param iStr input stream to be checksumed
     * @return the md5 string
     */
    suspend fun calculateMD5(iStr: InputStream): String = withContext(Dispatchers.Default) {
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

        return@withContext output
    }

    /**
     * Get a list of uris from a list of files
     *
     * @param docFileList files to pick the uris
     * @return a list of uris, can be empty
     */
    suspend fun getUriList(docFileList: List<DocumentFile>): List<Uri> =
        withContext(Dispatchers.Default) {
            val uriList = ArrayList<Uri>()

            docFileList.forEach {
                uriList.add(it.uri)
            }

            return@withContext uriList
        }

    /**
     * Get a list of files inside a folder
     *
     * @param uri folder to be searched
     * @param context the context
     * @return a list of files inside the given uri, can be empty
     */
    suspend fun getFileList(uri: Uri, context: Context): List<DocumentFile> =
        withContext(Dispatchers.IO) {
            val docFileList = ArrayList<DocumentFile>()

            val badDocFile = DocumentFile.fromTreeUri(context, uri)

            if (badDocFile != null && badDocFile.isDirectory && badDocFile.canRead()) {

                // WORKAROUND: iterate folders inside that until the uri match with our one
                val docFile = getTheRightDocFile(badDocFile, uri)

                docFile?.listFiles()?.forEach {
                    docFileList.add(it)
                }
            } else {
                Log.e(
                    TAG,
                    "Can't read the folder, is null or is not a folder. \n Folder: " + uri.toString()
                )
            }

            return@withContext docFileList
        }

    /**
     * Get the name of a file
     *
     * @param uri uri from the file
     * @param context the context
     * @return the name of the pased file
     */
    suspend fun getFileName(uri: Uri, context: Context): String =
        withContext(Dispatchers.IO) {
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

            return@withContext result!!
        }

    /**
     * WORKAROUND: iterate folders inside that until the uri match with our one
     * // TODO: WORKAROUND, need a fix if answered: https://stackoverflow.com/questions/58078606/documentfile-not-opening-the-correct-uri
     *
     * @param docFile folder to be iterated
     * @param uri uri to be searched in the folder
     * @return a docFile if it match with the uri or null it it cant find it
     */
    suspend fun getTheRightDocFile(docFile: DocumentFile, uri: Uri): DocumentFile? =
        withContext(Dispatchers.Default) {
            docFile.listFiles().forEach {
                if (it.isDirectory) {
                    if (it.uri.toString() == uri.toString()) {
                        return@withContext it
                    } else {
                        val df = getTheRightDocFile(it, uri)
                        if (df != null && df.isDirectory) {
                            if (df.uri.toString() == uri.toString()) {
                                return@withContext df
                            }
                        }
                    }

                }
            }
            return@withContext null
        }

    /**
     * Removes zeros from strings, Ej: 2.0 => 2
     *
     * @param value string to clean
     * @return cleaned string
     */
    fun trimTrailingZero(value: String?): String? {
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

    suspend fun cleanTextContent(text: String): String = withContext(Dispatchers.Default) {
        var text = text
        // strips off all non-ASCII characters
        text = text.replace("[^\\x00-\\x7F]".toRegex(), "")

        // erases all the ASCII control characters
        text = text.replace("[\\p{Cntrl}&&[^\r\n\t]]".toRegex(), "")

        // removes non-printable characters from Unicode
        text = text.replace("\\p{C}".toRegex(), "")

        return@withContext text.trim { it <= ' ' }
    }

    suspend fun syncManga(manga: Manga, context: Context): Manga = withContext(Dispatchers.IO) {
        if (manga.synchronized) // if the manga exist in the server, return
            return@withContext manga

        // check if the manga is alredy there
        val mangaRepo = MangaRepository.invoke(context)
        val mangaList = mangaRepo.search(manga.title)

        // if it is really not in the server...
        if (mangaList.isEmpty()) {
            if (manga.author_id != null) { // if we have an author, we add the manga
                mangaRepo.update(manga)
                return@withContext manga
            } else { // if we dont have an author we will throw an exception
                throw IllegalArgumentException("There is no author in this manga")
                TODO("we may be able to upload chapters w/o author")
            }
        } else {
            return@withContext mangaList[0]
        }
    }
}