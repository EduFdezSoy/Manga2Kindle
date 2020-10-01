package es.edufdezsoy.manga2kindle.ui.watchedFolders

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.service.ExampleJobService
import es.edufdezsoy.manga2kindle.service.ExampleService
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.view.*

class NotificationFragment : Fragment() {
    private val TAG = this::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        view.startServiceBtn.setOnClickListener { startService() }
        view.stopServiceBtn.setOnClickListener { stopService() }
        view.startServiceBtn2.setOnClickListener { startScheduledService() }
        view.stopServiceBtn2.setOnClickListener { stopScheduledService() }

        return view
    }

    private fun startService() {
        textView.text = "service started"

        val serviceIntent = Intent(context, ExampleService::class.java)
        serviceIntent.putExtra("inputExtra", "example input")

        activity?.startService(serviceIntent)
    }

    private fun stopService() {
        textView.text = "service stopped"

        val serviceIntent = Intent(context, ExampleService::class.java)
        activity?.stopService(serviceIntent)
    }

    private fun startScheduledService() {
        textView.text = "scheduled service started"

        val cn = ComponentName(requireContext(), ExampleJobService::class.java)
        val ji = JobInfo.Builder(123, cn)
            .setRequiresCharging(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(15 * 60 * 1000) // 15 mins, the lower valid value

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            ji.setImportantWhileForeground(true)
        }

        // check: https://developer.android.com/reference/android/app/job/JobInfo.Builder.html

        val scheduler = activity?.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(ji.build())
        if (resultCode == JobScheduler.RESULT_SUCCESS)
            Log.d(TAG, "startScheduledService: Job Scheduled")
        else
            Log.d(TAG, "startScheduledService: Job Scheduling failed")
    }

    private fun stopScheduledService() {
        textView.text = "scheduled service stoped"
        val scheduler = activity?.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(123)
        Log.d(TAG, "stopScheduledService: Job Canceled")
    }
}