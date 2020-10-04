/**
 * 착한가게 서비스 분류들을 모아논 enum 클래스
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.model

import kr.ac.gachon.finalproject.R


enum class NiceStores(val mcode: String, val holder: String, val imageId: Int) {
    KOREAN_FOOD("1", "한식", R.drawable.korean_food),
    CHINA_FOOD("2","중식", R.drawable.china_food),
    JAPAN_FOOD("3", "경양식일식", R.drawable.japan_food),
    OTHER_FOOD("4", "기타외식업(다방패스트푸드등)", R.drawable.other_food),
    HAIR("5", "이 미용업", R.drawable.hair),
    BATH("6", "목욕업", R.drawable.bath),
    LAUNDRY("7", "세탁업", R.drawable.laundry),
    STAYING("8", "숙박업", R.drawable.staying),
    MOVIE("9", "영화관람", R.drawable.movie),
    SPORTS("12", "수영장/볼링장/당구장/골프연습장", R.drawable.sports),
    OTHER_SERVICE("13", "기타서비스업종", R.drawable.other_service)
}