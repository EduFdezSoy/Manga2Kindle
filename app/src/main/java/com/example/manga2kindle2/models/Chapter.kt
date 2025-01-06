package com.example.manga2kindle2.models

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import io.github.agrevster.pocketbaseKotlin.models.Record
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream

@Serializable
data class Chapter(
    var manga: String? = null,
    var title: String? = null,
    var chapter: Float? = null,
    var volume: Int? = null,
    var email: String? = null,
    var file: String? = null,
    var fileName: String? = null,
    @Contextual var contentResolver: ContentResolver? = null
) : Record() {
    fun getFile(): Uri? {
        return file?.let { Uri.parse(it) }
    }

    fun getFileToByteArray(): ByteArray {
        return try {
            val inputStream = contentResolver?.openInputStream(getFile()!!)
            val outputStream = ByteArrayOutputStream()
            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            Log.i("M2K", "File bytes:" + outputStream.size() + " bytes")
            outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e("M2K", "Error converting file to byte array: $e")
            ByteArray(0)
        }
    }
}
