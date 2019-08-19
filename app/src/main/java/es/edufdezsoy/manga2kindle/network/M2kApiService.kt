package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.data.model.Language
import es.edufdezsoy.manga2kindle.data.model.Manga
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface M2kApiService {
    @GET("/")
    fun serverHello(): Call<String>

    //#region /author

    @GET("/author")
    fun getAllAuthors(
        @Query("limit") limit: Int?
    ): Call<List<Author>>

    @GET("/author")
    fun getAuthor(
        @Query("id") author_id: Int
    ): Call<List<Author>>

    @GET("/author")
    fun searchAuthor(
        @Query("search") search: String
    ): Call<List<Author>>

    @PUT("/author")
    fun addAuthor(
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("nickname") nickname: String?
    ): Call<List<Author>>

    //#endregion

    //#region /languages

    @GET("/languages")
    fun getAllLanguages(): Call<List<Language>>

    //#endregion

    //#region /status

    @GET("/status")
    fun getStatus(
        @Query("chapter_id") chapter_id: Int
    ): Call<List<Chapter>>

    //#endregion

    //#region /manga
    @GET("/manga")
    fun getAllMangas(
        @Query("limit") limit: Int?
    ): Call<List<Manga>>

    @GET("/manga")
    fun searchManga(
        @Query("search") search: String
    ): Call<List<Manga>>

    @PUT("/manga")
    fun addManga(
        @Query("title") title: String,
        @Query("author_id") author_id: Int
    ): Call<List<Manga>>

    //#endregion

    //#region /manga/chapter

    @Multipart
    @POST("/manga/chapter")
    fun sendChapter(
        @Part("manga_id") manga_id: Int,
        @Part("lang_id") lang_id: Int,
        @Part("title") title: String,
        @Part("chapter") chapter: Float,
        @Part("volume") volume: Int?,
        @Part("checksum") checksum: String,
        @Part("mail") mail: String,
        // @Part("file") file: File
        @Part file: MultipartBody.Part
    ): Call<List<Chapter>>

    //#endregion
}