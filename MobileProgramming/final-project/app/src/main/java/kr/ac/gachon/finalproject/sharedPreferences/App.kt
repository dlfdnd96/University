/**
 * SharedPreferences 사용하기 위한 클래스.
 * 다른 액티비티보다 먼저 생성되어야 다른 곳에 데이터 전송이 가능
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.sharedPreferences

import android.app.Application

class App : Application() {

    companion object {
        lateinit var prefs: AddressSharedPreferences
    }

    override fun onCreate() {
        /*
        prefs 초기화
         */
        prefs = AddressSharedPreferences(applicationContext)
        super.onCreate()
    }

}