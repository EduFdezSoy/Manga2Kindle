package es.edufdezsoy.manga2kindle.ui.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.ui.main.MainController
import es.edufdezsoy.manga2kindle.ui.newChapters.NewChaptersController
import es.edufdezsoy.manga2kindle.ui.observedFolders.ObservedFoldersController
import es.edufdezsoy.manga2kindle.ui.settings.SettingsController
import es.edufdezsoy.manga2kindle.ui.uploadedChapters.UploadedChaptersController
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity(), BaseInteractor.Controller {

    //#region vars and vals

    private lateinit var router: Router
    private lateinit var drawer: Drawer
    private lateinit var interactor: BaseInteractor

    //#endregion
    //#region lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        interactor = BaseInteractor(this)
        router = Conductor.attachRouter(this, controller_container, savedInstanceState)

        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(MainController()))

        baseToolbar.setTitle(R.string.app_name)
        buildDrawer()
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.close(this)
    }

    //#endregion
    //#region public functions

    fun scanMangas() {
        interactor.scanMangas(this)
    }

    fun isScanningMangas(): Boolean {
        return interactor.isScanning()
    }

    fun showSnackbar(msg: String) {
        showSnackbar(msg, null, null, null)
    }

    fun showSnackbar(msg: String, lenght: Int) {
        showSnackbar(msg, lenght, null, null)
    }

    fun showSnackbar(msg: String, lenght: Int?, action: String?, listener: View.OnClickListener?) {
        val l: Int
        if (lenght == null)
            l = Snackbar.LENGTH_SHORT
        else
            l = lenght

        val snackbar = Snackbar.make(findViewById(android.R.id.content), msg, l)

        if (action != null)
            if (listener == null)
                throw KotlinNullPointerException("listener may be defined")
            else
                snackbar.setAction(action, listener)

        snackbar.show()
    }

    //#endregion
    //#region private functions

    private fun buildDrawer() {
        DrawerBuilder().withActivity(this).build()

        val newChapters = PrimaryDrawerItem().withIdentifier(1).withName("New Chapters")
        val uploadedChapters = PrimaryDrawerItem().withIdentifier(2).withName("Uploaded Chapters")
        val observedFolders = PrimaryDrawerItem().withIdentifier(3).withName("Observed Folders")
        val settings = SecondaryDrawerItem().withIdentifier(4).withName("Settings")

        // Create the AccountHeader
        val headerResult = AccountHeaderBuilder()
            .withActivity(this)
//            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                ProfileDrawerItem().withName("Manga2kindle").withEmail("test_mail@example.com").withIcon(
                    getDrawable(R.drawable.btn_radio_on_mtrl)
                )
            )
            .withOnAccountHeaderListener(object : AccountHeader.OnAccountHeaderListener {
                override fun onProfileChanged(
                    view: View?,
                    profile: IProfile<*>,
                    current: Boolean
                ): Boolean {
                    return false
                }
            })
            .build()

        //create the drawer and remember the `Drawer` result object
        drawer = DrawerBuilder()
            .withActivity(this)
            .withToolbar(baseToolbar)
            .withAccountHeader(headerResult)
            .addDrawerItems(
                newChapters,
                uploadedChapters,
                observedFolders,
                DividerDrawerItem(),
                settings
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when (drawerItem) {
                        newChapters -> router.setRoot(RouterTransaction.with(NewChaptersController()))
                        uploadedChapters -> router.setRoot(
                            RouterTransaction.with(
                                UploadedChaptersController()
                            )
                        )
                        observedFolders -> router.setRoot(
                            RouterTransaction.with(
                                ObservedFoldersController()
                            )
                        )
                        settings -> router.setRoot(
                            RouterTransaction.with(
                                SettingsController()
                            )
                        )
                        else -> Toast.makeText(
                            this@BaseActivity,
                            "henlo! :D",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return false
                }
            })
            .build()
    }

    //#endregion
}