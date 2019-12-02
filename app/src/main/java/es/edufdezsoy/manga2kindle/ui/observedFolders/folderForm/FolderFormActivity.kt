package es.edufdezsoy.manga2kindle.ui.observedFolders.folderForm

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

class FolderFormActivity : AppCompatActivity(), BaseInteractor.Controller {
    //#region vars and vals

    private lateinit var router: Router
    private lateinit var toolbar: Toolbar
    private lateinit var controller: FolderFormController
    private var folder_id = 0

    companion object {
        val FOLDER_KEY = "folder_extra_key"
    }

    //#endregion
    //#region lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        folder_id = intent.getIntExtra(FOLDER_KEY, 0)

        router = Conductor.attachRouter(this, controller_container, savedInstanceState)
        controller = FolderFormController(folder_id)

        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(controller))

        setToolbar()
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
        if (folder_id != 0)
            menuInflater.inflate(R.menu.folder_form_menu, menu)
        else
            menuInflater.inflate(R.menu.chapter_form_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return controller.onOptionsItemSelected(item)
    }

    //#endregion
}