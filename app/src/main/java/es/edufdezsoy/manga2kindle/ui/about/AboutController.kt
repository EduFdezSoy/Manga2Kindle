package es.edufdezsoy.manga2kindle.ui.about

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutController : Controller() {
    private lateinit var context: Context
    private var versionEasterEggCounter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        context = inflater.context

        val view = AboutPage(context)
            .isRTL(false)
            .setImage(R.drawable.profile_icon)
            .setDescription(context.getString(R.string.about_description))
            .addGroup(context.getString(R.string.about_first_group))
            .addEmail(
                context.getString(R.string.about_email),
                context.getString(R.string.about_email)
            )
            .addWebsite(
                context.getString(R.string.about_website),
                context.getString(R.string.about_website_title)
            )
            .addPlayStore(
                context.getString(R.string.about_play_store_url),
                context.getString(R.string.about_play_store_title)
            )
            .addGroup(context.getString(R.string.about_second_group))
            .addGitHub(
                context.getString(R.string.about_username),
                context.getString(R.string.about_github_title)
            )
            .addTwitter(
                context.getString(R.string.about_username),
                context.getString(R.string.about_twitter_title)
            )
            .addInstagram(
                context.getString(R.string.about_username),
                context.getString(R.string.about_instagram_title)
            )
            .addItem(formVersionElement())
            .create()

        return view
    }

    private fun formVersionElement(): Element {
        val versionElement = Element()

        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        versionElement.title = context.getString(R.string.about_version, versionName)
        versionElement.setGravity(Gravity.CENTER)
        versionElement.setOnClickListener {
            if (++versionEasterEggCounter == 5) {
                Toast.makeText(
                    context,
                    context.getString(R.string.about_version_easterEgg),
                    Toast.LENGTH_LONG
                ).show()
                versionEasterEggCounter = 0
            }
        }
        return versionElement
    }
}
