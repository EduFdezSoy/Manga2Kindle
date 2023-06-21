package es.edufdezsoy.manga2kindle.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga

abstract class ChapterBaseAdapter :
    ListAdapter<ChapterWithManga, ChapterBaseAdapter.ChapterHolder>(DIFF_CALLBACK) {
    protected abstract var itemClickListener: OnItemClickListener?
    protected abstract var itemLongClickListener: OnItemLongClickListener?

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder
    abstract override fun onBindViewHolder(holder: ChapterHolder, position: Int)

    interface OnItemClickListener {
        fun onItemClick(chapter: ChapterWithManga)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(chapter: ChapterWithManga): Boolean
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }

    override fun submitList(list: List<ChapterWithManga>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChapterWithManga>() {
            override fun areItemsTheSame(
                oldItem: ChapterWithManga,
                newItem: ChapterWithManga
            ): Boolean {
                return oldItem.chapter.rowid == newItem.chapter.rowid
            }

            override fun areContentsTheSame(
                oldItem: ChapterWithManga,
                newItem: ChapterWithManga
            ): Boolean {
                return oldItem.chapter == newItem.chapter &&
                        oldItem.chapter.id == newItem.chapter.id &&
                        oldItem.chapter.status == newItem.chapter.status &&
                        oldItem.chapter.chapter == newItem.chapter.chapter &&
                        oldItem.chapter.mangaId == newItem.chapter.mangaId &&
                        oldItem.chapter.path == newItem.chapter.path &&
                        oldItem.chapter.volume == newItem.chapter.volume
            }
        }
    }

    abstract inner class ChapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}