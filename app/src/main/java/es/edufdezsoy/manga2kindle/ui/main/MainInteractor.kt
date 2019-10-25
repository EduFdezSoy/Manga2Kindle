package es.edufdezsoy.manga2kindle.ui.main

import es.edufdezsoy.manga2kindle.data.model.Author
import es.edufdezsoy.manga2kindle.network.ApiService

object MainInteractor {
    suspend fun callServer(): String? {
        val hello = ApiService.apiService.serverHello()
        return hello.name + " " + hello.version
    }

    suspend fun callAuthorSearch(): Author? {
        val res = ApiService.apiService.searchAuthor("a")
        return res[9]
    }
}