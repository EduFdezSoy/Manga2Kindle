package es.edufdezsoy.manga2kindle.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.edufdezsoy.manga2kindle.R
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
