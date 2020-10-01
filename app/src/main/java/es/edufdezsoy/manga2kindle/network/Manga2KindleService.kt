package es.edufdezsoy.manga2kindle.network

import retrofit2.http.GET

interface Manga2KindleService {

    companion object {
        const val API_URL = "https://manga2kindle.com/api/v2/"
    }

    @GET("/api/")
    suspend fun getHello(): String

    @GET("hello")
    suspend fun getVersion(): Object
}