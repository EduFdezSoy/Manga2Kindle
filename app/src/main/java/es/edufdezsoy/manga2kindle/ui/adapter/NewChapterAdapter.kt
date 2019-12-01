package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.model.viewObject.NewChapter
import es.edufdezsoy.manga2kindle.data.model.viewObject.NewChapterDiffCallback
import kotlinx.android.synthetic.main.item_chapter.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewChapterAdapter(var chapters: ArrayList<NewChapter>) :
    RecyclerView.Adapter<NewChapterAdapter.ViewHolder>(), CoroutineScope {

    interface OnClickListener {
        fun onItemClicked(chapter: NewChapter)
    }

    interface OnLongClickListener {
        fun onItemLongClicked(chapter: NewChapter)
    }

    private lateinit var context: Context
    private var onClickListener: OnClickListener? = null
    private var onLongClickListener: OnLongClickListener? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var lastClickTime: Long = 0

    constructor(chapters: List<NewChapter>) : this(chapters as ArrayList<NewChapter>)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_chapter, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        launch {
            val chapter = chapters[position]
            setBackgroundColor(holder, position)
            holder.lang.text = ""

            holder.manga.text = chapter.manga_title
            holder.chapter.text = chapter.chapter
            holder.author.text = chapter.author

            if (onClickListener != null)
                holder.setOnClickListener(View.OnClickListener {
                    // these three lines prevent mis-clicking
                    if (SystemClock.elapsedRealtime() - lastClickTime < 1000)
                        return@OnClickListener
                    lastClickTime = SystemClock.elapsedRealtime()

                    onClickListener!!.onItemClicked(chapter)
                })
            if (onLongClickListener != null)
                holder.setOnLongClickListener(View.OnLongClickListener {
                    onLongClickListener!!.onItemLongClicked(chapter)
                    return@OnLongClickListener true
                })
        }
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    private fun setBackgroundColor(holder: ViewHolder, position: Int) {
        if (position % 2 == 1)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.listBG_1))
        else
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.listBG_2))
    }

    fun setData(chapters: List<NewChapter>) {
        val diffCallback = NewChapterDiffCallback(this.chapters, chapters)
        val diffRes = DiffUtil.calculateDiff(diffCallback)
        this.chapters.clear()
        this.chapters.addAll(chapters)
        diffRes.dispatchUpdatesTo(this)
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    fun setOnLongClickListener(listener: OnLongClickListener) {
        onLongClickListener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val manga = view.tvTitle
        val chapter = view.tvChapter
        val author = view.tvAuthor
        val lang = view.tvLang

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

        fun setOnLongClickListener(onLongClickListener: View.OnLongClickListener) {
            itemView.setOnLongClickListener(onLongClickListener)
        }
    }
}