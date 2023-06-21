package es.edufdezsoy.manga2kindle.network.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import es.edufdezsoy.manga2kindle.data.model.Manga
import es.edufdezsoy.manga2kindle.network.networkModels.StandardResponse

class StandardResponseMoshiAdapter {
    @FromJson
    fun <T> arrayListFromJson(response: StandardResponse<T>): ArrayList<T> {
        val arrayList = ArrayList<T>()
        arrayList.addAll(response.items)
        return arrayList
    }

    @ToJson
    fun <T> arrayListToJson(arrayList: ArrayList<T>): StandardResponse<T> {
        return StandardResponse(items = arrayList.toArray() as Array<T>)
    }
}