package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.*
import es.edufdezsoy.manga2kindle.network.networkModels.StandardResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface Manga2KindleService {

    companion object {
        const val API_URL = "https://new.manga2kindle.com/api/"
    }

    //region Manga

    @GET("/api/collections/mangas/records/{id}")
    suspend fun getManga(@Path("id") id: String): StandardResponse<Manga>

    @GET("/api/collections/mangas/records")
    suspend fun searchManga(@Query("filter") title: String): StandardResponse<Manga>

    @POST("/api/collections/mangas/records")
    suspend fun putManga(@Body manga: Manga): Manga

    //endregion
    //region Status

    @GET("/api/collections/chapters/records/{id}")
    suspend fun getChapterStatus(@Path("id") id: String): UploadChapter

    @DELETE("/api/collections/chapters/records/{id}")
    suspend fun deleteChapterStatus(@Path("id") id: String): Response<Unit> // FIXME: this does not handle errors but an empty body will fail otherwise (https://github.com/square/retrofit/issues/2867)

    @POST("/api/collections/chapters/records")
    suspend fun putChapter(@Body chapter: UploadChapter): UploadChapter

    @Multipart
    @PATCH("/api/collections/pages/records/{id}")
    suspend fun putChapterFile(
        @Path("id") chapterId: String,
        @Part() file: MultipartBody.Part
    ): UploadChapter

    //endregion
}