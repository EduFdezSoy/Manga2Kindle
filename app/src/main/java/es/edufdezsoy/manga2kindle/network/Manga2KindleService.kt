package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.data.model.Status
import es.edufdezsoy.manga2kindle.data.model.Version
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface Manga2KindleService {

    companion object {
        const val API_URL = "https://manga2kindle.com/v2/"
    }

    @GET("/v2/")
    suspend fun getVersion(): Version

    //region Author

    @GET("author/{id}")
    suspend fun getAuthor(@Path("id") id: Int): Author

    @GET("author/search/{query}")
    suspend fun searchAuthor(@Path("query") query: String): Array<Author?>

    //endregion
    //region Manga

    @GET("manga/{id}")
    suspend fun getManga(@Path("id") id: Int): Manga

    @GET("manga/search/{query}")
    suspend fun searchManga(@Path("query") query: String): Array<Manga?>

    @PUT("manga")
    suspend fun putManga(manga: Manga): Manga

    //endregion
    //region Status

    @GET("status/{id}")
    suspend fun getStatus(@Path("id") id: Int): Status

    @PUT("status/register")
    suspend fun getNewStatus(): Status

    //endregion
}