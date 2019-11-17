package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface M2kApiService {
    @GET("/hello")
    suspend fun serverHello(): Hello

    //#region /author

    /**
     * Get all the authors from the server, currently limited to 100
     *
     * @param limit how many authors we want to retrieve
     * @return this list can be empty but normally it's size will be equal to the limit set
     */
    @GET("/author")
    suspend fun getAllAuthors(
        @Query("limit") limit: Int?
    ): List<Author>

    /**
     * Get an author by its id
     *
     * @param author_id id from the requested author
     * @return this list can be empty but it must return only one author
     */
    @GET("/author")
    suspend fun getAuthor(
        @Query("id") author_id: Int
    ): List<Author>

    /**
     * Search for an author
     *
     * @param search the string we are searching for
     * @return this list can be empty, it return all authors with coincidences
     */
    @GET("/author")
    suspend fun searchAuthor(
        @Query("search") search: String
    ): List<Author>

    /**
     * Adds an author to the server database
     *
     * @param name the name of the author
     * @param surname the surname of the author
     * @param nickname the nickname of the author
     *
     * @return this list can be empty but it must return one item, the new author
     */
    @PUT("/author")
    suspend fun addAuthor(
        @Query("name") name: String?,
        @Query("surname") surname: String?,
        @Query("nickname") nickname: String?
    ): List<Author>

    //#endregion

    //#region /languages

    /**
     * Get all languages in the server
     *
     * @return this list can be empty but must have all languages in the server
     */
    @GET("/languages")
    suspend fun getAllLanguages(): List<Language>

    //#endregion

    //#region /status

    /**
     * Get a chapter's status
     *
     * @param chapter_id the chapter id we want to check
     * @return this list can be null but must have the chapter requested
     */
    @GET("/status")
    suspend fun getStatus(
        @Query("chapter_id") chapter_id: Int
    ): List<Chapter>

    //#endregion

    //#region /manga

    /**
     * Get all the mangas from the server, currently limited to 100
     *
     * @param limit how many mangas we want to retrieve
     * @return this list can be empty but normally it's size will be equal to the limit set
     */
    @GET("/manga")
    suspend fun getAllMangas(
        @Query("limit") limit: Int?
    ): List<Manga>

    /**
     * Search for a manga
     *
     * @param search the string we are searching for
     * @return this list can be empty, it return all mangas with coincidences
     */
    @GET("/manga")
    suspend fun searchManga(
        @Query("search") search: String
    ): List<Manga>

    /**
     * Add a manga to the server
     *
     * @param title title of the manga
     * @param author_id id from the author of this manga
     *
     * @return this list can be empty but it must return one item, the new manga
     */
    @PUT("/manga")
    suspend fun addManga(
        @Query("title") title: String,
        @Query("author_id") author_id: Int
    ): List<Manga>

    //#endregion

    //#region /manga/chapter

    /**
     * Uploads a new chapter to the server
     *
     * @param manga_id id from this chapter's manga
     * @param lang_id language of this chapter
     * @param title the chapter's title
     * @param chapter chapter's number
     * @param volume volume in witch this chapter is
     * @param checksum md5 of the zipped file
     * @param mail mail to be delivered
     * @param file the zip file that contains the chapter
     *
     * @return this list can be empty but it must return one item, the new chapter
     */
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