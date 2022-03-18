package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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

    @GET("chapter/{id}")
    suspend fun getChapterStatus(@Path("id") id: Int): Status

    @DELETE("chapter/{id}")
    suspend fun deleteChapterStatus(@Path("id") id: Int): Response<Unit> // FIXME: this does not handle errors but an empty body will fail otherwise (https://github.com/square/retrofit/issues/2867)

    // @Multipart
    @PUT("chapter")
    suspend fun getNewChapterStatus(@Body chapter: UploadChapter): Status

    @Multipart
    @POST("chapter/{id}/{page}")
    suspend fun putChapterPage(
        @Path("id") id: Int,
        @Path("page") page: Int,
        @Part() image: MultipartBody.Part
    )

    //endregion
}