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

class ChapterAdapter : ListAdapter<ChapterWithManga, ChapterAdapter.NoteHolder>(DIFF_CALLBACK) {
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChapterWithManga>() {
            override fun areItemsTheSame(oldItem: ChapterWithManga, newItem: ChapterWithManga): Boolean {
                return oldItem.chapter.id == newItem.chapter.id
            }

            override fun areContentsTheSame(oldItem: ChapterWithManga, newItem: ChapterWithManga): Boolean {
                return oldItem.chapter == newItem.chapter &&
                        oldItem.chapter.mangaId == newItem.chapter.mangaId &&
                        oldItem.chapter.path == newItem.chapter.path &&
                        oldItem.chapter.remoteId == newItem.chapter.remoteId &&
                        oldItem.chapter.volume == newItem.chapter.volume
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

//        holder.manga.text = chapter.manga_title
//        holder.chapterTitle.text = ": " + chapter.title
//        holder.chapter.text = "Ch." + chapter.chapter.toString()
//        holder.chapter2.text = chapter.chapter.toString()
//        if (chapter.volume != null)
//            holder.volume.text = "(Vol. " + chapter.volume + ")"
//        else
//            holder.volume.text = ""
    }

    override fun submitList(list: List<ChapterWithManga>?) {
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
        fun onItemClick(chapter: ChapterWithManga)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(chapter: ChapterWithManga)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }
}