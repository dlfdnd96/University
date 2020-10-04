/**
 * 사용자 주소를 저장하기 위한 SharedPreferences
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.sharedPreferences

import android.content.Context
import android.content.SharedPreferences

class AddressSharedPreferences(context: Context) {

    /*
    파일 이름과 주소를 저장할 key 값을 만들고 prefs 인스턴스 초기화
     */
    val PREFS_FILENAME = "prefs"
    val PREF_KEY_ADDR_EDIT_TEXT = "addrEditText"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    /*
    get, set 함수 설정.
    get 실행 시 저장된 값을 반환
    set 실행 시 value로 값을 대체한 후 저장
     */
    var addrEditText: String
        get() = prefs.getString(PREF_KEY_ADDR_EDIT_TEXT, "")
        set(value) = prefs.edit().putString(PREF_KEY_ADDR_EDIT_TEXT, value).apply()

}