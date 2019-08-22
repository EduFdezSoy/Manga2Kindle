package es.edufdezsoy.manga2kindle.ui.main

import android.view.View
import es.edufdezsoy.manga2kindle.data.model.Author
import kotlinx.android.synthetic.main.activity_main.view.*

class MainView(val view: View, val controller: MainController) {
    init {
        view.tv1.text = "currando..."

        controller.callServerHello()
        controller.callAuthorSearch()
    }

    fun drawServerHello(res: String) {
        view.tv1.text = res
    }

    fun drawAuthorData(res: Author) {
        view.tv2.text = res.name
        view.tv3.text = res.surname
        view.tv4.text = res.nickname
    }
}
