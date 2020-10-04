/**
 * 메인 화면 액티비티
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.address.AddressActivity
import kr.ac.gachon.finalproject.lookedCurrentStore.CurrentStoreActivity
import kr.ac.gachon.finalproject.sharedPreferences.App

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        화면 위쪽에 사용자가 설정한 주소 표시
         */
        txt_user_addr.text = "서울특별시 "
        txt_user_addr.append(App.prefs.addrEditText)

        /*
        서비스 품목 사이에 구분선 삽입
         */
        DividerItemDecoration(applicationContext, LinearLayoutManager(this).orientation).run {
            store_item_code_name_list.addItemDecoration(this)
        }

        store_item_code_name_list.adapter = MainAdapter()
        store_item_code_name_list.layoutManager = GridLayoutManager(this, 3)
    }

    /*
    주소변경과 최근 본 착한가게 기능 버튼을 수행할 수 있도록 설정
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    /*
    뒤로가기 버튼 구현
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.change_addr -> {
                startActivity(Intent(this, AddressActivity::class.java))
            }
            R.id.current_store -> {
                startActivity(Intent(this, CurrentStoreActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

}