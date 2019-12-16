package es.edufdezsoy.manga2kindle.service.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ServiceStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            Intent.ACTION_REBOOT,
            Intent.ACTION_BOOT_COMPLETED -> ServiceScheduler().sheduleScanManga(context!!)
        }
    }
}