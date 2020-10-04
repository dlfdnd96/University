/**
 * 착한가게에 대한 상세정보를 나타내는 액티비티
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_detail_nice_store.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.ac.gachon.finalproject.R
import kr.ac.gachon.finalproject.address.AddressActivity
import kr.ac.gachon.finalproject.database.DatabaseModule
import kr.ac.gachon.finalproject.lookedCurrentStore.CurrentStoreActivity
import kr.ac.gachon.finalproject.map.MapsActivity
import kr.ac.gachon.finalproject.model.NiceStoreData

class DetailNiceStoreActivity : AppCompatActivity() {

    /*
    초기화 지연을 실행하는 읽기 전용 프로퍼티
     */
    private val database by lazy {
        DatabaseModule.getDatabase(this)
    }

    private val actionBarTitle by lazy {
        intent.getStringExtra("detailCodeItemName")
    }

    /*
    DB의 결과 값들을 저장하는 변수들
     */
    private var loadDefaultList = listOf<NiceStoreData>()
    private var loadOtherList = listOf<NiceStoreData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_nice_store)

        /*
        액션바에 뒤로가기버튼과 제목 설정
         */
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = actionBarTitle

        detail_store.layoutManager = LinearLayoutManager(this)
        detail_store.setHasFixedSize(true)

        /*
        DB는 메인 쓰레드에서 실행이 불가능 하므로 coroutin으로 실행
         */
        CoroutineScope(Dispatchers.Main).launch {
            /*
            DB에서 먼저 결과 값을 불러온 후 다음 작업을 수행하기 위해 async, await 사용
             */
            CoroutineScope(Dispatchers.IO).async {
                loadDefaultList = database.niceStoreDao().loadDefaultDetailNiceStore(
                    intent.getLongExtra("detailStoreId", 0)
                )
                loadOtherList = database.niceStoreDao().loadOtherDetailNiceStore(
                    intent.getLongExtra("detailStoreId", 0)
                )
            }.await()

            /*
            착한가게 이름, 주소, 전화번호, 추천수 표시
             */
            sh_name.text = loadDefaultList[0].SH_NAME
            sh_addr.append(loadDefaultList[0].SH_ADDR)
            sh_phone.append(loadDefaultList[0].SH_PHONE)
            sh_rcmn.append(loadDefaultList[0].SH_RCMN.toString())

            /*
            지도보기 버튼을 클릭시 해당 위치를 보여주는 구글지도가 실행.
            착한가게 주소와 이름을 같이 전달
             */
            button_map.setOnClickListener {
                val intent = Intent(this@DetailNiceStoreActivity, MapsActivity::class.java)
                intent.putExtra("storeAddress", loadDefaultList[0].SH_ADDR)
                intent.putExtra("storeName", loadDefaultList[0].SH_NAME)

                startActivity(intent)
            }

            val mAdapter = DetailNiceStoreAdapter(ArrayList(loadOtherList))
            mAdapter.notifyDataSetChanged()
            detail_store.adapter = mAdapter
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

}
