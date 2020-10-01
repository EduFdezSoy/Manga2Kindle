package es.edufdezsoy.manga2kindle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Chapter

class ChapterAdapter : ListAdapter<Chapter, ChapterAdapter.NoteHolder>(DIFF_CALLBACK) {
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Chapter>() {
            override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
                return oldItem.author == newItem.author &&
                        oldItem.chapter == newItem.chapter &&
                        oldItem.manga_id == newItem.manga_id &&
                        oldItem.manga_title == newItem.manga_title &&
                        oldItem.path == newItem.path &&
                        oldItem.remote_id == newItem.remote_id &&
                        oldItem.volume == newItem.volume
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_new_chapter, parent, false)

        return NoteHolder(view)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val chapter = getItem(position)

        holder.manga.text = chapter.manga_title
        holder.chapterTitle.text = ": " + chapter.title
        holder.chapter.text = "Ch." + chapter.chapter.toString()
        holder.chapter2.text = chapter.chapter.toString()
        if (chapter.volume != null)
            holder.volume.text = "(Vol. " + chapter.volume + ")"
        else
            holder.volume.text = ""
    }

    override fun submitList(list: List<Chapter>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }

    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val manga: TextView = itemView.findViewById(R.id.title_textview)
        val chapterTitle: TextView = itemView.findViewById(R.id.chapter_name_textview)
        val chapter: TextView = itemView.findViewById(R.id.chapter_textview)
        val chapter2: TextView = itemView.findViewById(R.id.chapter_bg_textview)
        val volume: TextView = itemView.findViewById(R.id.volume_textview)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(getItem(adapterPosition))
                    itemLongClickListener?.onItemLongClick(getItem(adapterPosition))
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(chapter: Chapter)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(chapter: Chapter)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }
}