package kr.ac.gachon.bbangbbangclock.dao;

import android.content.Intent;

import org.json.JSONArray;

/**
 * 알람 DAO
 *
 * @since : 19-11-25
 * @author : 빵빵시게팀
 */
public interface AlarmDAO {

    void insertAlarmData(
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
            Integer volume
    );
    void updateAlarmData(
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
            Integer volume
    );
    void insertBoardingData(
            String currentDate,
            String routeName,
            String stationName
    );

    void updateChecked(Integer pkNumber, Integer checked);

    JSONArray getAlarmData(Integer pkNumber);
    JSONArray getAlarmDataList();

    JSONArray getBoardingDataListStName(String month);
    JSONArray getBoardingDataListRouteNum(String month);
    JSONArray getBoardingDataCount(String month);

    long getCount();

    void deleteAlarmData(Integer pkNumber);

    String SoundInfo(Integer pkNumber);

}
