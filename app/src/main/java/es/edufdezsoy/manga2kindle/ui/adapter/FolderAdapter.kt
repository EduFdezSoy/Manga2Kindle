package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Folder
import kotlinx.android.synthetic.main.item_folder.view.*

class FolderAdapter(var folders: List<Folder>) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    private var onClickListener: View.OnClickListener? = null
    private var onLongClickListener: View.OnLongClickListener? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_folder, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setBackgroundColor(holder, position)

        holder.name?.text = folders[position].name
        holder.path?.text = Uri.parse(folders[position].path).path

        if (onClickListener != null)
            holder.setOnClickListener(onClickListener!!)
        if (onLongClickListener != null)
            holder.setOnLongClickListener(onLongClickListener!!)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    private fun setBackgroundColor(holder: ViewHolder, position: Int) {
        if (position % 2 == 1)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.listBG_1))
        else
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.listBG_2))
    }

    fun addAll(folders: List<Folder>) {
        this.folders = folders
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        onClickListener = listener
    }

    fun setOnLongClickListener(listener: View.OnLongClickListener) {
        onLongClickListener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.tvFolderName
        val path = view.tvFolderPath

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

        fun setOnLongClickListener(onLongClickListener: View.OnLongClickListener) {
            itemView.setOnLongClickListener(onLongClickListener)
        }
    }
}