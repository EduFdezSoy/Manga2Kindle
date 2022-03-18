package es.edufdezsoy.manga2kindle.ui.more.about

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mikepenz.aboutlibraries.LibsBuilder
import es.edufdezsoy.manga2kindle.BuildConfig
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentAboutBinding.inflate(inflater, container, false)

        @SuppressLint("SetTextI18n") // absolutely no reason to put this in a resource
        binding.descriptionVersionTextView.text =
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        clickListeners(binding)

        return binding.root
    }

    private fun clickListeners(binding: FragmentAboutBinding) {
        // Whats new
        binding.layoutWhatsNewConstraintLayout.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("https://github.com/EduFdezSoy/Manga2Kindle/releases/latest")
            startActivity(intent)
        }

        // Webpage
        binding.layoutWebpageConstraintLayout.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("https://www.manga2kindle.com/")
            startActivity(intent)
        }

        // Email
        binding.layoutEmailConstraintLayout.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("mailto:hello@manga2kindle.com")
            startActivity(intent)
        }

        // Github
        binding.layoutGithubConstraintLayout.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("https://github.com/Manga2Kindle")
            startActivity(intent)
        }

        // About Libraries (licenses)
        binding.layoutLicensesConstraintLayout.setOnClickListener {
            val abLibs = LibsBuilder()
                .withAboutIconShown(false)
                .withAboutVersionShown(false)
                .withLicenseShown(true)
                .withEdgeToEdge(true)

            findNavController().navigate(
                R.id.action_aboutFragment_to_about_libraries,
                bundleOf("data" to abLibs)
            )
        }
    }
}