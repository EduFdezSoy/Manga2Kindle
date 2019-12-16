package es.edufdezsoy.manga2kindle.ui.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.data.M2kSharedPref

object AdapterUtils {
    fun randAuthorFace(context: Context): String {
        if (M2kSharedPref.invoke(context).getBoolean("null_author_donger", false))
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