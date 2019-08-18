package es.edufdezsoy.manga2kindle.ui.main

import android.os.Bundle
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.network.ApiService
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv1.text = "currando..."


        // load the server hello message
        Thread(Runnable {
            val call = ApiService.apiService.serverHello()
            val res = call.execute().body()
            this@MainActivity.runOnUiThread { tv1.text = res!! }
        }).start()

        // load an author
        Thread(Runnable {
            val call = ApiService.apiService.searchAuthor("a")
            val res = call.execute().body()?.get(9)
            this@MainActivity.runOnUiThread {
                tv2.text = res!!.name
                tv3.text = res.surname
                tv4.text = res.nickname
            }
        }).start()
    }
}
