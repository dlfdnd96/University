/**
 * 주소 변경하는 액티비티
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.address

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_address.*
import kr.ac.gachon.finalproject.main.MainActivity
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.sharedPreferences.App

class AddressActivity : AppCompatActivity() {

    var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        /*
        스피너를 누르면 주소 목록들이 나오도록 기능 구현
         */
        addr_spinner.adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, AddressData.values().map { it.holder })
        addr_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {  }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                address = AddressData.values()[position].holder
            }
        }

        /*
        선택완료 버튼을 누르면 주소가 SharedPreferences에 저장되고 메인 엑티비티 시작
         */
        button_addr.setOnClickListener {
            App.prefs.addrEditText = address

            Toast.makeText(this, "주소가 저장 됐습니다.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /*
    뒤로 가기 버튼 구현
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}