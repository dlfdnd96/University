/**
 * 데이터베이스 모듈 구성
 *
 * @since: 2019-06-04
 * @author: 류일웅
 */

package kr.ac.gachon.finalproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kr.ac.gachon.finalproject.model.NiceStoreDao
import kr.ac.gachon.finalproject.model.NiceStoreData

@Database(entities = [NiceStoreData::class], version = 1)
abstract class DatabaseModule : RoomDatabase() {

    abstract fun niceStoreDao(): NiceStoreDao

    companion object {
        private var database: DatabaseModule? = null
        private const val NICE_STORE_DB = "nice_store.db" // DB를 저장하는 파일 이름

        fun getDatabase(context: Context) : DatabaseModule {
            if (database == null) {
                synchronized(DatabaseModule::class) {
                    database = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseModule::class.java,
                        NICE_STORE_DB)
                        .fallbackToDestructiveMigration() // DB가 갱신될 때 기존의 테이블을 버리고 새로 사용
                        .build()
                }
            }

            return database!!
        }
    }

}