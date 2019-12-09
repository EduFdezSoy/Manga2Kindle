package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R

object AdapterUtils {
    fun randAuthorFace(context: Context): String {
        if (context.getSharedPreferences(
                "es.edufdezsoy.manga2kindle_preferences",
                Context.MODE_PRIVATE
            ).getBoolean("null_author_donger", false)
        )
            return arrayOf(
                "¯\\_(ツ)_/¯",
                "¯\\_(ヅ)_/¯",
                "¯\\_( ͡° ͜ʖ ͡°)_/¯",
                "¯\\_㋡_/¯",
                "¯\\(°_o)/¯"
            ).random()
        else
            return ""
    }

    fun setBackgroundColor(holder: RecyclerView.ViewHolder, position: Int, context: Context) {
        if (position % 2 == 1)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.listBG_1))
        else
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.listBG_2))
    }
}