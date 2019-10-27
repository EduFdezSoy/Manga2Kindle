package es.edufdezsoy.manga2kindle.ui.hiddenChapters

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.viewObject.HiddenChapter
import es.edufdezsoy.manga2kindle.ui.adapter.HiddenChapterAdapter
import kotlinx.android.synthetic.main.view_hidden_chapters.view.*

class HiddenChaptersView(val view: View, val controller: HiddenChaptersContract.Controller) :
    HiddenChaptersContract.View {
    private lateinit var adapter: HiddenChapterAdapter

    init {
        controller.loadChapters()
    }

    override fun setChapters(chapters: List<HiddenChapter>) {
        if (chapters.isNotEmpty()) {
            if (::adapter.isInitialized) {
                adapter.setData(chapters)
            } else {
                adapter = HiddenChapterAdapter(chapters)
                adapter.setOnClickListener(View.OnClickListener {
                    controller.openChapterDetails(
                        adapter.chapters.get(view.rvHiddenChapters.getChildAdapterPosition(it))
                    )
                })

                adapter.setOnLongClickListener(View.OnLongClickListener {
                    controller.showChapter(
                        adapter.chapters.get(view.rvHiddenChapters.getChildAdapterPosition(it))
                    )

                    return@OnLongClickListener false
                })
                view.rvHiddenChapters.layoutManager = LinearLayoutManager(view.context)
                view.rvHiddenChapters.itemAnimator = DefaultItemAnimator()
                view.rvHiddenChapters.adapter = adapter
                view.flBackground.visibility = View.GONE
            }
        } else {
            view.flBackground.visibility = View.VISIBLE
            view.tvViewTitle.text = view.context.getString(R.string.uploaded_chapters_empty_list)
        }
    }
}