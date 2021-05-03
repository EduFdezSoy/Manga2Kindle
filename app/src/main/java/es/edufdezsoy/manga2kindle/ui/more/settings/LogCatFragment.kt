package es.edufdezsoy.manga2kindle.ui.more.settings

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.utils.LogRegistry
import kotlinx.android.synthetic.main.fragment_log_cat.view.*
import kotlinx.coroutines.launch

class LogCatFragment : Fragment() {
    private val handler = Handler()
    private val delay: Long = 500; // 1000 milliseconds == 1 second

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log_cat, container, false)
        view.logcat_textView.movementMethod = ScrollingMovementMethod()

        lifecycleScope.launch {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    var text = ""
                    LogRegistry.getInstance(null).log.forEach {
                        text += when (it[0]) {
                            'V' -> "<font color='#a1a7ab'>$it</font>"
                            'D' -> "<font color='#2ecc71'>$it</font>"
                            'I' -> "<font color='#3498db'>$it</font>"
                            'W' -> "<font color='#f39c12'>$it</font>"
                            'E' -> "<font color='#e74c3c'>$it</font>"
                            else -> it
                        }
                        text += "<br>"
                    }

                    view?.logcat_textView?.text = Html.fromHtml(text)
                    handler.postDelayed(this, delay)
                }
            }, delay)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}