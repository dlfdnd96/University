/**
 * 메인 화면
 *
 * @since: 2019.10.01
 * @author: 이정묵, 류일웅
 */
package kr.ac.gachon.searchdogs.activity

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kr.ac.gachon.searchdogs.R
import kr.ac.gachon.searchdogs.fragment.CameraFragment
import kr.ac.gachon.searchdogs.fragment.DictFragment
import kr.ac.gachon.searchdogs.fragment.GalleryFragment


class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var firstTime : Long = 0
    private var secondTime : Long = 0

    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
        navView = findViewById(R.id.main_bnv)
        navView.setOnNavigationItemSelectedListener(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // 앱 실행시 초기 화면 설정
        navView.selectedItemId = R.id.Camera

        notice()
    }

    override fun onBackPressed() {
        secondTime = System.currentTimeMillis()

        if (secondTime - firstTime < 2000) {
            super.onBackPressed()
            finish()
        } else Toast.makeText(this,"'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()

        firstTime = System.currentTimeMillis()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.Gallery -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fl, GalleryFragment())
                    .commit()
            }
            R.id.Camera -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fl, CameraFragment())
                    .commit()
            }
            R.id.Dictionary -> {
                Toast.makeText(this, "사전 불러오는 중", Toast.LENGTH_SHORT).show()
                supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fl, DictFragment())
                .commit()
            }
        }
        return true
    }

    private fun notice() {
        val intent = Intent(this, NoticeActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
