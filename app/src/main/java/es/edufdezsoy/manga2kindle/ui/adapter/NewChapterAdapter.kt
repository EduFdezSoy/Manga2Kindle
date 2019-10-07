package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import kotlinx.android.synthetic.main.item_chapter.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewChapterAdapter(var chapters: List<Chapter>) :
    RecyclerView.Adapter<NewChapterAdapter.ViewHolder>(), CoroutineScope {

    private lateinit var context: Context
    private var onClickListener: View.OnClickListener? = null
    private var onLongClickListener: View.OnLongClickListener? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

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
            holder.manga?.text = ""
            holder.author?.text = ""

            val manga = M2kDatabase(context).MangaDao().getMangaById(chapters[position].manga_id)
            holder.manga?.text = manga.title

            val author = manga.author_id?.let { M2kDatabase(context).AuthorDao().getAuthor(it) }

            holder.author?.text = author.toString()
        }

        holder.chapter.text = chapters[position].toString()

        launch {
            holder.lang.text = ""
            val lang = chapters[position].lang_id?.let {
                M2kDatabase(context).LanguageDao().getLanguage(it)
            }
            if (lang != null)
                holder.lang.text = lang.code
        }

        if (onClickListener != null)
            holder.setOnClickListener(onClickListener!!)
        if (onLongClickListener != null)
            holder.setOnLongClickListener(onLongClickListener!!)
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    fun addAll(chapters: List<Chapter>) {
        this.chapters = chapters
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

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

        fun setOnLongClickListener(onLongClickListener: View.OnLongClickListener) {
            itemView.setOnLongClickListener(onLongClickListener)
        }
    }

}