package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.data.model.viewObject.UploadedChapter
import es.edufdezsoy.manga2kindle.ui.adapter.UploadedChapterAdapter
import kotlinx.android.synthetic.main.view_uploaded_chapters.view.*

class UploadedChaptersView(val view: View, val controller: UploadedChaptersController) :
    UploadedChaptersContract.View {
    private lateinit var adapter: UploadedChapterAdapter

    init {
        view.rvUploadedChapters.layoutManager = LinearLayoutManager(controller.activity)
        view.tvViewTitle.text = "loading list..."

        controller.loadChapters()

        view.swipeRefresh.setOnRefreshListener {
            controller.reloadChapters()
        }
    }

    override fun setChapters(chapters: List<UploadedChapter>) {
        if (chapters.isNotEmpty()) {
            if (::adapter.isInitialized) {
                adapter.addAll(chapters)
            } else {
                adapter = UploadedChapterAdapter(chapters)
                adapter.setOnClickListener(View.OnClickListener { v ->
                    controller.openChapterDetails(
                        adapter.chapters.get(
                            view.rvUploadedChapters.getChildAdapterPosition(v)
                        )
                    )
                })

                adapter.setOnLongClickListener(View.OnLongClickListener {
                    Toast.makeText(
                        view.context,
                        "TODO: hide entry",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@OnLongClickListener false
                })
                view.rvUploadedChapters.adapter = adapter
                view.flBackground.visibility = View.GONE
            }
        } else {
            view.tvViewTitle.text = "Looks like this list is empty!"
        }

        view.swipeRefresh.isRefreshing = false
    }
}