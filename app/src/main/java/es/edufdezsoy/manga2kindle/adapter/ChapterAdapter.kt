package es.edufdezsoy.manga2kindle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R

class ChapterAdapter : ChapterBaseAdapter() {
    override var itemClickListener: OnItemClickListener? = null
    override var itemLongClickListener: OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder {
        return ChapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_new_chapter, parent, false)
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
        }
    }

    inner class ChapterHolder(itemView: View) : ChapterBaseAdapter.ChapterHolder(itemView) {
        val manga: TextView = itemView.findViewById(R.id.title_textview)
        val chapterTitle: TextView = itemView.findViewById(R.id.chapter_name_textview)
        val chapter: TextView = itemView.findViewById(R.id.chapter_textview)
        val chapter2: TextView = itemView.findViewById(R.id.chapter_bg_textview)
        val volume: TextView = itemView.findViewById(R.id.volume_textview)

        init {
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
    }
}