package es.edufdezsoy.manga2kindle.ui.observedFolders

import es.edufdezsoy.manga2kindle.data.model.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ObservedFoldersInteractor(val controller: Controller) {
    interface Controller {
        fun setFolders(folders: List<Folder>)
    }

    suspend fun loadMockFolders() {
        var folders = listOf<Folder>()
        withContext(Dispatchers.Default) {
            Thread.sleep(2_000)
            folders = listOf(
                Folder(1, "Test 1", "/path/test/NumberOne"),
                Folder(2, "Test 2", "/path/test/SecondFolder"),
                Folder(3, "Test 3", "/path/test/TheNextOne"),
                Folder(4, "Test 4", "/path/test/NotTheFifth")
            )
        }.also {
            controller.setFolders(folders)
        }
    }
}