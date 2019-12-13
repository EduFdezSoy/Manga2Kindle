package es.edufdezsoy.manga2kindle.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.edufdezsoy.manga2kindle.data.M2kSharedPref
import es.edufdezsoy.manga2kindle.ui.appIntro.IntroActivity
import es.edufdezsoy.manga2kindle.ui.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, BaseActivity::class.java)
        startActivity(intent)

        launch {
            val firstStart =
                M2kSharedPref.invoke(this@SplashActivity).getBoolean("fisrtStart", true)

            if (firstStart) {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                M2kSharedPref.invoke(this@SplashActivity)
                    .edit()
                    .putBoolean("fisrtStart", false)
                    .apply()
            }
        }

        finish()
    }
}