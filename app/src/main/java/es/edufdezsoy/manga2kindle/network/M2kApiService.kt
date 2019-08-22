package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Language
import es.edufdezsoy.manga2kindle.data.model.Manga
import okhttp3.MultipartBody
import retrofit2.http.*

interface M2kApiService {
    @GET("/")
    suspend fun serverHello(): String

    //#region /author

    @GET("/author")
    suspend fun getAllAuthors(
        @Query("limit") limit: Int?
    ): List<Author>

    @GET("/author")
    suspend fun getAuthor(
        @Query("id") author_id: Int
    ): List<Author>

    @GET("/author")
    suspend fun searchAuthor(
        @Query("search") search: String
    ): List<Author>

    @PUT("/author")
    suspend fun addAuthor(
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("nickname") nickname: String?
    ): List<Author>

    //#endregion

    //#region /languages

    @GET("/languages")
    suspend fun getAllLanguages(): List<Language>

    //#endregion

    //#region /status

    @GET("/status")
    suspend fun getStatus(
        @Query("chapter_id") chapter_id: Int
    ): List<Chapter>

    //#endregion

    //#region /manga
    @GET("/manga")
    suspend fun getAllMangas(
        @Query("limit") limit: Int?
    ): List<Manga>

    @GET("/manga")
    suspend fun searchManga(
        @Query("search") search: String
    ): List<Manga>

    @PUT("/manga")
    suspend fun addManga(
        @Query("title") title: String,
        @Query("author_id") author_id: Int
    ): List<Manga>

    //#endregion

    //#region /manga/chapter

    @Multipart
    @POST("/manga/chapter")
    suspend fun sendChapter(
        @Part("manga_id") manga_id: Int,
        @Part("lang_id") lang_id: Int,
        @Part("title") title: String,
        @Part("chapter") chapter: Float,
        @Part("volume") volume: Int?,
        @Part("checksum") checksum: String,
        @Part("mail") mail: String,
        // @Part("file") file: File
        @Part file: MultipartBody.Part
    ): List<Chapter>

    //#endregion
}