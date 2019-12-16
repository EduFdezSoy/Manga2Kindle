package es.edufdezsoy.manga2kindle.network

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressRequestBody(private val file: File, private val listener: UploadCallbacks) :
    RequestBody() {

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish()
    }

    override fun contentType(): MediaType? {
        return MediaType.parse("zip") // TODO: this may not be hardcoded
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fin = FileInputStream(file)
        var uploaded = 0L

        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())

            do {
                read = fin.read(buffer)
                if (read != -1) {
                    handler.post(ProgressUpdater(uploaded, fileLength))

                    uploaded += read
                    sink.write(buffer, 0, read)
                }
            } while (read != -1)
        } finally {
            fin.close()
        }
    }

    private inner class ProgressUpdater(private val uploaded: Long, private val total: Long) :
        Runnable {
        override fun run() {
            listener.onProgressUpdate((100 * uploaded / total).toInt())
        }
    }
}