package kr.ac.gachon.bbangbbangclock.data;

import androidx.annotation.Nullable;

/**
 * 대중교통 정류소 데이터 클래스
 *
 * @since : 19-11-25
 * @author : 류일웅
 */
public class SearchStationData {

    private String stationClass;
    private String stationName;
    private String stationID;
    private String CID;
    private String arsID;
    private String cityName;
    private String laneName;
    private String localStationID;
    private String dong;

    public SearchStationData(
            String stationClass,
            String stationName,
            String stationID,
            String CID,
            String arsID,
            String cityName,
            @Nullable String laneName,
            @Nullable String localStationID,
            String dong
    ) {
        this.stationClass = stationClass;
        this.stationName = stationName;
        this.stationID = stationID;
        this.arsID = arsID;
        this.CID = CID;
        this.cityName = cityName;
        this.laneName = laneName;
        this.localStationID = localStationID;
        this.dong = dong;
    }

    public String getStationClass() {
        return stationClass;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationID() {
        return stationID;
    }

    public String getArsID() {
        return arsID;
    }

    public String getCID() {
        return CID;
    }

    public String getCityName() {
        return cityName;
    }

    public String getLaneName() {
        return laneName;
    }

    public String getLocalStationID() {
        return localStationID;
    }

    public String getDong() {
        return dong;
    }
}
