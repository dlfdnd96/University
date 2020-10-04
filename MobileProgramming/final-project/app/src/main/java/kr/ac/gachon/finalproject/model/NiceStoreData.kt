/**
 * 착한가게 테이블 Data 클래스
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "NiceStore")
data class NiceStoreData(
    /*
    위에서부터 순서대로
    고유번호, 착한가게 번호, 이름, 서비스 분류 번호, 서비스 분류 이름,
    주소, 전화번호, 추천수, 서비스 품목 이름, 서비스 품목 가격
     */
    @PrimaryKey(autoGenerate = true)
    var UUID: Long? = null,
    var SH_ID: Long,
    var SH_NAME: String,
    var INDUTY_CODE_SE: Int,
    var INDUTY_CODE_SE_NAME: String,
    var SH_ADDR: String,
    var SH_PHONE: String,
    var SH_RCMN: Int,
    var IM_NAME: String,
    var IM_PRICE: Int
)

/*
공공데이터에서 착한가게 리스트들을 불러온 후, 변수에 저장할 때 사용하는 data 클래스
 */
data class NiceStoreDataInfo(val list_total_count: Int, val row: List<NiceStoreData>)
