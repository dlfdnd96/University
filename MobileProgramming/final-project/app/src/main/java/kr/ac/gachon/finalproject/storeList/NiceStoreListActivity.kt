/**
 * 상품 분류에 대한 착한가게 리스트를 보여주는 액티비티
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.storeList

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_nice_store_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.address.AddressActivity
import kr.ac.gachon.finalproject.database.DatabaseModule
import kr.ac.gachon.finalproject.lookedCurrentStore.CurrentStoreActivity
import kr.ac.gachon.finalproject.model.NiceStoreData
import kr.ac.gachon.finalproject.sharedPreferences.App

class NiceStoreListActivity : AppCompatActivity() {

    /*
    초기화 지연을 실행하는 읽기 전용 프로퍼티
     */
    private val database by lazy {
        DatabaseModule.getDatabase(this)
    }

    private val actionBarTitle by lazy {
        intent.getStringExtra("selectedCodeItemName")
    }

    private var loadNiceStoreList = listOf<NiceStoreData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nice_store_list)

        /*
        액션바에 뒤로가기와 제목 설정
         */
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = actionBarTitle

        /*
        착한가게를 사이에 구분선 삽입
         */
        store_list.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        store_list.layoutManager = LinearLayoutManager(this)
        store_list.setHasFixedSize(true)

        /*
        DB를 사용하기 위해 coroutin 사용
         */
        CoroutineScope(Dispatchers.Main).launch {
            /*
            사용자가 설정한 주소에 맞게 착한가게 리스트를 변수에 저장
             */
            CoroutineScope(Dispatchers.Default).async {
                loadNiceStoreList = database.niceStoreDao().loadNiceStoreDataList(
                    intent.getLongExtra("selectedCode", 0)
                    , "%" + App.prefs.addrEditText + "%"
                )
            }.await()

            /*
            착한가게 리스트가 없을 경우: dialog 메세지 생성 후 확인을 누르면 이전화면으로 전환
            착한가게 리스트가 있을 경우: 착한가게 리스트들을 보여줌
             */
            if (loadNiceStoreList.count() == 0) {
                val builder = AlertDialog.Builder(this@NiceStoreListActivity)
                val dialogView = layoutInflater.inflate(R.layout.no_store_dialog, null)
                builder.setView(dialogView)
                    .setPositiveButton("확인") { dialogInterface, i ->
                        finish()
                    }
                    .show()
            } else {
                val mAdapter = NiceStoreListAdapter(ArrayList(loadNiceStoreList))
                mAdapter.notifyDataSetChanged()
                store_list.adapter = mAdapter
            }
        }
    }

    /*
    옵션 메뉴를 볼 수 있도록 설정
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    /*
    주소변경과 최근 본 착한가게 기능 버튼을 수행할 수 있도록 설정
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.change_addr ->
                startActivity(Intent(this, AddressActivity::class.java))
            R.id.current_store ->
                startActivity(Intent(this, CurrentStoreActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    /*
    뒤로가기 버튼 구현
    */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /*
    최근 본 착한가게 리스트들을 저장하기 위한 전역변수 선언
     */
    companion object {
        var lookedCurrentStore: MutableList<NiceStoreData> = ArrayList()
    }

}