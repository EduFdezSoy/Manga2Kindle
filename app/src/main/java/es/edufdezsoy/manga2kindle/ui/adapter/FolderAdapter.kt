package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.Folder
import kotlinx.android.synthetic.main.item_folder.view.*

class FolderAdapter(val folders: List<Folder>, val context: Context) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name?.text = folders[position].name
        holder.path?.text = folders[position].path
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.tvFolderName
        val path = view.tvFolderPath
    }
}