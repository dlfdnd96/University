/**
 * 최근 본 착한가게(최대 5가게)를 보여주는 액티비티
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.lookedCurrentStore

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_current_store.*
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.storeList.NiceStoreListActivity.Companion.lookedCurrentStore

class CurrentStoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_store)

        /*
        액션바에 뒤로가기 버튼 설정
         */
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        /*
        착한가게들 사이에 구분선 삽입
         */
        current_store_list.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        current_store_list.layoutManager = LinearLayoutManager(this)
        current_store_list.setHasFixedSize(true)

        /*
        최근에 본 착한가게가 없을 경우: dialog 메세지를 띄운 후 이전 화면으로 되돌아가는 기능
        최근에 본 착한가게가 있을 경우: 최대 5개의 착한가게들을 보여줌
         */
        if (lookedCurrentStore.isEmpty()) {
            val builder = AlertDialog.Builder(this@CurrentStoreActivity)
            val dialogView = layoutInflater.inflate(R.layout.no_store_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    finish()
                }
                .show()

        } else {
            val mAdapter = CurrentStoreAdapter(lookedCurrentStore)
            mAdapter.notifyDataSetChanged()
            current_store_list.adapter = mAdapter
        }
    }

    /*
    뒤로가기 버튼 구현
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}