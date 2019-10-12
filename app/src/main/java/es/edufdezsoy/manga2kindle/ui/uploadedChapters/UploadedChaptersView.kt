package es.edufdezsoy.manga2kindle.ui.uploadedChapters

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.ui.adapter.UploadedChapterAdapter
import kotlinx.android.synthetic.main.view_new_chapters.view.*
import kotlinx.android.synthetic.main.view_uploaded_chapters.view.*
import kotlinx.android.synthetic.main.view_uploaded_chapters.view.flBackground
import kotlinx.android.synthetic.main.view_uploaded_chapters.view.tvViewTitle

class UploadedChaptersView(val view: View, val controller: UploadedChaptersController) :
    UploadedChaptersContract.View {
    init {
        view.rvUploadedChapters.layoutManager = LinearLayoutManager(controller.activity)
        view.tvViewTitle.text = view.context.getString(R.string.view_uploaded_chapters_title)

        controller.loadChapters()
    }

    override fun setChapters(chapters: List<Chapter>) {
        if (!chapters.isEmpty()) {
            val adapter = UploadedChapterAdapter(chapters)
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

        } else {
            view.tvViewTitle.text = view.context.getString(R.string.uploaded_chapters_empty_list)
        }
    }
}