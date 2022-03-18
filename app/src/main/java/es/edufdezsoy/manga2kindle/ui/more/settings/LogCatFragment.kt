package es.edufdezsoy.manga2kindle.ui.more.settings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import es.edufdezsoy.manga2kindle.databinding.FragmentLogCatBinding
import es.edufdezsoy.manga2kindle.utils.LogRegistry
import kotlinx.coroutines.launch

class LogCatFragment : Fragment() {
    private val handler = Handler(Looper.getMainLooper())
    private val delay: Long = 500; // 1000 milliseconds == 1 second

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLogCatBinding.inflate(inflater, container, false)
        binding.logcatTextView.movementMethod = ScrollingMovementMethod()

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

                    binding.logcatTextView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    handler.postDelayed(this, delay)
                }
            }, delay)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}