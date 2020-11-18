package es.edufdezsoy.manga2kindle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Folder

class FolderAdapter : ListAdapter<Folder, FolderAdapter.FolderHolder>(DIFF_CALLBACK) {
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
                return oldItem.folderId == newItem.folderId
            }

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
                return oldItem == newItem &&
                        oldItem.folderId == newItem.folderId &&
                        oldItem.name == newItem.name &&
                        oldItem.path == newItem.path &&
                        oldItem.active == newItem.active
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)

        return FolderHolder(view)
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val folder = getItem(position)

        holder.name.text = folder.name
        holder.logo.text = folder.name
        holder.path.text = folder.path
        holder.active.isChecked = folder.active
    }

    override fun submitList(list: List<Folder>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }

    inner class FolderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_textview)
        val path: TextView = itemView.findViewById(R.id.path_textview)
        val active: SwitchCompat = itemView.findViewById(R.id.active_switch)
        val logo: TextView = itemView.findViewById(R.id.logo_bg_textview)

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
        fun onItemClick(chapter: Folder)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(chapter: Folder)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }
}