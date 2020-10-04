package kr.ac.gachon.bbangbbangclock.common;

/**
 * 공용으로 사용되는 코드 모음
 *
 * @since : 19-11-25
 * @author : 빵빵시게팀
 */
public class StatusCode {

    // 액티비티 상태 코드
    public static final int SEARCH_STATION = 1000;
    public static final int EDIT_STATION = 1001;
    public static final int SET_SOUND = 1002;

    // 대중교통 정류장 코드
    public static final int SELECT_BUS = 100;
    public static final int SELECT_SUBWAY = 101;

    // 로그 태그
    public static final String TAG = "ANDROID_LOG";

    // Intent 키
    public static final String BUS_LANE_DATA = "busLaneData";
    public static final String BUS_CLASS_DATA = "busCodeData";
    public static final String SUBWAY_STATION_DATA = "subwayData";
    public static final String SUBWAY_CLASS_DATA = "subwayCodeData";
    public static final String STATION_ID_DATA = "stationIdData";
    public static final String LOCAL_STATION_ID_DATA = "localStationIdData";
    public static final String BUS_ROUTE_ID_DATA = "busLocalBlIdData";
    public static final String SOUND_INDEX_DATA = "soundIndexData";
    public static final String SOUND_NAME_DATA = "soundNameData";
    public static final String DATABASE_INDEX_DATA = "databaseIndexData";
    public static final String ALARM_SETTING_STATUS_DATA = "alarmSettingStatusData";
    public static final String STATION_CLASS_DATA = "stationClassData";
    public static final String ALARM_RECEIVER_CODE_DATA = "alarmReceiverCodeData";
    public static final String ALARM_PRIMARY_KEY_DATA = "alarmPrimaryKeyData";
    public static final String STATION_ARSID_DATA = "stationArsIdData";
    public static final String STATION_CID_DATA = "stationCIDData";
    public static final String STATION_CITY_NAME_DATA = "stationCityNameData";
    public static final String STATION_DIRECTION_DATA = "stationDirectionData";
    public static final String SEARCH_STATION_ACTIVITY_STATUS_DATA = "searchStationActivityStatusData";

    // 도착 정보 키
    public static final String BUS_FIRST_REST_TIME = "busFirstRestTime";
    public static final String BUS_SECOND_REST_TIME = "busSecondRestTime";

    // 데이터베이스 column
    // alarm_data 테이블
    public static final String DB_PK_NUM = "pk_num";
    public static final String DB_HOUR = "hour";
    public static final String DB_MINUTE = "minute";
    public static final String DB_STATION_CLASS = "station_class";
    public static final String DB_STATION_ID = "station_id";
    public static final String DB_STATION_NAME = "station_name";
    public static final String DB_CID = "cid";
    public static final String DB_ARS_ID = "ars_id";
    public static final String DB_CITY_NAME = "city_name";
    public static final String DB_ROUTE_ID = "route_id";
    public static final String DB_ROUTE_NAME = "route_name";
    public static final String DB_LOCAL_STATION_ID = "local_station_id";
    public static final String DB_SUBWAY_DIRECTION = "subway_direction";
    public static final String DB_DAY = "day";
    public static final String DB_CHECKED = "checked";
    public static final String DB_SOUND_ID = "sound_id";
    public static final String DB_VOLUME = "volume";
    // boarding_data 테이블
    public static final String DB_CURRENT_DATA = "current_data";
    // alarm_uuid_data 테이블
    public static final String DB_UUID_DATA = "alarm_uuid";

}
