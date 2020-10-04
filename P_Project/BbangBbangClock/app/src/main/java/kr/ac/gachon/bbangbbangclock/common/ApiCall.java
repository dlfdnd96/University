package kr.ac.gachon.bbangbbangclock.common;

import android.content.Context;
import android.util.Log;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 각 필요한 정보를 가져오는 API 모음
 *
 * @since : 19-11-25
 * @author : 류일웅
 */
public class ApiCall {

    // TODO: API키는 따로 json 파일로 만들어서 보관하기
    private String ODSAY_API_KEY = "hZUcv6evNgrFVWtg7GODgb+dGzk1auC1ds1o+FCZBUI";
    private String DATAGO_API_KEY = "reBdvs2h3zgQogrTMpQfSMDjf4dP0elrV1eMUXhlRVomJOa7FstIczHf12Nb4v%2FX6JfMhe574tzYo9xfdfRJBA%3D%3D";
    private ODsayService odsayService = null;
    private String requestType = "";
    private String returnData = "";
    // API 수행 동작 상태
    private boolean lockState;

    /*
     * ##################################################
     * ODSay API
     * ##################################################
     */
    /**
     * ##################################################
     * 대중교통 정류장 API 호출
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : context, stationString
     * @return : returnData
     * ##################################################
     */
    public String searchStation(Context context, String stationString) {
        setOdasyInitialize(context, "searchStation");

        // API 호출
        odsayService.requestSearchStation(stationString,
                "",
                "",
                "",
                "",
                "",
                onResultCallbackListener);

        dataProcessing();

        return returnData;
    }

    /**
     * ##################################################
     * 버스 노선정보 API 호출
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : context, stationString
     * @return : returnData
     * ##################################################
     */
    public String searchBusLane(Context context, String stationId) {
        setOdasyInitialize(context, "busStationInfo");

        odsayService.requestBusStationInfo(stationId, onResultCallbackListener);

        dataProcessing();

        return returnData;
    }

    /**
     * ##################################################
     * 지하철 정보 API 호출
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : context, stationString
     * @return : returnData
     * ##################################################
     */
    public String searchSubwayInfo(Context context, String stationString) {
        setOdasyInitialize(context, "subwayStationInfo");

        // API 호출
        odsayService.requestSubwayStationInfo(stationString, onResultCallbackListener);

        dataProcessing();

        return returnData;
    }

    /**
     * ##################################################
     * 지하철 시간표 API 호출
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : context, stationId, wayCode
     * @return : returnData
     * ##################################################
     */
    public String searchSubwayTimeTable(Context context, String stationId, String wayCode) {
        setOdasyInitialize(context, "subwayTimeTable");

        // API 호출
        odsayService.requestSubwayTimeTable(stationId, wayCode, onResultCallbackListener);

        dataProcessing();

        return returnData;
    }

    /**
     * ##################################################
     * ODsay API 및 필요 변수 초기화
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void setOdasyInitialize(Context context, String requestAPI) {
        ODsayApiCall(context);
        requestType = requestAPI;
        lockState = true;
    }

    /**
     * ##################################################
     * ODsay API 설정
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : context
     * @return :
     * ##################################################
     */
    private void ODsayApiCall(Context context) {
        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService = ODsayService.init(context, ODSAY_API_KEY);
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);
    }

    /**
     * ##################################################
     * API 수행 동작 상태를 처리
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void dataProcessing() {
        while (true) {
            try {
                // API 호출이 다 끝났으면 반복문 탈출
                if (!lockState) break;

                // 0.1초 단위로 실행. 이렇게 하지 않으면 에러 발생
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ##################################################
     * API 콜백 함수
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        // 호출 성공 시 실행
        @Override
        public void onSuccess(ODsayData odsayData, API api) {
            switch (requestType) {
                case "searchStation":
                    returnData = setStationDataToJson(odsayData, api);
                    break;
                case "busStationInfo":
                    returnData = setBusLaneDataToJson(odsayData, api);
                    break;
                case "subwayStationInfo":
                    returnData = setSubwayNearbyStationDataToJson(odsayData, api);
                    break;
                case "subwayTimeTable":
                    returnData = setSubwayTimeTableToJson(odsayData, api);
            }

            lockState = false;
        }

        // 호출 실패 시 실행
        @Override
        public void onError(int i, String s, API api) {
            Log.d(StatusCode.TAG, "Android: error: "+i+","+s+","+api);
        }
    };

    /**
     * ##################################################
     * 대중교통 정류장 정보를 JSON 형식으로 변환
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : odsayData, api
     * @return : jsonString
     * ##################################################
     */
    private String setStationDataToJson(ODsayData odsayData, API api) {
        final String[] requestField = {
                "stationClass", "stationName", "stationID", "CID", "arsID", "cityName", "dong"
        };
        String jsonString = "{" + "station" + " : " + "[{";

        try {
            // API Value 는 API 호출 메소드 명을 따라갑니다.
            // JSON 형식 참고 URL: https://lab.odsay.com/guide/console#searchStation
            if (api == API.SEARCH_STATION) {
                final JSONObject jsonObject = odsayData.getJson().getJSONObject("result");
                final String station = jsonObject.getString("station");
                final JSONArray jsonArray = new JSONArray(station);

                jsonString = addStationDataToJsonString(jsonArray, requestField, jsonString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * ##################################################
     * 버스 노선정보를 JSON 형식으로 변환
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : odsayData, api
     * @return : jsonString
     * ##################################################
     */
    // 아래 함수 전체 구조는 setStationDataToJson() 와 동일함. by 류일웅
    private String setBusLaneDataToJson(ODsayData odsayData, API api) {
        final String[] requestField = {"busNo", "busStartPoint", "busEndPoint", "busLocalBlID"};
        String jsonString = "{" + "lane" + " : " + "[{";

        try {
            if (api == API.BUS_STATION_INFO) {
                // JSON 형식 참고 URL: https://lab.odsay.com/guide/console#busStationInfo
                final JSONObject jsonObject = odsayData.getJson().getJSONObject("result");
                final String busLane = jsonObject.getString("lane");
                final JSONArray jsonArray = new JSONArray(busLane);

                jsonString = addBusLaneDataToJsonString(jsonArray, requestField, jsonString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * ##################################################
     * 지하철 이전역, 다음역 정보를 JSON 형식으로 변환
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : odsayData, api
     * @return : jsonString
     * ##################################################
     */
    // 아래 함수 전체 구조는 setStationDataToJson() 와 동일함. by 류일웅
    private String setSubwayNearbyStationDataToJson(ODsayData odsayData, API api) {
        final String[] requestField = {"stationName", "stationID", "laneName"};
        final String[] nearbyField = {"prevOBJ", "nextOBJ"};
        String jsonString = "{" + "subwayInfo" + " : " + "[{";

        try {
            if (api == API.SUBWAY_STATION_INFO) {
                // JSON 형식 참고 URL: https://lab.odsay.com/guide/console#subwayStationInfo
                final JSONObject jsonObject = odsayData.getJson().getJSONObject("result");

                for (int i=0; i<nearbyField.length; i++) {
                    final JSONObject nearbyStation = jsonObject.getJSONObject(nearbyField[i]);

                    if (nearbyStation.length() == 0)
                        continue;

                    final String station = nearbyStation.getString("station");
                    final JSONArray jsonArray = new JSONArray(station);

                    jsonString = addSubwayDataToJsonString(jsonArray, requestField, jsonString, i);
                }

                jsonString = jsonString.substring(0, jsonString.length() - 3);
                jsonString += "]" + "}";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * ##################################################
     * 지하철 시간표 정보를 JSON 형식으로 변환
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : odsayData, api
     * @return : jsonString
     * ##################################################
     */
    private String setSubwayTimeTableToJson(ODsayData odsayData, API api) {
        Date currentTime = new Date();
        String dateText = new SimpleDateFormat("EE요일", Locale.getDefault()).format(currentTime);

        final String[] requestField = {"Idx", "list"};
        String jsonString = "{" + "subwayTimeTable" + " : " + "[{";

        try {
            if (api == API.SUBWAY_TIME_TABLE) {
                // JSON 형식 참고 URL: https://lab.odsay.com/guide/console#subwayTimeTable
                final JSONObject jsonObject = odsayData.getJson().getJSONObject("result");
                JSONObject ordList;

                if (dateText.equals("Sat요일"))
                    ordList = jsonObject.getJSONObject("SatList");
                else if (dateText.equals("Sun요일"))
                    ordList = jsonObject.getJSONObject("SunList");
                else
                    ordList = jsonObject.getJSONObject("OrdList");

                final JSONObject direction = ordList.has("up")
                        ? ordList.getJSONObject("up") : ordList.getJSONObject("down");
                final String time = direction.getString("time");
                final JSONArray jsonArray = new JSONArray(time);

                jsonString = addSubwayTimeTableDataToJsonString(jsonArray, requestField, jsonString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * ##################################################
     * 대중교통 정류장의 정보를 JSON 배열 형식으로 저장
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : jsonArray, requestField, jsonString
     * @return : jsonString
     * ##################################################
     */
    private String addStationDataToJsonString(
            JSONArray jsonArray,
            String[] requestField,
            String jsonString
    ) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject subJsonObject = jsonArray.getJSONObject(i);

                // 경기도, 서울 지역만 표시
                if (Integer.parseInt(subJsonObject.getString("CID")) >= 3000)
                    continue;

                // 지하철역이면 노선이름까지 저장
                if (Integer.parseInt(subJsonObject.getString("stationClass")) == 2) {
                    for (int j = 0; j < requestField.length; j++) {
                        jsonString += "'"
                                + requestField[j]
                                + "'"
                                + " : "
                                + "'"
                                + subJsonObject.getString(requestField[j])
                                + "',";
                    }

                    jsonString += "'laneName' : '";
                    jsonString += subJsonObject.getString("laneName") + "',";
                }
                else {
                    for (int j = 0; j < requestField.length; j++) {
                        jsonString += "'"
                                + requestField[j]
                                + "'"
                                + " : "
                                + "'"
                                + subJsonObject.getString(requestField[j])
                                + "',";
                    }

                    if (!subJsonObject.has("localStationID")) {
                        jsonString = jsonString.substring(0, jsonString.length() - 1);
                        jsonString += "}, {";

                        continue;
                    }

                    jsonString += "'localStationID' : '";
                    jsonString += subJsonObject.getString("localStationID") + "',";
                }

                jsonString = jsonString.substring(0, jsonString.length() - 1);
                jsonString += "}, {";
            }

            /*
            배열의 끝부분을 제대로 완성시켜줌
            예: example:{[{ex1, ex2, ...}, {ex3, ex4, ...}, {
             => example:{[{ex1, ex2, ...}, {ex3, ex4, ...}]}
            */
            jsonString = jsonString.substring(0, jsonString.length() - 3);
            jsonString += "]" + "}";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * ##################################################
     * 버스 노선정보를 JSON 배열 형식으로 저장
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : jsonArray, requestField, jsonString
     * @return : jsonString
     * ##################################################
     */
    // 아래 함수 구조는 addStationDataToJsonString()와 동일함. by 류일웅
    private String addBusLaneDataToJsonString(
            JSONArray jsonArray,
            String[] requestField,
            String jsonString
    ) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject subJsonObject = jsonArray.getJSONObject(i);

                for (int j = 0; j < requestField.length; j++) {
                    jsonString += "'"
                            + requestField[j]
                            + "'"
                            + " : "
                            + "'"
                            + subJsonObject.getString(requestField[j])
                            + "',";
                }

                jsonString = jsonString.substring(0, jsonString.length() - 1);
                jsonString += "}, {";
            }

            jsonString = jsonString.substring(0, jsonString.length() - 3);
            jsonString += "]" + "}";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * ##################################################
     * 지하철 정보를 JSON 배열 형식으로 저장
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : jsonArray, requestField, jsonString, index
     * @return : jsonString
     * ##################################################
     */
    // 아래 함수 구조는 addStationDataToJsonString()와 동일함. by 류일웅
    private String addSubwayDataToJsonString(
            JSONArray jsonArray,
            String[] requestField,
            String jsonString,
            int index
    ) {
        try {
            final String[] directionField = {"1", "2"};

            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject subJsonObject = jsonArray.getJSONObject(i);

                for (int j = 0; j < requestField.length; j++) {
                    jsonString += "'"
                            + requestField[j]
                            + "'"
                            + " : "
                            + "'"
                            + subJsonObject.getString(requestField[j])
                            + "',";
                }

                jsonString += "'direction'" + " : " + "'" + directionField[index] + "',";

                jsonString = jsonString.substring(0, jsonString.length() - 1);
                jsonString += "}, {";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * ##################################################
     * 지하철 시간표 정보를 JSON 배열 형식으로 저장
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : jsonArray, requestField, jsonString
     * @return : jsonString
     * ##################################################
     */
    private String addSubwayTimeTableDataToJsonString(
            JSONArray jsonArray,
            String[] requestField,
            String jsonString
    ) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject subJsonObject = jsonArray.getJSONObject(i);

                for (int j = 0; j < requestField.length; j++) {
                    jsonString += "'"
                            + requestField[j]
                            + "'"
                            + " : "
                            + "'"
                            + subJsonObject.getString(requestField[j])
                            + "',";
                }

                jsonString = jsonString.substring(0, jsonString.length() - 1);
                jsonString += "}, {";
            }

            jsonString = jsonString.substring(0, jsonString.length() - 3);
            jsonString += "]" + "}";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /*
     * ##################################################
     * 공공데이터포털 API
     * ##################################################
     */
    /**
     * ##################################################
     * 경기도 버스 도착 정보를 가져옴
     *
     * @since : 19-12-10
     * @author : 류일웅
     * @param : localStationId, routeId
     * @return : busPredictRestTimeMap
     * ##################################################
     */
    public Map<String, String> getGyeonggidoBusRestTime(String localStationId, String routeId) {
        BufferedReader rd = null;
        HttpURLConnection conn = null;
        Map<String, String> busPredictRestTimeMap = new HashMap<>();

        try {
            StringBuilder urlBuilder = new StringBuilder(
                    "http://openapi.gbis.go.kr/ws/rest/busarrivalservice");
            urlBuilder.append("?");
            urlBuilder.append(URLEncoder.encode("serviceKey","UTF-8"));
            urlBuilder.append("=");
            urlBuilder.append(DATAGO_API_KEY);
            urlBuilder.append("&");
            urlBuilder.append(URLEncoder.encode("stationId","UTF-8"));
            urlBuilder.append("=");
            urlBuilder.append(localStationId);
            urlBuilder.append("&");
            urlBuilder.append(URLEncoder.encode("routeId","UTF-8"));
            urlBuilder.append("=");
            urlBuilder.append(routeId);

            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            else
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null)
                sb.append(line);

            busPredictRestTimeMap = parseXMLGyeonggido(sb);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rd != null)
                    rd.close();
                if (conn != null)
                    conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return busPredictRestTimeMap;
    }

    /**
     * ##################################################
     * 경기도 버스 도착 정보 데이터(XML)를 parsing
     *
     * @since : 19-12-10
     * @author : 류일웅
     * @param : sb
     * @return : restTimeMap
     * ##################################################
     */
    private Map<String, String> parseXMLGyeonggido(StringBuilder sb) {
        final int STEP_NONE = 0;
        final int STEP_predictTime1 = 1;
        final int STEP_predictTime2 = 2;
        Map<String, String> restTimeMap = new HashMap<>();

        try {
            String predictTime1 = null;
            String predictTime2 = null;

            // XML 파서 초기화
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            parser.setInput(new StringReader(sb.toString()));

            int eventType = parser.getEventType();
            int step = STEP_NONE;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    // XML 데이터 시작
                }
                else if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();

                    if (startTag.equals("predictTime1"))
                        step = STEP_predictTime1;
                    else if (startTag.equals("predictTime2"))
                        step = STEP_predictTime2;
                    else
                        step = STEP_NONE;
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    String endTag = parser.getName();

                    if ((endTag.equals("predictTime1") && step != STEP_predictTime1) ||
                            (endTag.equals("predictTime2") && step != STEP_predictTime2))
                        Log.d(StatusCode.TAG, "LOG: XML END TAG Error");

                    step = STEP_NONE;
                }
                else if (eventType == XmlPullParser.TEXT) {
                    String text = parser.getText();
                    if (step == STEP_predictTime1)
                        predictTime1 = text;
                    else if (step == STEP_predictTime2)
                        predictTime2 = text;
                }

                eventType = parser.next();
            }

            if (predictTime1 == null) {
                Log.d(StatusCode.TAG, "LOG: XML Error");
                // ERROR : XML is invalid.
            } else {
                restTimeMap.put(StatusCode.BUS_FIRST_REST_TIME, predictTime1);

                if (predictTime2 != null)
                    restTimeMap.put(StatusCode.BUS_SECOND_REST_TIME, predictTime2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return restTimeMap;
    }

    /**
     * ##################################################
     * 서울시 버스 도착 정보를 가져옴
     *
     * @since : 19-12-10
     * @author : 류일웅
     * @param : arsId
     * @return :
     * ##################################################
     */
    public Map<String, String> getSeoulBusRestTime(String arsId, String busNumber) {
        BufferedReader rd = null;
        HttpURLConnection conn = null;
        Map<String, String> busPredictRestTimeMap = new HashMap<>();

        try {
            StringBuilder urlBuilder = new StringBuilder(
                    "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid");
            urlBuilder.append("?");
            urlBuilder.append(URLEncoder.encode("serviceKey","UTF-8"));
            urlBuilder.append("=");
            urlBuilder.append(DATAGO_API_KEY);
            urlBuilder.append("&");
            urlBuilder.append(URLEncoder.encode("arsId","UTF-8"));
            urlBuilder.append("=");
            urlBuilder.append(arsId);

            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            else
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null)
                sb.append(line);

            busPredictRestTimeMap = parseXMLSeoul(sb, busNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rd != null)
                    rd.close();
                if (conn != null)
                    conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return busPredictRestTimeMap;
    }

    /**
     * ##################################################
     * 서울 버스 도착 정보 데이터(XML)를 parsing
     *
     * @since : 19-12-10
     * @author : 류일웅
     * @param : sb
     * @return : restTimeMap
     * ##################################################
     */
    private Map<String, String> parseXMLSeoul(StringBuilder sb, String busNumber) {
        final int STEP_NONE = 0;
        final int STEP_arrmsgSec1 = 1;
        final int STEP_arrmsgSec2 = 2;
        final int STEP_rtNm = 3;
        Map<String, String> restTimeMap = new HashMap<>();

        try {
            String arrmsgSec1 = null;
            String arrmsgSec2 = null;
            String rtNm = null;

            // XML 파서 초기화
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            parser.setInput(new StringReader(sb.toString()));

            int eventType = parser.getEventType();
            int step = STEP_NONE;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    // XML 데이터 시작
                }
                else if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();

                    if (startTag.equals("arrmsgSec1"))
                        step = STEP_arrmsgSec1;
                    else if (startTag.equals("arrmsgSec2"))
                        step = STEP_arrmsgSec2;
                    else if (startTag.equals("rtNm"))
                        step = STEP_rtNm;
                    else
                        step = STEP_NONE;
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    String endTag = parser.getName();

                    if ((endTag.equals("predictTime1") && step != STEP_arrmsgSec1) ||
                            (endTag.equals("predictTime2") && step != STEP_arrmsgSec2) ||
                            (endTag.equals("rtNm") && step != STEP_rtNm))
                        Log.d(StatusCode.TAG, "LOG: XML END TAG Error");

                    step = STEP_NONE;
                }
                else if (eventType == XmlPullParser.TEXT) {
                    String text = parser.getText();

                    if (step == STEP_arrmsgSec1) {
                        arrmsgSec1 = text;
                    }
                    else if (step == STEP_arrmsgSec2) {
                        arrmsgSec2 = text;
                    }
                    else if (step == STEP_rtNm) {
                        rtNm = text;

                        if (busNumber.equals(rtNm)) {
                            restTimeMap.put(StatusCode.BUS_FIRST_REST_TIME, arrmsgSec1);

                            if (arrmsgSec2 != null)
                                restTimeMap.put(StatusCode.BUS_SECOND_REST_TIME, arrmsgSec2);
                        }
                    }
                }

                eventType = parser.next();
            }

            if (arrmsgSec1 == null || rtNm == null) {
                Log.d(StatusCode.TAG, "LOG: XML Error");
                // ERROR : XML is invalid.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return restTimeMap;
    }

}