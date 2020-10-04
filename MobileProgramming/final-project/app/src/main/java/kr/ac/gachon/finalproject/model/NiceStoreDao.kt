/**
 * 착한가게 DB의 DAO
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */
package kr.ac.gachon.finalproject.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NiceStoreDao {
    /*
    착한가게 리스트들을 DB에 저장
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNiceStore (
        niceStoreData: ArrayList<NiceStoreData>
    )

    /*
    착한가게 리스트들을 전부 불러오는 기능
     */
    @Query("SELECT * FROM NiceStore")
    fun loadNiceStoreAllData(): List<NiceStoreData>

    /*
    사용자가 설정한 주소와 일치하는 착한가게들만 불러오는 기능
     */
    @Query("SELECT * FROM NiceStore WHERE INDUTY_CODE_SE = :code AND SH_ADDR LIKE:address GROUP BY SH_NAME")
    fun loadNiceStoreDataList(code: Long, address: String): List<NiceStoreData>

    /*
    착한가게의 이름, 주소, 전화번호, 추천수를 불러오기 위한 기능
     */
    @Query("SELECT * FROM NiceStore WHERE SH_ID = :id GROUP BY SH_NAME")
    fun loadDefaultDetailNiceStore(id: Long): List<NiceStoreData>

    /*
    착한가게의 대표 서비스 품목과 가격을 볼러오기 위한 기능
     */
    @Query("SELECT * FROM NiceStore WHERE SH_ID = :id")
    fun loadOtherDetailNiceStore(id: Long): List<NiceStoreData>

    /*
    테이블 내용을 삭제하는 기능
     */
    @Query("DELETE FROM NiceStore")
    fun deleteData()
}