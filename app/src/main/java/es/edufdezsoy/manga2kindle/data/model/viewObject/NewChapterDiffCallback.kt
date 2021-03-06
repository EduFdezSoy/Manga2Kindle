package es.edufdezsoy.manga2kindle.data.model.viewObject

import androidx.recyclerview.widget.DiffUtil

class NewChapterDiffCallback(val old: List<NewChapter>, val new: List<NewChapter>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].local_id == new[newItemPosition].local_id
    }

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }
}