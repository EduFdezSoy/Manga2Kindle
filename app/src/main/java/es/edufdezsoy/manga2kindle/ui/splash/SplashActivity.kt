package es.edufdezsoy.manga2kindle.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, BaseActivity::class.java)
        startActivity(intent)
        finish()
    }
}