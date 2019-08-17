package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.data.model.Author
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface M2kApiService {
    @GET("/")
    fun serverHello(): Call<String>

    //#region /author

    @GET("/author")
    fun getAuthors(@Query("limit") limit: Int): Call<List<Author>>

    @GET("/author")
    fun searchAuthor(@Query("search") search: String): Call<List<Author>>

    @GET("/author")
    fun searchAuthor(@Query("search") search: String, @Query("limit") limit: Int): Call<List<Author>>

    //#endregion

}