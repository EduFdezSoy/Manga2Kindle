package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.viewObject.UploadedChapter
import es.edufdezsoy.manga2kindle.ui.adapter.UploadedChapterAdapter
import kotlinx.android.synthetic.main.view_uploaded_chapters.view.*

class UploadedChaptersView(val view: View, val controller: UploadedChaptersController) :
    UploadedChaptersContract.View {
    private lateinit var adapter: UploadedChapterAdapter

    init {
        view.rvUploadedChapters.layoutManager = LinearLayoutManager(controller.activity)
        view.tvViewTitle.text = view.context.getString(R.string.view_uploaded_chapters_title)

        controller.loadChapters()

        view.swipeRefresh.setOnRefreshListener {
            controller.reloadChapters()
        }
    }

    override fun setChapters(chapters: List<UploadedChapter>) {
        if (chapters.isNotEmpty()) {
            if (::adapter.isInitialized) {
                adapter.setData(chapters)
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
                    controller.hideChapter(
                        adapter.chapters.get(view.rvUploadedChapters.getChildAdapterPosition(it))
                    )

                    return@OnLongClickListener true
                })
                view.rvUploadedChapters.layoutManager = LinearLayoutManager(controller.context)
                view.rvUploadedChapters.itemAnimator = DefaultItemAnimator()
                view.rvUploadedChapters.adapter = adapter
                view.flBackground.visibility = View.GONE
            }
        } else {
            view.tvViewTitle.text = view.context.getString(R.string.uploaded_chapters_empty_list)
        }

        view.swipeRefresh.isRefreshing = false
    }
}