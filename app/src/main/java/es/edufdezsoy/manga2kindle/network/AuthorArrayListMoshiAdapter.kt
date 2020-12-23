package es.edufdezsoy.manga2kindle.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import es.edufdezsoy.manga2kindle.data.model.Author

class AuthorArrayListMoshiAdapter {
    @ToJson
    fun arrayListToJson(list: ArrayList<Author>): List<Author> = list

    @FromJson
    fun arrayListFromJson(list: List<Author>): ArrayList<Author> = list as ArrayList<Author>
}