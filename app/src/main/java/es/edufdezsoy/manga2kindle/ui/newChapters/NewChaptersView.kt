package es.edufdezsoy.manga2kindle.ui.newChapters

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.ui.adapter.NewChapterAdapter
import kotlinx.android.synthetic.main.view_new_chapters.view.*

class NewChaptersView(val view: View, val controller: NewChaptersController) :
    NewChaptersContract.View {
    init {
        view.rvNewChapters.layoutManager = LinearLayoutManager(controller.activity)
        view.tvViewTitle.text = "loading list..."

        controller.loadChapters()
    }

    override fun setChapters(chapters: List<Chapter>) {
        val adapter = NewChapterAdapter(chapters)
        adapter.setOnClickListener(View.OnClickListener { v ->
            Toast.makeText(
                view.context,
                "yeah, it works",
                Toast.LENGTH_LONG
            ).show()
        })

        view.rvNewChapters.adapter = adapter
        view.flBackground.visibility = View.GONE
    }
}