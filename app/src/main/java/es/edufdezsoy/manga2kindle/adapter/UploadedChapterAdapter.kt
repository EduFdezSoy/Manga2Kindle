package es.edufdezsoy.manga2kindle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Status

class UploadedChapterAdapter : ChapterBaseAdapter() {
    override var itemClickListener: OnItemClickListener? = null
    override var itemLongClickListener: OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder {
        return ChapterHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_uploaded_chapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChapterBaseAdapter.ChapterHolder, position: Int) {
        if (holder is ChapterHolder) {
            val chapter = getItem(position)

            // these two lines allow the text to move
            holder.manga.isSelected = true
            holder.chapterTitle.isSelected = true

            holder.manga.text = chapter.manga.manga.title
            holder.chapterTitle.text = chapter.chapter.title
            holder.chapter.text = "Ch." + chapter.chapter.chapterToString()
            holder.chapter2.text = chapter.chapter.chapterToString().replace(".", "-")

            if (chapter.chapter.volume != null)
                holder.volume.text = "(Vol. " + chapter.chapter.volume + ")"
            else
                holder.volume.text = ""

            if (holder.chapterTitle.text.isNotBlank())
                holder.chapter.text = holder.chapter.text.toString() + ": "

            when (chapter.chapter.status) {
                Status.DONE -> holder.done()
                Status.ERROR -> holder.error()
                in Status.UPLOADED..Status.SENDING -> holder.processing()
                in Status.REGISTERED..Status.UPLOADING -> holder.uploading()
            }
        }
    }

    inner class ChapterHolder(itemView: View) : ChapterBaseAdapter.ChapterHolder(itemView) {
        val manga: TextView = itemView.findViewById(R.id.title_textview)
        val chapterTitle: TextView = itemView.findViewById(R.id.chapter_name_textview)
        val chapter: TextView = itemView.findViewById(R.id.chapter_textview)
        val chapter2: TextView = itemView.findViewById(R.id.chapter_bg_textview)
        val volume: TextView = itemView.findViewById(R.id.volume_textview)

        private val status_bar: ProgressBar = itemView.findViewById(R.id.chapter_progress)
        private val status_upload: ImageView = itemView.findViewById(R.id.chapter_progress_uploading)
        private val status_done: ImageView = itemView.findViewById(R.id.chapter_progress_done)
        private val status_error: ImageView = itemView.findViewById(R.id.chapter_progress_error)

        init {
            setAllIconsInvisible()

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(getItem(adapterPosition))
                }
            }
            itemView.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val returnValue =
                        itemLongClickListener?.onItemLongClick(getItem(adapterPosition))
                    if (returnValue != null) {
                        return@setOnLongClickListener returnValue
                    }
                }
                return@setOnLongClickListener false
            }
        }

        private fun setAllIconsInvisible() {
            status_bar.visibility = View.INVISIBLE
            status_upload.visibility = View.INVISIBLE
            status_done.visibility = View.INVISIBLE
            status_error.visibility = View.INVISIBLE
        }

        fun uploading() {
            setAllIconsInvisible()
            status_bar.visibility = View.VISIBLE
            status_upload.visibility = View.VISIBLE
        }

        fun processing() {
            setAllIconsInvisible()
            status_bar.visibility = View.VISIBLE
        }

        fun done() {
            setAllIconsInvisible()
            status_done.visibility = View.VISIBLE
        }

        fun error() {
            setAllIconsInvisible()
            status_error.visibility = View.VISIBLE
        }
    }
}