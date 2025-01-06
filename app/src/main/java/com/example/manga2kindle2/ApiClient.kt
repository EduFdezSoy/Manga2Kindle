package com.example.manga2kindle2

import android.util.Log
import com.example.manga2kindle2.models.Chapter
import io.github.agrevster.pocketbaseKotlin.FileUpload
import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.ktor.http.URLProtocol
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

class ApiClient {
    private val client = PocketbaseClient({
        protocol = URLProtocol.HTTPS
        host = "new.manga2kindle.com" // TODO: extract to a config file maybe?
    })

    suspend fun createChapter(chapter: Chapter): Chapter {
        // checks the chapter data and file
        if (!chapterChecks(chapter)) {
            return chapter
        }

        val chapterByteArray = chapter.getFileToByteArray()
        if (chapterByteArray.isEmpty()) {
            Log.e("M2K", "chapterByteArray is empty!")
            return chapter
        }

        // create/upload a chapter
        val response = client.records.create<Chapter>(
            "chapters",
            mapOf<String, JsonPrimitive>(
                "manga" to Json.encodeToJsonElement(chapter.manga).jsonPrimitive,
                "title" to Json.encodeToJsonElement(chapter.title).jsonPrimitive,
                "chapter" to Json.encodeToJsonElement(chapter.chapter).jsonPrimitive,
                "volume" to Json.encodeToJsonElement(chapter.volume).jsonPrimitive,
                "email" to Json.encodeToJsonElement(chapter.email).jsonPrimitive,
            ),
            listOf(
                FileUpload("file", chapterByteArray, "file.cbz") // TODO: set file name - FIXED: doesn't matter, server works fine with any name and its not relevant
            )
        )

        // print response id
        Log.i("M2K", "id: ${response.collectionId}")

        return response
    }

    private fun chapterChecks(chapter: Chapter): Boolean {
        if (chapter.manga == null) {
            Log.e("M2K", "Manga is required")
            return false
        }

        if (chapter.email == null) {
            Log.e("M2K", "Email is required")
            return false
        }

        if (chapter.getFile() == null) {
            Log.e("M2K", "File is required")
            return false
        }

        return true
    }
}
