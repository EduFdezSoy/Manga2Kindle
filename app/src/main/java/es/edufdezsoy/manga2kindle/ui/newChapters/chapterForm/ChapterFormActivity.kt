package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.ui.base.BaseInteractor
import kotlinx.android.synthetic.main.activity_base.*

class ChapterFormActivity : AppCompatActivity(), BaseInteractor.Controller {
    //#region vars and vals

    private lateinit var router: Router
    private lateinit var toolbar: Toolbar
    private lateinit var controller: ChapterFormController

    companion object {
        val CHAPTER_KEY = "chapter_extra_key"
        val MANGA_KEY = "manga_extra_key"
        val AUTHOR_KEY = "author_extra_key"
    }

    //#endregion
    //#region lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        router = Conductor.attachRouter(this, controller_container, savedInstanceState)

        val chapter_id = intent.getIntExtra(CHAPTER_KEY, 0)
        val manga_id = intent.getIntExtra(MANGA_KEY, 0)
        val author_id = intent.getIntExtra(AUTHOR_KEY, 0)

        controller = ChapterFormController(chapter_id, manga_id, author_id)

        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(controller))

        setToolbar()
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }

    //#endregion
    //#region toolbar functions

    private fun setToolbar() {
        setSupportActionBar(baseToolbar)
        baseToolbar.setTitle(R.string.app_name)
        baseToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        baseToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chapter_form_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return controller.onOptionsItemSelected(item)
    }

    //#endregion

}