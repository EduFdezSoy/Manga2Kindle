package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.data.model.Status
import es.edufdezsoy.manga2kindle.data.model.Version
import retrofit2.http.GET
import retrofit2.http.PUT

interface Manga2KindleService {

    companion object {
        const val API_URL = "https://manga2kindle.com/v2/"
    }

    @GET("/v2/")
    suspend fun getVersion(): Version

    //region Author

    @GET("author/:id")
    suspend fun getAuthor(id: Int): Author

    @GET("author/search/:query")
    suspend fun searchAuthor(query: String): Array<Author?>

    //endregion
    //region Manga

    @GET("manga/:id")
    suspend fun getManga(id: Int): Manga

    @GET("manga/search/:query")
    suspend fun searchManga(query: String): Array<Manga?>

    @PUT("manga")
    suspend fun putManga(manga: Manga): Manga

    //endregion
    //region Status

    @GET("status/:id")
    suspend fun getStatus(id: Int): Status

    @PUT("status/register")
    suspend fun getNewStatus(): Status

    //endregion
}