package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kDatabase
import es.edufdezsoy.manga2kindle.data.model.Chapter
import es.edufdezsoy.manga2kindle.network.ApiService
import kotlinx.android.synthetic.main.item_chapter_uploaded.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UploadedChapterAdapter(var chapters: List<Chapter>) :
    RecyclerView.Adapter<UploadedChapterAdapter.ViewHolder>(), CoroutineScope {

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
                R.layout.item_chapter_uploaded, parent, false
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

            var authorText = ""
            if (!author?.surname.isNullOrEmpty() || !author?.name.isNullOrEmpty()) {
                if (!author?.surname.isNullOrEmpty())
                    authorText += author?.surname + " "
                if (!author?.name.isNullOrEmpty())
                    authorText += author?.name + " "
                if (!author?.nickname.isNullOrEmpty())
                    authorText += "(AKA " + author?.nickname + ")"
            } else {
                if (!author?.nickname.isNullOrEmpty())
                    authorText += author?.nickname
            }
            holder.author?.text = authorText
        }

        if (chapters[position].volume == null) {
            holder.volText?.visibility = View.GONE
            holder.vol?.visibility = View.GONE
            holder.spacer?.visibility = View.GONE
        } else {
            holder.volText?.visibility = View.VISIBLE
            holder.vol?.visibility = View.VISIBLE
            holder.spacer?.visibility = View.VISIBLE
            holder.vol?.text = chapters[position].volume!!.toString()
        }

        holder.ch?.text = chapters[position].chapter.toString()
        holder.title?.text = chapters[position].title

        launch {
            holder.lang.text = ""
            val lang = chapters[position].lang_id?.let {
                M2kDatabase(context).LanguageDao().getLanguage(it)
            }
            if (lang != null)
                holder.lang.text = lang.code
        }

        // get and set status
        launch {
            holder.status.text = ""
            holder.reason.text = ""

            // TODO: this must be saved in the database and checked in the db before the api call
            ApiService.apiService.getStatus(chapters[position].id!!).also {
                if (!it[0].error) {
                    if (!it[0].delivered) {
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorProcessing)
                        )
                        holder.status.text = "processing"
                    } else {
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess)
                        )
                        holder.status.text = "success"
                    }
                } else {
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorFailed))
                    holder.status.text = "failed"
                    holder.status.text = it[0].reason
                }
            }
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
        val vol = view.tvVol
        val volText = view.tvVolText
        val spacer = view.tvLineText
        val ch = view.tvCh
        val chText = view.tvChText
        val title = view.tvChTitle
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