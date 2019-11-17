package es.edufdezsoy.manga2kindle.ui.about

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutController : Controller() {
    private lateinit var context: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        context = inflater.context

        val view = AboutPage(context)
            .isRTL(false)
            .setImage(R.drawable.profile_icon)
            .setDescription(
                "Hi! my name is Eduardo and I like to make things. \n " +
                        "Feel free to email me, I'll be happy to hear you! \n " +
                        "If you liked the app leave me some stars on the Play Store! ╰(✿˙ᗜ˙)੭━☆ﾟ.*･｡ﾟ"
            )
            .addGroup("Manga2Kindle info")
            .addEmail("hello@manga2kindle.com", "hello@manga2kindle.com")
            .addWebsite("https://manga2kindle.com/", "manga2kindle.com")
            .addPlayStore("es.edufdezsoy.manga2kindle", "Play Store")
            .addGroup("Follow me!")
            .addGitHub("EduFdezSoy", "GitHub")
            .addTwitter("EduFdezSoy", "Twitter")
            .addInstagram("EduFdezSoy", "Instagram")
            .addItem(formVersionElement())
            .create()

        return view
    }

    private fun formVersionElement(): Element {
        val versionElement = Element()

        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        versionElement.title = "Manga2Kindle v$versionName - Eduardo Fernandez"
        versionElement.setGravity(Gravity.CENTER)
        return versionElement
    }
}
