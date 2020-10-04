package kr.ac.gachon.bbangbbangclock.activity;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.common.ApiCall;
import kr.ac.gachon.bbangbbangclock.common.StatusCode;
import kr.ac.gachon.bbangbbangclock.dao.AlarmSQLiteDAO;
import kr.ac.gachon.bbangbbangclock.receiver.AlarmReceiver;
import kr.ac.gachon.bbangbbangclock.util.AlarmUtil;

public class AlarmActivity extends AppCompatActivity {

    @BindView(R.id.activity_alarm_logo_image)
    ImageView titleImage;
    @BindView(R.id.activity_alarm_weather_image)
    ImageView weatherImage;
    @BindView(R.id.activity_alarm_station_image)
    ImageView stationImage;
    @BindView(R.id.fragment_weather_crawl)
    TextView weatherCrawl;
    @BindView(R.id.activity_alarm_current_time)
    TextView currentTime;
    @BindView(R.id.activity_alarm_route_name)
    TextView routeName;
    @BindView(R.id.activity_alarm_arrived)
    TextView arriveTxt;
    @BindView(R.id.activity_alarm_after5_alarm)
    Button afterFiveMinutesAlarm;
    @BindView(R.id.activity_alarm_riding_alarm)
    Button ridingAlarm;
    @BindView(R.id.activity_alarm_end_alarm)
    Button endAlarm;

    private ApiCall apiCall;
    private MediaPlayer mediaPlayer;
    private AlarmSQLiteDAO alarmSQLiteDAO;
    private int alarmPK;
    private Map<String, String> busPredictRestTime;
    private Handler handler;
    private String cityName = "서울";
    private String weather = "날씨";
    private URL nWeather;
    private Intent intent;
    private Bundle bundle;
    private String showRouteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        ButterKnife.bind(this);

        alarmSQLiteDAO = new AlarmSQLiteDAO(this);
        busPredictRestTime = new HashMap<>();
        apiCall = new ApiCall();
        handler = new Handler();
        intent = getIntent();
        bundle = intent.getExtras();
        alarmPK = bundle.getInt(StatusCode.ALARM_PRIMARY_KEY_DATA);

        naverWeatherCrawling();
        showCurrentTime();

        //슬립모드에서 깨우기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        playMusic();

        afterFiveMinutesAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), AlarmReceiver.class);
                bundle = new Bundle();

                Log.d(StatusCode.TAG, "알람 액티비티: "+alarmPK);

                bundle.putInt(StatusCode.ALARM_RECEIVER_CODE_DATA, alarmPK);
                intent.putExtras(bundle);

                AlarmUtil.getInstance().setFiveMinuteAlarm(
                        AlarmActivity.this, intent, alarmPK);

                stopMusic();

                finish();
            }
        });

        ridingAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("MM-dd");
                String CurrentDate = simpleDate.format(mDate);
                String routeName = null;
                String stationName = null;
                alarmPK = bundle.getInt(StatusCode.ALARM_PRIMARY_KEY_DATA);

                try {
                    JSONArray getParam = alarmSQLiteDAO.getAlarmData(alarmPK);
                    JSONObject stationInfo = getParam.getJSONObject(0);
                    routeName = stationInfo.getString(StatusCode.DB_ROUTE_NAME);
                    stationName =  stationInfo.getString(StatusCode.DB_STATION_NAME);

                    String[] split1 = routeName.split(";");
                    routeName = split1[0];
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                alarmSQLiteDAO.insertBoardingData(CurrentDate, routeName, stationName);

                stopMusic();

                finish();
            }
        });

        endAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();

                finish();
            }
        });

        try {
            final JSONArray getParam = alarmSQLiteDAO.getAlarmData(alarmPK);
            final JSONObject stationInfo = getParam.getJSONObject(0);
            final String stationClass = stationInfo.getString(StatusCode.DB_STATION_CLASS);

            if (stationClass.equals("1"))
                searchBusRestTime(stationInfo);
            else if (stationClass.equals("2"))
                searchSubwayRestTime(stationInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopMusic();
    }

    /**
     * ##################################################
     * 현재 날씨 표현
     *
     * @since : 19-12-14
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void showCurrentTime() {
        SimpleDateFormat format1 = new SimpleDateFormat ( "HH:mm");
        Date now = new Date();
        String time1 = format1.format(now);

        currentTime.setText(time1);
    }

    /**
     * ##################################################
     * 네이버 날씨 크롤링
     *
     * @since : 19-12-14
     * @author : 이용성, 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void naverWeatherCrawling() {
        new Thread((Runnable) () -> {
            final StringBuilder builder = new StringBuilder();
            final StringBuilder urlBuilder = new StringBuilder();
            Elements docTemp;
            Elements docTemp2;
            Elements docWeather;
            String weatherStatusText = null;
            String weatherStatus;

            try {
                urlBuilder.append("https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query");
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(cityName,"UTF-8"));
                urlBuilder.append("+");
                urlBuilder.append(URLEncoder.encode(weather,"UTF-8"));
                nWeather = new URL(urlBuilder.toString());

                Document doc = Jsoup.connect(String.valueOf(nWeather)).get();

                docTemp = doc.select("div.info_data").select("p.info_temperature");

                for (Element link : docTemp)
                    builder.append("현재 온도는 ").append(link.text()).append(" ");

                docTemp2 = doc.select("div.info_data").select("ul.info_list").select("span.sensible");

                for (Element link : docTemp2)
                    builder.append(link.text()).append("C 입니다. \n");

                docWeather = doc.select("div.today_area._mainTabContent").select("div.info_data").select("p.cast_txt");

                for (Element link : docWeather) {
                    builder.append("현재 날씨는 ").append(link.text()).append("\n");
                    weatherStatusText = link.text();
                }
            } catch(IOException e) {
                builder.append("Error");
            }

            weatherStatus = weatherStatusText.split(",")[0];

            runOnUiThread(() -> {
                weatherCrawl.setText(builder.toString());

                switch (weatherStatus) {
                    case "맑음":
                        weatherImage.setImageResource(R.drawable.ic_sunny);

                        break;
                    case "구름많음":
                        weatherImage.setImageResource(R.drawable.ic_plenty_cloud);

                        break;
                    case "흐림":
                        weatherImage.setImageResource(R.drawable.ic_cloudy);

                        break;
                    case "비":
                        weatherImage.setImageResource(R.drawable.ic_rain);

                        break;
                    case "눈":
                        weatherImage.setImageResource(R.drawable.ic_snow);

                        break;
                }
            });
        }).start();
    }

    /**
     * ##################################################
     * 알람음 재생 세팅
     *
     * @since : 19-12-14
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void playMusic() {
        int soundId = 0;
        int volume = 0;

        // 플레이 할 때 가장 먼저 음악 중지 실행
        stopMusic();

        try {
            JSONArray getParam = alarmSQLiteDAO.getAlarmData(alarmPK);
            JSONObject stationInfo = getParam.getJSONObject(0);
            soundId = Integer.parseInt(stationInfo.getString(StatusCode.DB_SOUND_ID));
            volume = Integer.parseInt(stationInfo.getString(StatusCode.DB_VOLUME));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 기본 벨소리(알람)의 URI
        Uri alert = Uri.parse("android.resource://kr.ac.gachon.bbangbbangclock/"
                + SelectSoundActivity.soundFileArray[soundId]);
        // 1. MediaPlayer 객체 생성
        mediaPlayer = new MediaPlayer();

        try {
            // 2. 데이터 소스 설정 (인터넷에 있는 음악 파일도 가능함)
            if (soundId == 0)
                mediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            else
                mediaPlayer.setDataSource(this, alert);

            startAlarm(mediaPlayer, volume);
        } catch (Exception ex) {
            try {
                // MediaPlayer의 Error 상태 초기화
                mediaPlayer.reset();
                startAlarm(mediaPlayer, volume);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    /**
     * ##################################################
     * 알람을 소리 크기에 맞춰서 재생
     *
     * @since : 19-12-14
     * @author : 류일웅
     * @param : stationInfo
     * @return :
     * ##################################################
     */
    private void startAlarm(MediaPlayer player, int volume) throws java.io.IOException, IllegalArgumentException, IllegalStateException {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        // 현재 Alarm 볼륨 구함
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            // Alarm 볼륨 설정
//            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
            // 음악 반복 재생
            player.setLooping(true);
            // 3. 재생 준비
            player.prepare();
            // 4. 재생 시작
            player.start();
        }
    }

    /**
     * ##################################################
     * 알람 멈춤
     *
     * @since : 19-12-14
     * @author : 류일웅
     * @param : stationInfo
     * @return :
     * ##################################################
     */
    public void stopMusic() {
        if (mediaPlayer != null) {
            // 5. 재생 중지
            mediaPlayer.stop();
            // 6. MediaPlayer 리소스 해제
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * ##################################################
     * 버스 예상 도착시간 불러오기
     *
     * @since : 19-12-14
     * @author : 류일웅
     * @param : stationInfo
     * @return :
     * ##################################################
     */
    private void searchBusRestTime(JSONObject stationInfo) {
        new Thread(new Runnable() {
            boolean isSeoul = true;

            @Override
            public void run() {
                try {
                    final String CID = stationInfo.getString(StatusCode.DB_CID);

                    if (CID.equals("1000")) {
                        final String arsId = stationInfo.getString(StatusCode.DB_ARS_ID).replace("-", "");
                        final String routeNameText = stationInfo.getString(StatusCode.DB_ROUTE_NAME);
                        String routeName = null;

                        if (routeNameText.contains(";"))
                            routeName = routeNameText.split(";")[0];
                        if (routeNameText.contains(" "))
                            routeName = routeNameText.split(" ")[0];

                        final String busNumber = routeName;
                        showRouteName = busNumber;

                        busPredictRestTime = apiCall.getSeoulBusRestTime(arsId, busNumber);
                    }
                    else {
                        String localStationId = stationInfo.getString(StatusCode.DB_LOCAL_STATION_ID);
                        String routeId = stationInfo.getString(StatusCode.DB_ROUTE_ID);
                        final String routeNameText = stationInfo.getString(StatusCode.DB_ROUTE_NAME);
                        String routeName = null;
                        isSeoul = false;

                        if (routeNameText.contains(";"))
                            routeName = routeNameText.split(";")[0];
                        if (routeNameText.contains(" "))
                            routeName = routeNameText.split(" ")[0];

                        showRouteName = routeName;

                        Log.d(StatusCode.TAG, "알람 액티비티 경기도: "+localStationId+","+routeId);

                        busPredictRestTime = apiCall.getGyeonggidoBusRestTime(localStationId, routeId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!busPredictRestTime.isEmpty()) {
                            stationImage.setImageResource(R.drawable.ic_bus);
                            routeName.setText(showRouteName);
                            routeName.append("번");

                            if (isSeoul) {
                                arriveTxt.setText("첫 번째 도착 예정: ");
                                arriveTxt.append(busPredictRestTime.get(StatusCode.BUS_FIRST_REST_TIME));

                                if (busPredictRestTime.containsKey(StatusCode.BUS_SECOND_REST_TIME))
                                    arriveTxt.append("\n두 번째 도착 예정: ");
                                    arriveTxt.append(busPredictRestTime.get(StatusCode.BUS_SECOND_REST_TIME));
                            }
                            else {
                                arriveTxt.setText("첫 번째 도착 예정: ");
                                arriveTxt.append(busPredictRestTime.get(StatusCode.BUS_FIRST_REST_TIME));
                                arriveTxt.append("분 전");

                                if (busPredictRestTime.containsKey(StatusCode.BUS_SECOND_REST_TIME)) {
                                    arriveTxt.append("\n두 번째 도착 예정: ");
                                    arriveTxt.append(busPredictRestTime.get(StatusCode.BUS_SECOND_REST_TIME));
                                    arriveTxt.append("분 전");
                                }
                            }
                        }
                        else
                            arriveTxt.setText("버스 도착 정보 없음");
                    }
                });
            }
        }).start();
    }

    /**
     * ##################################################
     * 지하철 시간표 불러오기
     *
     * @since : 19-12-14
     * @author : 류일웅
     * @param : stationInfo
     * @return :
     * ##################################################
     */
    private void searchSubwayRestTime(JSONObject stationInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String stationId = stationInfo.getString(StatusCode.DB_STATION_ID);
                    final String direction = stationInfo.getString(StatusCode.DB_SUBWAY_DIRECTION);
                    final String hour = stationInfo.getString(StatusCode.DB_HOUR);
                    final String routeNameInDB = stationInfo.getString(StatusCode.DB_ROUTE_NAME);
                    showRouteName = routeNameInDB.replace(";", " ");

                    Log.d(StatusCode.TAG, "알람 액티비티: 지하철 id: "+stationId);

                    String subwayTimeTable = apiCall.searchSubwayTimeTable(
                            AlarmActivity.this, stationId, direction);

                    JSONArray resultTime = findSubwayRestTime(subwayTimeTable, hour);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                stationImage.setImageResource(R.drawable.ic_subway);
                                routeName.setText(showRouteName);

                                for (int i = 0; i < resultTime.length(); i++) {
                                    JSONObject jsonObject = resultTime.getJSONObject(i);
                                    String time = jsonObject.getString("Idx");
                                    String list = jsonObject.getString("list");

                                    arriveTxt.append(time + "시: " + list + "\n");
                                }

                                arriveTxt.setTextSize(14);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * ##################################################
     * 설정한 시간과 1시간 후의 시간표 불러오기
     *
     * @since : 19-12-14
     * @author : 류일웅
     * @param : timeTable, hour
     * @return :
     * ##################################################
     */
    private JSONArray findSubwayRestTime(String timeTable, String hour) {
        JSONObject resultTime = new JSONObject();
        JSONArray resultTimeArray = new JSONArray();

        try {
            JSONObject subwayTimeTable = new JSONObject(timeTable);
            JSONArray timeTableArray = subwayTimeTable.getJSONArray("subwayTimeTable");
            JSONObject timeTableData = null;
            String timeTableHour = null;

            for (int i=0; i<timeTableArray.length(); i++) {
                timeTableData = timeTableArray.getJSONObject(i);
                timeTableHour = timeTableData.getString("Idx");

                if (hour.equals(timeTableHour)) {
                    resultTime = timeTableData;
                    resultTimeArray.put(resultTime);

                    if (i+1 < timeTableArray.length()) {
                        resultTime = timeTableArray.getJSONObject(i+1);
                        resultTimeArray.put(resultTime);

                        break;
                    }

                    break;
                }
            }

            if (resultTime.length() == 0) {
                timeTableData = timeTableArray.getJSONObject(0);

                resultTimeArray.put(timeTableData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultTimeArray;
    }

}

