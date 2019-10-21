package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.authorForm

import android.view.View
import es.edufdezsoy.manga2kindle.data.model.Author
import kotlinx.android.synthetic.main.view_author_form.view.*

class AuthorFormView(val view: View, val controller: AuthorFormContract.Controller) :
    AuthorFormContract.View {

    override fun setAuthor(author: Author) {
        view.tietName.setText(author.name)
        view.tietSurname.setText(author.surname)
        view.tietNickname.setText(author.nickname)
    }

    override fun saveAuthor() {
        controller.saveAuthor(
            name = view.tietName.text.toString(),
            surname = view.tietSurname.text.toString(),
            nickname = view.tietNickname.text.toString()
        )
    }

    override fun setNameList(names: List<String>) {
        TODO("not implemented")
    }

    override fun setSurnameList(surnames: List<String>) {
        TODO("not implemented")
    }
}