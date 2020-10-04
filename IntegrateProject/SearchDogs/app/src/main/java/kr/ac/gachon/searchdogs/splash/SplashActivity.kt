package kr.ac.gachon.searchdogs.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.ac.gachon.searchdogs.activity.MainActivity
import java.lang.Exception

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Thread.sleep(1000)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e : Exception) {
            return
        }
    }
}
