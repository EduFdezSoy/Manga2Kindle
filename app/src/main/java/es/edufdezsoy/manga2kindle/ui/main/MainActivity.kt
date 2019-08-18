package es.edufdezsoy.manga2kindle.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import es.edufdezsoy.manga2kindle.M2kApplication
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.Exception
import org.apache.commons.io.IOUtils
import java.io.FileOutputStream
import java.security.DigestInputStream
import java.security.MessageDigest


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv1.text = "currando..."


        // load the server hello message
        Thread(Runnable {
            try {
              val call = ApiService.apiService.serverHello()
              val res = call.execute().body()
              this@MainActivity.runOnUiThread { tv1.text = res!! }
            } catch (e: Exception) {
                Log.e(M2kApplication.TAG, "Exception", e)
            }
        }).start()

        // load an author
        Thread(Runnable {
            try {
                val call = ApiService.apiService.searchAuthor("a")
                val res = call.execute().body()?.get(9)
                this@MainActivity.runOnUiThread {
                    tv2.text = res!!.name
                    tv3.text = res.surname
                    tv4.text = res.nickname
                }
            } catch (e: Exception) {
                Log.e(M2kApplication.TAG, "Exception", e)
            }

        }).start()

        performFileSearch()
    }

    // upload file
    val READ_REQUEST_CODE = 42;

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    fun performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*")

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                val uri = resultData.getData()
                Log.i(M2kApplication.TAG, "uri: " + uri.toString())
                val inputStream = baseContext.contentResolver.openInputStream(uri!!)

                val tempFile = File.createTempFile("M2K", "temp.jpg")
                tempFile.deleteOnExit()
                val out = FileOutputStream(tempFile)
                IOUtils.copy(inputStream, out)

                val md = MessageDigest.getInstance("MD5")
                DigestInputStream(inputStream, md)
                val digest = md.digest()

                val hexString = StringBuilder()

                for (i in 0 until digest.size) {
                    val hex = Integer.toHexString(0xFF and digest[i].toInt())
                    if (hex.length == 1) {
                        hexString.append('0')
                    }
                    hexString.append(hex)
                }

                Log.e(M2kApplication.TAG, hexString.toString())
                uploadTestFile(tempFile, uri, hexString.toString())
            }
        }
    }

    private fun uploadTestFile(file: File, uri: Uri, md5: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(M2kApplication.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(M2kApiService::class.java)

        // load the server hello message
        Thread(Runnable {
            try {
                val reqFile = RequestBody.create(MediaType.parse(contentResolver.getType(uri)!!), file)
                val part = MultipartBody.Part.createFormData("file", file.name, reqFile)

                val call =
                    apiService.sendChapter(1, 11, "test chapter with photo", 0F, null, md5, "test@example.com", part)
                val res = call.execute().body()
                this@MainActivity.runOnUiThread { tv1.text = file.absolutePath }
            } catch (e: Exception) {
                Log.e(M2kApplication.TAG, "Exception", e)
            }
        }).start()
    }
}
