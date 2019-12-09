package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.viewObject.UploadedChapter
import es.edufdezsoy.manga2kindle.data.model.viewObject.UploadedChapterDiffCallback
import kotlinx.android.synthetic.main.item_chapter_uploaded.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UploadedChapterAdapter(var chapters: ArrayList<UploadedChapter>) :
    RecyclerView.Adapter<UploadedChapterAdapter.ViewHolder>(), CoroutineScope {

    private lateinit var context: Context
    private var onClickListener: View.OnClickListener? = null
    private var onLongClickListener: View.OnLongClickListener? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    constructor(chapters: List<UploadedChapter>) : this(chapters as ArrayList<UploadedChapter>)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val view =
            LayoutInflater.from(context).inflate(R.layout.item_chapter_uploaded, parent, false)
        val viewholder = ViewHolder(view)

        return viewholder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        launch {
            AdapterUtils.setBackgroundColor(holder, position, context)

            holder.manga.text = chapters[position].manga_title
            holder.chapter.text = chapters[position].chapter

            if (chapters[position].author.isNotBlank())
                holder.author.text = chapters[position].author
            else
                holder.author.text = AdapterUtils.randAuthorFace(context)

            holder.status.setTextColor(
                ContextCompat.getColor(
                    context,
                    chapters[position].status_color
                )
            )
            holder.status.text = chapters[position].status
            holder.reason.text = chapters[position].reason

            holder.lang.text = ""

            if (onClickListener != null)
                holder.setOnClickListener(onClickListener!!)
            if (onLongClickListener != null)
                holder.setOnLongClickListener(onLongClickListener!!)
        }
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    fun setData(chapters: List<UploadedChapter>) {
        val diffCallback = UploadedChapterDiffCallback(this.chapters, chapters)
        val diffRes = DiffUtil.calculateDiff(diffCallback)
        this.chapters.clear()
        this.chapters.addAll(chapters)
        diffRes.dispatchUpdatesTo(this)
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        onClickListener = listener
    }

    fun setOnLongClickListener(listener: View.OnLongClickListener) {
        onLongClickListener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val manga = view.tvTitle
        val chapter = view.tvChapter
        val author = view.tvAuthor
        val lang = view.tvLang
        val status = view.tvStatus
        val reason = view.tvReason

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

        fun setOnLongClickListener(onLongClickListener: View.OnLongClickListener) {
            itemView.setOnLongClickListener(onLongClickListener)
        }
    }
}