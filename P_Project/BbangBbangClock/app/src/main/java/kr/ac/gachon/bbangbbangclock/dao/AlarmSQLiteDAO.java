package kr.ac.gachon.bbangbbangclock.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.gachon.bbangbbangclock.common.StatusCode;

/**
 * SQLLite DAO
 * 모든 SELECT의 결과를 JSON으로 반환
 *
 * @since : 19-11-25
 * @author : 빵빵시게팀
 */
public class AlarmSQLiteDAO extends SQLiteOpenHelper implements AlarmDAO {

    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private ContentValues contentValues;
    private final String ALARM_DATA_TABLE = "alarm_data";
    private final String BOARDING_DATA_TABLE = "boarding_data";
    private final String[] ALARM_DATA_TABLE_COLUMN = {"pk_num", "hour", "minute", "station_class", "station_id",
            "station_name", "cid", "ars_id", "city_name", "route_id", "route_name", "local_station_id",
            "subway_direction", "day", "checked", "sound_id", "volume"};
    private JSONObject databaseJson;
    private JSONArray resultJsonArray;

    public AlarmSQLiteDAO(Context context) {
        super(context, "Alarm.db", null, DATABASE_VERSION); //데이터베이스 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALARM_DATA_TABLE +
                "(pk_num INTEGER PRIMARY KEY, "+
                "hour INTEGER, "+
                "minute INTEGER, "+
                "station_class TEXT, "+
                "station_id TEXT, "+
                "station_name TEXT, "+
                "cid TEXT, "+
                "ars_id TEXT, "+
                "city_name TEXT, "+
                "route_id TEXT, "+
                "route_name TEXT, "+
                "local_station_id TEXT, "+
                "subway_direction TEXT, "+
                "day TEXT, "+
                "checked INTEGER, "+ //1은 설정,0은 해제
                "sound_id INTEGER, "+
                "volume INTEGER);"
        );
        db.execSQL("CREATE TABLE IF NOT EXISTS " + BOARDING_DATA_TABLE +
                "(pk_num INTEGER PRIMARY KEY, "+
                "current_data TEXT, "+
                "route_name TEXT, "+
                "station_name TEXT);"
        );
    }

    @Override
    public void insertBoardingData(
            String currentDate,
            String routeName,
            String stationName){
        db = getWritableDatabase();
        contentValues = new ContentValues();

        contentValues.put(StatusCode.DB_CURRENT_DATA, currentDate);
        contentValues.put(StatusCode.DB_ROUTE_NAME, routeName);
        contentValues.put(StatusCode.DB_STATION_NAME, stationName);

        db.insert(BOARDING_DATA_TABLE,null, contentValues);
        db.close();
    }

    @Override
    public void insertAlarmData(
            Integer hour,
            Integer minute,
            String stationClass,
            String stationID,
            String stationName,
            String cid,
            String arsID,
            String cityName,
            String routeID,
            String routeName,
            String localStationID,
            String subwayDirection,
            String day,
            Integer soundID,
            Integer volume) {
        db = getWritableDatabase();

        contentValues = new ContentValues();

        contentValues.put(StatusCode.DB_HOUR, hour);
        contentValues.put(StatusCode.DB_MINUTE, minute);
        contentValues.put(StatusCode.DB_STATION_CLASS, stationClass);
        contentValues.put(StatusCode.DB_STATION_ID, stationID);
        contentValues.put(StatusCode.DB_STATION_NAME, stationName);
        contentValues.put(StatusCode.DB_CID, cid);
        contentValues.put(StatusCode.DB_ARS_ID, arsID);
        contentValues.put(StatusCode.DB_CITY_NAME, cityName);
        contentValues.put(StatusCode.DB_ROUTE_ID, routeID);
        contentValues.put(StatusCode.DB_ROUTE_NAME, routeName);
        contentValues.put(StatusCode.DB_LOCAL_STATION_ID, localStationID);
        contentValues.put(StatusCode.DB_SUBWAY_DIRECTION, subwayDirection);
        contentValues.put(StatusCode.DB_DAY, day);
        contentValues.put(StatusCode.DB_CHECKED, 1);
        contentValues.put(StatusCode.DB_SOUND_ID, soundID);
        contentValues.put(StatusCode.DB_VOLUME, volume);

        db.insert(ALARM_DATA_TABLE, null, contentValues);

        db.close();
    }

    @Override
    public void updateAlarmData(
            Integer pkNumber,
            Integer hour,
            Integer minute,
            String stationClass,
            String stationID,
            String stationName,
            String cid,
            String arsID,
            String cityName,
            String routeID,
            String routeName,
            String localStationID,
            String subwayDirection,
            String day,
            Integer soundID,
            Integer volume) {
        db = getWritableDatabase();

        contentValues = new ContentValues();

        contentValues.put(StatusCode.DB_HOUR, hour);
        contentValues.put(StatusCode.DB_MINUTE, minute);
        contentValues.put(StatusCode.DB_STATION_CLASS, stationClass);
        contentValues.put(StatusCode.DB_STATION_ID, stationID);
        contentValues.put(StatusCode.DB_STATION_NAME, stationName);
        contentValues.put(StatusCode.DB_CID, cid);
        contentValues.put(StatusCode.DB_ARS_ID, arsID);
        contentValues.put(StatusCode.DB_CITY_NAME, cityName);
        contentValues.put(StatusCode.DB_ROUTE_ID, routeID);
        contentValues.put(StatusCode.DB_ROUTE_NAME, routeName);
        contentValues.put(StatusCode.DB_LOCAL_STATION_ID, localStationID);
        contentValues.put(StatusCode.DB_SUBWAY_DIRECTION, subwayDirection);
        contentValues.put(StatusCode.DB_DAY, day);
        contentValues.put(StatusCode.DB_CHECKED, 1);
        contentValues.put(StatusCode.DB_SOUND_ID, soundID);
        contentValues.put(StatusCode.DB_VOLUME, volume);

        String[] numArr = {pkNumber.toString()};

        db.update(ALARM_DATA_TABLE, contentValues, StatusCode.DB_PK_NUM + " = ?", numArr);

        db.close();
    }

    @Override
    public void updateChecked(Integer pkNumber, Integer checked) {
        db = getWritableDatabase();

        contentValues = new ContentValues();

        contentValues.put(StatusCode.DB_CHECKED, checked);

        db.update(ALARM_DATA_TABLE, contentValues,
                StatusCode.DB_PK_NUM + " = ?", new String[] {pkNumber.toString()});

        db.close();
    }

    @Override
    public JSONArray getAlarmData(Integer pkNumber) {
        db = getReadableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.query(ALARM_DATA_TABLE,null,StatusCode.DB_PK_NUM + " = ?",
                    new String[] {pkNumber.toString()},null,null,null
            );

            if (cursor != null) {
                resultJsonArray = new JSONArray();

                while (cursor.moveToNext()) {
                    databaseJson = new JSONObject();

                    for (String element : ALARM_DATA_TABLE_COLUMN)
                        databaseJson.put(element, cursor.getString(cursor.getColumnIndex(element)));

                    resultJsonArray.put(databaseJson);
                }
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                db.close();
        }

        return resultJsonArray;
    }

    @Override
    public JSONArray getAlarmDataList() {
        db = getReadableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.query(ALARM_DATA_TABLE,null,null,null,
                    null,null,null
            );

            if (cursor != null) {
                resultJsonArray = new JSONArray();

                while (cursor.moveToNext()) {
                    databaseJson = new JSONObject();

                    for (String element : ALARM_DATA_TABLE_COLUMN)
                        databaseJson.put(element, cursor.getString(cursor.getColumnIndex(element)));

                    resultJsonArray.put(databaseJson);
                }
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                db.close();
        }

        return resultJsonArray;
    }

    @Override
    public JSONArray getBoardingDataListStName(String month) {
        String[] split1 = month.split("\\월");
        String[] split2 = split1[0].split("\\분");
        String result = split2[0];
        String a;
        switch (result){
            case "1":
                a="'01%' OR current_data LIKE '02%' OR current_data LIKE '03%'";
                break;
            case "2":
                a="'04%' OR current_data LIKE '05%' OR current_data LIKE '06%'";
                break;
            case "3":
                a="'07%' OR current_data LIKE '08%' OR current_data LIKE '09%'";
                break;
            case "4":
                a="'10%' OR current_data LIKE '11%' OR current_data LIKE '12%'";
                break;
            default:
                a = "'"+result+"%"+"'";
                break;
        }

        db = getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT station_name, COUNT(*) FROM boarding_data GROUP BY station_name  HAVING current_data LIKE " + a;
            cursor = db.rawQuery(sql,null);

            if (cursor != null) {
                resultJsonArray = new JSONArray();

                while(cursor.moveToNext()){
                    String stationName = cursor.getString(0);
                    String countStationName = cursor.getString(1);
                    databaseJson = new JSONObject();
                    databaseJson.put("station_name", stationName);
                    databaseJson.put("count", countStationName);
                    resultJsonArray.put(databaseJson);
                }
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                db.close();

        }

        return resultJsonArray;

    }
    @Override
    public JSONArray getBoardingDataListRouteNum(String month) {
        Log.d(StatusCode.TAG, "getBoardingDataListRouteNum: "+month);
        String[] split1 = month.split("\\월");
        String[] split2 = split1[0].split("\\분");
        String result = split2[0];
        String a;
        switch (result){
            case "1":
                a="'01%' OR current_data LIKE '02%' OR current_data LIKE '03%'";
                break;
            case "2":
                a="'04%' OR current_data LIKE '05%' OR current_data LIKE '06%'";
                break;
            case "3":
                a="'07%' OR current_data LIKE '08%' OR current_data LIKE '09%'";
                break;
            case "4":
                a="'10%' OR current_data LIKE '11%' OR current_data LIKE '12%'";
                break;
            default:
                a = "'"+result+"%"+"'";
                break;
        }
        db = getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT route_name, COUNT(*) FROM boarding_data GROUP BY route_name  HAVING current_data LIKE " + a;

            cursor = db.rawQuery(sql,null);

            if (cursor != null) {

                resultJsonArray = new JSONArray();
                while(cursor.moveToNext()){
                    String routeName = cursor.getString(0);
                    String countStationName = cursor.getString(1);
                    databaseJson = new JSONObject();
                    databaseJson.put("route_name", routeName);
                    databaseJson.put("count", countStationName);
                    resultJsonArray.put(databaseJson);

                }
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                db.close();

        }

        return resultJsonArray;

    }

    @Override
    public JSONArray getBoardingDataCount(String month) {
        String[] split1 = month.split("\\월");
        String[] split2 = split1[0].split("\\분");
        String result = split2[0];
        String a;
        switch (result){
            case "1":
                a="'01%' OR current_data LIKE '02%' OR current_data LIKE '03%'";
                break;
            case "2":
                a="'04%' OR current_data LIKE '05%' OR current_data LIKE '06%'";
                break;
            case "3":
                a="'07%' OR current_data LIKE '08%' OR current_data LIKE '09%'";
                break;
            case "4":
                a="'10%' OR current_data LIKE '11%' OR current_data LIKE '12%'";
                break;
            default:
                a = "'"+result+"%"+"'";
                break;
        }

        Log.d(StatusCode.TAG, "getBoardingDataCount: "+a);

        db = getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT route_name FROM boarding_data GROUP BY route_name  HAVING current_data LIKE " + a;

            cursor = db.rawQuery(sql,null);

            if (cursor != null) {
                resultJsonArray = new JSONArray();
                int count = cursor.getCount();
                databaseJson = new JSONObject();
                databaseJson.put("count", count);
                resultJsonArray.put(databaseJson);
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();

        } finally {
            if (cursor != null)
                db.close();

        }

        return resultJsonArray;

    }


    @Override
    public void deleteAlarmData(Integer pkNumber) {
        db = getWritableDatabase();

        String[] numArr = {pkNumber.toString()};

        db.delete(ALARM_DATA_TABLE, StatusCode.DB_PK_NUM + " = ?", numArr);

        db.close();
    }

    @Override
    public long getCount(){
        db = getReadableDatabase();

        return DatabaseUtils.queryNumEntries(db, ALARM_DATA_TABLE, null, null);
    }

    @Override
    public String SoundInfo(Integer no){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT SoundID, Volume FROM AlarmData WHERE _Num="+no+";",null);
        String sdInfo = "";
        if (cursor != null) {
            while (cursor.moveToNext())
                sdInfo += cursor.getInt(0) + "," + cursor.getInt(1);
        }

        db.close();

        return sdInfo;
    }

}