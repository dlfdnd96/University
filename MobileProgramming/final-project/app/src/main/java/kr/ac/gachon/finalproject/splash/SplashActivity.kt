/**
 * 처음 앱 실행시 잠깐 나타나는 Splash
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.ac.gachon.finalproject.main.MainActivity
import kr.ac.gachon.finalproject.address.AddressActivity
import kr.ac.gachon.finalproject.database.DatabaseModule
import kr.ac.gachon.finalproject.model.NiceStoreData
import kr.ac.gachon.finalproject.model.NiceStoreDataInfo
import kr.ac.gachon.finalproject.network.NetworkModule
import kr.ac.gachon.finalproject.sharedPreferences.App
import okhttp3.Response

class SplashActivity : AppCompatActivity() {

    /*
    초기화 지연을 실행하는 읽기 전용 프로퍼티
     */
    private val database by lazy {
        DatabaseModule.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        공공데이터에서 값을 불러와 DB에 저장
         */
        loadDataFromUrl()

        val addressIntent = Intent(this, AddressActivity::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)

        /*
        앱에 사전에 설정한 주소가 있다면 메인화면 실행
        앱에 설정한 주소가 없다면 주소 설정화면 실행
         */
        if (App.prefs.addrEditText.isNotEmpty()) {
            startActivity(mainIntent)
        } else {
            startActivity(addressIntent)
        }

        finish()
    }

    /**
     * 공공데이터에서 값을 불러와 DB에 저장
     *
     * @since: 2019-06-04
     * @author: 류일웅
     * @param: None
     * @return: None
     */
    private fun loadDataFromUrl() {
        /*
        DB에 값을 저장하기 위해 coroutin 사용
         */
        CoroutineScope(Dispatchers.Main).launch {
            /*
            착한가게 공공데이터는 입력받는 property가 페이지 숫자만 가능.
            페이지 숫자는 착한가게 데이터 행을 의미.
            현재까지 데이터는 1800여개 있으므로 1부터 1800사이의 수를 URL에 입력해서 데이터를 받을 수 있다
             */
            var niceStoreList: ArrayList<NiceStoreData> = ArrayList()
            var pageCount = 1

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    /*
                    착한가게 공공데이터는 한 번에 1000개씩 받아올 수 있으므로 2번 반복한다
                     */
                    for (i in 0..1) {
                        val httpUrl = NetworkModule.makeHttpRequest(
                            NetworkModule.makeHttpUrl(pageCount = pageCount)
                        )
                        var response: Response? = null

                        /*
                        공공데이터를 요청
                         */
                        CoroutineScope(Dispatchers.IO).async {
                            response = NetworkModule.client.newCall(httpUrl).execute()
                        }.await()

                        with(response?.body()?.string()) {
                            /*
                            공공데이터를 성공적으로 받았을 경우 변수에 데이터 저장
                             */
                            if (response!!.isSuccessful && !this.isNullOrBlank()) {
                                niceStoreList = savePublicData(this, niceStoreList)
                            }
                        }

                        pageCount = 1000
                    }
                } catch (e: Error) {
                    Log.i("DATABASE ERROR", e.message)
                }

                /*
                기존의 테이블 내용을 삭제하고 다시 삽입
                 */
                database.niceStoreDao().deleteData()
                database.niceStoreDao().insertNiceStore(niceStoreList)
            }
        }
    }

    /**
     * 공공데이터를 변수에 저장하는 기능
     *
     * @since: 2019-06-04
     * @author: 류일웅
     * @param: jsonBody, storeList
     * @return: ArrayList<NiceStoreData>
     */
    private fun savePublicData(jsonBody: String, storeList: ArrayList<NiceStoreData>) : ArrayList<NiceStoreData> {
        try {
            /*
            JSON으로 받아온 데이터를 파싱
             */
            val gson = GsonBuilder().create()
            val parser = JsonParser()
            val rootObj = parser.parse(jsonBody).asJsonObject.get("ListPriceModelStoreProductService")
            val niceStore = gson.fromJson(rootObj, NiceStoreDataInfo::class.java)

            /*
            JSON 데이터가 없다면 Error Log 생성
             */
            if (niceStore.row.isEmpty()) {
                Log.i("JSON ERROR", "JSON Data is empty!!")
                return storeList
            }

            /*
            공공데이터를 불러온 만큼 변수에 데이터를 저장
             */
            for (i in 0..(niceStore.row.size - 1)) {
                NiceStoreData(
                    SH_ID = niceStore.row[i].SH_ID,
                    SH_NAME = niceStore.row[i].SH_NAME,
                    INDUTY_CODE_SE = niceStore.row[i].INDUTY_CODE_SE,
                    INDUTY_CODE_SE_NAME = niceStore.row[i].INDUTY_CODE_SE_NAME,
                    SH_PHONE = niceStore.row[i].SH_PHONE,
                    SH_ADDR = niceStore.row[i].SH_ADDR,
                    SH_RCMN = niceStore.row[i].SH_RCMN,
                    IM_NAME = niceStore.row[i].IM_NAME,
                    IM_PRICE = niceStore.row[i].IM_PRICE
                ).run {
                    storeList.add(this)
                }
            }

        } catch (e: Exception) {
            Log.i("JSON ERROR", e.message)
        }

        return storeList
    }

}