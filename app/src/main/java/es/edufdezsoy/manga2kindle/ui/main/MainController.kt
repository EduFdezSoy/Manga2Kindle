package es.edufdezsoy.manga2kindle.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import es.edufdezsoy.manga2kindle.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainController : Controller(), CoroutineScope {
    //#region vars and vals

    val interactor = MainInteractor
    lateinit var view: MainView
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    //#endregion
    //#region lifecycle methods

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v = inflater.inflate(R.layout.view_main, container, false)

        job = Job()
        view = MainView(view = v, controller = this)

        return v
    }

    override fun onDestroyView(view: View) {
        job.cancel()
        super.onDestroyView(view)
    }

    //#endregion
    //#region public methods

    fun callServerHello() {
        launch {
            val res = interactor.callServer()
            view.drawServerHello(res!!)

        }
    }

    fun callAuthorSearch() {
        launch {
            val res = interactor.callAuthorSearch()
            view.drawAuthorData(res!!)
        }
    }

    //#endregion
}