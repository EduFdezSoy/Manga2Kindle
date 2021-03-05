package es.edufdezsoy.manga2kindle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga

class ChapterAdapter : ListAdapter<ChapterWithManga, ChapterAdapter.ChapterHolder>(DIFF_CALLBACK) {
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChapterWithManga>() {
            override fun areItemsTheSame(oldItem: ChapterWithManga, newItem: ChapterWithManga): Boolean {
                return oldItem.chapter.id == newItem.chapter.id
            }

            override fun areContentsTheSame(oldItem: ChapterWithManga, newItem: ChapterWithManga): Boolean {
                return oldItem.chapter == newItem.chapter &&
                        oldItem.chapter.chapter == newItem.chapter.chapter &&
                        oldItem.chapter.mangaId == newItem.chapter.mangaId &&
                        oldItem.chapter.path == newItem.chapter.path &&
                        oldItem.chapter.remoteId == newItem.chapter.remoteId &&
                        oldItem.chapter.volume == newItem.chapter.volume
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_new_chapter, parent, false)

        return ChapterHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterHolder, position: Int) {
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

    override fun submitList(list: List<ChapterWithManga>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }

    inner class ChapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    interface OnItemClickListener {
        fun onItemClick(chapter: ChapterWithManga)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(chapter: ChapterWithManga): Boolean
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }
}