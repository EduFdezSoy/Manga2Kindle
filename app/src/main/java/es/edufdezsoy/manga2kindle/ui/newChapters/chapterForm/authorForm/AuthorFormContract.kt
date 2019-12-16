package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.authorForm

import es.edufdezsoy.manga2kindle.data.model.Author

interface AuthorFormContract {
    interface Controller {
        fun findNames(str: String)
        fun findSurnames(str: String)
        fun saveAuthor(name: String, surname: String, nickname: String)
        fun cancelEdit()
    }

    interface View {
        fun setAuthor(author: Author)
        fun setNameList(names: List<String>)
        fun setSurnameList(surnames: List<String>)
        fun saveAuthor()
    }
}