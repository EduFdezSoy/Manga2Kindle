package es.edufdezsoy.manga2kindle.ui.uploaded

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.lifecycle.observe
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.network.ApiService
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * TODO: Create list
 * - Change class name, this will be the upload list
 * This list will be split in two:
 * First half will have the chapters uploading or with some error
 * Second half will have the completed ones (or with errors if dismissed)
 *
 * The uploading ones can have a progress bar, not needed in the first version but cool to have,
 * add it to a git issue if you are not going to do it now.
 */
class DashboardFragment : Fragment() {
    val apiService = ApiService.getInstance(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        lifecycleScope.launch {
            getVersion().observe(viewLifecycleOwner) {
                textView.text = it.version
            }
        }

        return view
    }

    private fun getVersion() = liveData(Dispatchers.IO) {
        emit(apiService.getVersion())
    }
}