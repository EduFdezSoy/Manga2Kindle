package es.edufdezsoy.manga2kindle.service.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadcastReceiver(private val action: String) : BroadcastReceiver() {
    companion object {
        val PERCENT_EXTRA = "percent"
        
        val ACTION_UPDATE_CHAPTER_LIST = "es.edufdezsoy.intent.action.NEW_CHAPTER_LIST_UPDATE"
        val ACTION_UPDATED_CHAPTER_LIST = "es.edufdezsoy.intent.action.NEW_CHAPTER_LIST"

        val ACTION_UPDATE_CHAPTER_STATUS = "es.edufdezsoy.intent.action.CHAPTER_STATUS_UPDATE"
        val ACTION_UPDATED_CHAPTER_STATUS = "es.edufdezsoy.intent.action.CHAPTER_STATUS"
    }

    private lateinit var onFinished: () -> Unit
    private lateinit var onUpdate: (Int) -> Unit

    constructor(action: String, onFinished: () -> Unit) : this(action) {
        this.onFinished = onFinished
    }

    constructor(action: String, onFinished: () -> Unit, onUpdate: (percent: Int) -> Unit) : this(
        action,
        onFinished
    ) {
        this.onUpdate = onUpdate
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == action) {
            onFinished()
        } else if (::onUpdate.isInitialized && intent?.action == action + "_UPDATE") {
            val p = intent.getIntExtra(PERCENT_EXTRA, 0)
            onUpdate(p)
        }
    }
}