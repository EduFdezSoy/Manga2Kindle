package es.edufdezsoy.manga2kindle.ui.main

import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.network.ApiService

object MainInteractor {
    suspend fun callServer(): String? {
        val str = ApiService.apiService.serverHello()
        return str
    }

    suspend fun callAuthorSearch(): Author? {
        val res = ApiService.apiService.searchAuthor("a")
        return res[9]
    }
}