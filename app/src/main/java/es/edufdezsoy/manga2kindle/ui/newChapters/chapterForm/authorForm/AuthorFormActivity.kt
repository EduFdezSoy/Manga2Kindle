package es.edufdezsoy.manga2kindle.ui.newChapters.chapterForm.authorForm

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.ui.base.BaseInteractor
import kotlinx.android.synthetic.main.activity_base.*

class AuthorFormActivity : AppCompatActivity(), BaseInteractor.Controller {
    //#region vars and vals

    private lateinit var router: Router
    private lateinit var controller: AuthorFormController

    companion object {
        const val CHAPTER_KEY = "chapter_extra_key"
    }


    //#endregion
    //#region lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        router = Conductor.attachRouter(this, controller_container, savedInstanceState)

        val chapter_id = intent.getIntExtra(CHAPTER_KEY, 0)
        controller = AuthorFormController(chapter_id)

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