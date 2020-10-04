package kr.ac.gachon.bbangbbangclock.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.common.StatusCode;
import kr.ac.gachon.bbangbbangclock.dao.AlarmSQLiteDAO;

/**
 * 알람 설정 액티비티
 *
 * seekbar 사용 시 SeekBarCompat 라이브러리 사용함.
 * 참고 URL: https://github.com/ahmedrizwan/SeekBarCompat
 *
 * @since : 19-11-25
 * @author : 빵빵시게팀
 */
public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.activity_setting_time_picker)
    TimePicker timePicker;
    @BindView(R.id.activity_setting_station_location)
    TextView stationLocationTextView;
    @BindView(R.id.activity_setting_station_direction)
    TextView stationDirectionTextView;
    @BindView(R.id.activity_setting_sound_info)
    TextView soundInfo;
    @BindView(R.id.material_seekBar)
    SeekBar seekBar;
    @BindView(R.id.sunday)
    ToggleButton Sun;
    @BindView(R.id.monday)
    ToggleButton Mon;
    @BindView(R.id.tuesday)
    ToggleButton Tue;
    @BindView(R.id.wednesday)
    ToggleButton Wed;
    @BindView(R.id.thursday)
    ToggleButton Thu;
    @BindView(R.id.friday)
    ToggleButton Fri;
    @BindView(R.id.saturday)
    ToggleButton Sat;
    @BindView(R.id.activity_setting_sound_play)
    ImageButton soundPlay;
    @BindView(R.id.activity_setting_sound_pause)
    ImageButton pause;
    @BindView(R.id.activity_setting_cancel)
    Button CanSet;
    @BindView(R.id.activity_setting_save)
    Button SavSet;
    @BindView(R.id.activity_setting_station_layout)
    View stationLayout;
    @BindView(R.id.activity_setting_sound_layout)
    View soundLayout;

    private Intent intent;
    private Bundle bundle;
    private String settingStatus;
    private AlarmManager am;
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private String stationName, stationClass, busNumOrSubwayDirection, stationId, CID, arsId,
            cityName, localStationId = null, routeId = null, subwayDirection = null;
    private Integer hour, minute, pkNumber, soundId, volumeControl;
    private AlarmSQLiteDAO alarmSQLiteDAO;
    private final String[] WEEK_NAME = {"일", "월", "화", "수", "목", "금", "토"};
    private String day = "";

    @OnClick({R.id.activity_setting_station_layout, R.id.activity_setting_sound_layout
            , R.id.activity_setting_sound_play, R.id.activity_setting_sound_pause
            , R.id.activity_setting_cancel, R.id.activity_setting_save})
    public void onViewClicked(View view) {
        switch(view.getId()){
            case R.id.activity_setting_station_layout:
                setAlarmConfigure(SearchStationActivity.class, StatusCode.SEARCH_STATION);

                break;
            case R.id.activity_setting_sound_layout:
                setAlarmConfigure(SelectSoundActivity.class, StatusCode.SET_SOUND);

                break;
            case R.id.activity_setting_sound_play:
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                soundPlay.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);

                break;
            case R.id.activity_setting_sound_pause:
                mediaPlayer.pause();
                pause.setVisibility(View.INVISIBLE);
                soundPlay.setVisibility(View.VISIBLE);

                break;
            case R.id.activity_setting_cancel:
                setResult(Activity.RESULT_CANCELED);
                stopPlaying();
                finish();

                break;
            case R.id.activity_setting_save:
                intent = getIntent();

                stopPlaying();
                registerAlarm(view);

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        bundle = new Bundle();
        am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        //알람음, 음량 설정
        audioManager.setSpeakerphoneOn(true);

        checkSettingStatus();

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int min) {
                hour = hourOfDay;
                minute = min;
            }
        });

        pause.setVisibility(View.INVISIBLE);
        soundPlay.setVisibility(View.VISIBLE);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,i,AudioManager.FLAG_PLAY_SOUND);
                volumeControl = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pause.setVisibility(View.INVISIBLE);
                soundPlay.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case StatusCode.SEARCH_STATION:
                // 사용자가 선택한 버스 혹은 지하철 정보를 설정창에서 표시
                if (resultCode == StatusCode.SELECT_BUS) {
                    showSelectedBusStation(data);
                }
                else if (resultCode == StatusCode.SELECT_SUBWAY) {
                    showSelectedSubwayStation(data);
                }

                break;
            case StatusCode.SET_SOUND:
                if (resultCode == RESULT_OK) {
                    bundle = data.getExtras();

                    soundId = bundle.getInt(StatusCode.SOUND_INDEX_DATA, 0);
                    final String soundName = bundle.getString(StatusCode.SOUND_NAME_DATA);

                    soundInfo.setText(soundName);

                    try {
                        mediaPlayer.reset();

                        if (soundId == 0)
                            mediaPlayer.setDataSource(getApplicationContext(),
                                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                        else {
                            Uri uri = Uri.parse("android.resource://kr.ac.gachon.bbangbbangclock/"
                                    + SelectSoundActivity.soundFileArray[soundId]);
                            mediaPlayer.setDataSource(getApplicationContext(), uri);
                        }
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                }
        }
    }

    /**
     * ##################################################
     * 알람이 추가인지 편집인지에 따른 초기 화면 설정
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void checkSettingStatus() {
        intent = getIntent();
        bundle = intent.getExtras();
        pkNumber = bundle.getInt(StatusCode.DATABASE_INDEX_DATA);
        settingStatus = bundle.getString(StatusCode.ALARM_SETTING_STATUS_DATA);

        if (settingStatus.equals("edit"))
            editAlarm(this);
        else if (settingStatus.equals("add")) {
            hour = 8;
            minute = 0;
            timePicker.setHour(hour);
            timePicker.setMinute(minute);

            final String station = bundle.getString(StatusCode.STATION_CLASS_DATA);

            if (station.equals("bus")) {
                showSelectedBusStation(intent);
            }
            else if (station.equals("subway")) {
                showSelectedSubwayStation(intent);
            }

            // 추가할때는 디폴트 알람음으로 초기화
            mediaPlayer = MediaPlayer.create(
                    getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            );

            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    AudioManager.FLAG_PLAY_SOUND
            );
            // 디폴트 알람음은 ID값을 0으로 저장하도록 함
            soundId = 0;
            // 기본적으로 음량은 최대로
            seekBar.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            // 음량 전역변수에 기본값인 최댓값을 저장
            volumeControl = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
    }

    /**
     * ##################################################
     * 각 알람 설정 액티비티로 이동
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : config, resultCode
     * @return :
     * ##################################################
     */
    private void setAlarmConfigure(Class config, int resultCode) {
        intent = new Intent(SettingActivity.this, config);

        bundle.putString(StatusCode.SEARCH_STATION_ACTIVITY_STATUS_DATA, "edit");
        intent.putExtras(bundle);

        stopPlaying();

        startActivityForResult(intent, resultCode);
    }

    /**
     * ##################################################
     * 설정한 알람을 데이터베이스에 저장하고 알람이 울리도록 세팅
     *
     * @since : 19-11-25
     * @author : 빵빵시계팀
     * @param : view
     * @return :
     * ##################################################
     */
    public void registerAlarm(View view) {
        day = getDayVal(day);

        final Calendar cal = Calendar.getInstance();

        if (stationName == null && day.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "값을 설정하세요", Toast.LENGTH_SHORT).show();

            return;
        }
        else if (day.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "요일을 선택하세요", Toast.LENGTH_SHORT).show();

            return;
        }
        else if (stationName == null) {
            Toast.makeText(getApplicationContext(),
                    "정류장, 노선을 선택하세요", Toast.LENGTH_SHORT).show();

            return;
        }
        else if (stationClass == null || stationId == null) {
            Toast.makeText(getApplicationContext(),
                    "데이터베이스 오류. 정류장을 다시 검색 해 주세요", Toast.LENGTH_LONG).show();

            return;
        }

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        alarmSQLiteDAO = new AlarmSQLiteDAO(this);

        if (settingStatus.equals("add"))
            alarmSQLiteDAO.insertAlarmData(
                    hour,
                    minute,
                    stationClass,
                    stationId,
                    stationName,
                    CID,
                    arsId,
                    cityName,
                    routeId,
                    busNumOrSubwayDirection,
                    localStationId,
                    subwayDirection,
                    day,
                    soundId,
                    volumeControl
            );
        else if (settingStatus.equals("edit"))
            alarmSQLiteDAO.updateAlarmData(
                    pkNumber,
                    hour,
                    minute,
                    stationClass,
                    stationId,
                    stationName,
                    CID,
                    arsId,
                    cityName,
                    routeId,
                    busNumOrSubwayDirection,
                    localStationId,
                    subwayDirection,
                    day,
                    soundId,
                    volumeControl
            );

        intent = new Intent(SettingActivity.this, MainActivity.class);
        setResult(StatusCode.EDIT_STATION, intent);
        finish();
    }

    /**
     * ##################################################
     * 설정된 날짜를 한글로 표현
     *
     * @since : 19-11-05
     * @author : 빵빵시게팀
     * @param : day
     * @return : day
     * ##################################################
     */
    private String getDayVal(String day){
        if (Sun.isChecked())
            day += WEEK_NAME[0] + ";";
        if (Mon.isChecked())
            day += WEEK_NAME[1] + ";";
        if (Tue.isChecked())
            day += WEEK_NAME[2] + ";";
        if (Wed.isChecked())
            day += WEEK_NAME[3] + ";";
        if (Thu.isChecked())
            day += WEEK_NAME[4] + ";";
        if (Fri.isChecked())
            day += WEEK_NAME[5] + ";";
        if (Sat.isChecked())
            day += WEEK_NAME[6] + ";";

        return day;
    }

    /**
     * ##################################################
     * 알람을 편집
     *
     * @since : 19-11-25
     * @author : 빵빵시계팀
     * @param : context
     * @return :
     * ##################################################
     */
    private void editAlarm(Context context){
        final AlarmSQLiteDAO db = new AlarmSQLiteDAO(context);
        final JSONArray alarmEditJsonArray = db.getAlarmData(pkNumber);

        try {
            final JSONObject alarmEditJson = alarmEditJsonArray.getJSONObject(0);
            final String[] editDay = alarmEditJson.getString(StatusCode.DB_DAY).split(";");

            searchDayValue(editDay);

            pkNumber = Integer.parseInt(alarmEditJson.getString(StatusCode.DB_PK_NUM));
            hour = Integer.parseInt(alarmEditJson.getString(StatusCode.DB_HOUR));
            minute = Integer.parseInt(alarmEditJson.getString(StatusCode.DB_MINUTE));
            stationClass = alarmEditJson.getString(StatusCode.DB_STATION_CLASS);
            stationId = alarmEditJson.getString(StatusCode.DB_STATION_ID);
            stationName = alarmEditJson.getString(StatusCode.DB_STATION_NAME);
            CID = alarmEditJson.getString(StatusCode.DB_CID);
            arsId = alarmEditJson.getString(StatusCode.DB_ARS_ID);
            cityName = alarmEditJson.getString(StatusCode.DB_CITY_NAME);
            routeId = (alarmEditJson.has(StatusCode.DB_ROUTE_ID))
                    ? alarmEditJson.getString(StatusCode.DB_ROUTE_ID) : null;
            busNumOrSubwayDirection = alarmEditJson.getString(StatusCode.DB_ROUTE_NAME)
                    .replace(";", " ");
            localStationId = (alarmEditJson.has(StatusCode.DB_LOCAL_STATION_ID))
                    ? alarmEditJson.getString(StatusCode.DB_LOCAL_STATION_ID) : null;
            subwayDirection = (alarmEditJson.has(StatusCode.DB_SUBWAY_DIRECTION))
                    ? alarmEditJson.getString(StatusCode.DB_SUBWAY_DIRECTION) : null;

            stationDirectionTextView.setText(busNumOrSubwayDirection);
            stationLocationTextView.setText(stationName);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);

            //알람음과 음량을 읽어와서 세팅하고 변경없이 저장하면 원래의 값이 그대로 저장될 수 있도록 함
            final int editVolume = Integer.parseInt(alarmEditJson.getString(StatusCode.DB_VOLUME));
            final int editSound = Integer.parseInt(alarmEditJson.getString(StatusCode.DB_SOUND_ID));

            seekBar.setProgress(editVolume);
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, editVolume, AudioManager.FLAG_PLAY_SOUND);

            if (editSound == 0)
                mediaPlayer = MediaPlayer.create(getApplicationContext(),
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            else {
                final Uri uri=Uri.parse("android.resource://kr.ac.gachon.bbangbbangclock/"
                        + SelectSoundActivity.soundFileArray[editSound]);

                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.prepare();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            soundInfo.setText(SelectSoundActivity.soundNameArray[editSound]);
            volumeControl = editVolume;
            soundId = editSound;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ##################################################
     * 편집 상태일 시 설정한 날짜 불러오기
     *
     * @since : 19-12-05
     * @author : 류일웅
     * @param : day
     * @return :
     * ##################################################
     */
    private void searchDayValue(String[] day) {
        int dayIndex = 0;

        for (int i=0; i<WEEK_NAME.length; i++) {
            if (day[dayIndex].equals(WEEK_NAME[i])) {
                switch (i) {
                    case 0:
                        Sun.setChecked(true);

                        break;
                    case 1:
                        Mon.setChecked(true);

                        break;
                    case 2:
                        Tue.setChecked(true);

                        break;
                    case 3:
                        Wed.setChecked(true);

                        break;
                    case 4:
                        Thu.setChecked(true);

                        break;
                    case 5:
                        Fri.setChecked(true);

                        break;
                    case 6:
                        Sat.setChecked(true);

                        break;
                }

                dayIndex++;
            }

            if (dayIndex == day.length)
                break;
        }
    }

    /**
     * ##################################################
     * 알람 재생 중지
     *
     * @since : 19-11-25
     * @author : 빵빵시계팀
     * @param :
     * @return :
     * ##################################################
     */
    private void stopPlaying() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();

        pause.setVisibility(View.INVISIBLE);
        soundPlay.setVisibility(View.VISIBLE);
    }

    /**
     * ##################################################
     * 선택한 버스 정류장을 표시
     *
     * @since : 19-12-08
     * @author : 류일웅
     * @param : data
     * @return :
     * ##################################################
     */
    private void showSelectedBusStation(Intent data) {
        bundle = data.getExtras();

        final String busLane = bundle.getString(StatusCode.BUS_LANE_DATA);
        final String[] resultData = busLane.split(",");

        // 공백문자 제거
        for (int i=0; i<resultData.length; i++)
            resultData[i] = resultData[i].trim();

        stationName = resultData[0];
        stationClass = bundle.getString(StatusCode.BUS_CLASS_DATA);
        stationId = bundle.getString(StatusCode.STATION_ID_DATA);
        CID = bundle.getString(StatusCode.STATION_CID_DATA);
        routeId = bundle.getString(StatusCode.BUS_ROUTE_ID_DATA);
        arsId = bundle.getString(StatusCode.STATION_ARSID_DATA);
        cityName = bundle.getString(StatusCode.STATION_CITY_NAME_DATA);
        localStationId = bundle.getString(StatusCode.LOCAL_STATION_ID_DATA);
        busNumOrSubwayDirection = resultData[1] + ";" + resultData[2];

        stationLocationTextView.setText(resultData[0]);
        stationDirectionTextView.setText(resultData[1]);
        stationDirectionTextView.append("번 ");
        stationDirectionTextView.append(resultData[2]);
        stationDirectionTextView.append(" 방향");
    }

    /**
     * ##################################################
     * 선택한 지하철 정류장을 표시
     *
     * @since : 19-12-08
     * @author : 류일웅
     * @param : data
     * @return :
     * ##################################################
     */
    private void showSelectedSubwayStation(Intent data) {
        bundle = data.getExtras();

        final String subwayStation = bundle.getString(StatusCode.SUBWAY_STATION_DATA);
        final String[] resultData = subwayStation.split(";");

        stationName = resultData[0];
        stationClass = bundle.getString(StatusCode.SUBWAY_CLASS_DATA);
        stationId = bundle.getString(StatusCode.STATION_ID_DATA);
        CID = bundle.getString(StatusCode.STATION_CID_DATA);
        arsId = bundle.getString(StatusCode.STATION_ARSID_DATA);
        cityName = resultData[0]; // 원래는 cityName을 가져와야 하지만 크롤링 떄문에 변경
        busNumOrSubwayDirection = resultData[1] + ";" + resultData[2];
        subwayDirection = bundle.getString(StatusCode.STATION_DIRECTION_DATA);

        stationLocationTextView.setText(resultData[0]);
        stationDirectionTextView.setText(resultData[1]);
        stationDirectionTextView.append(" ");
        stationDirectionTextView.append(resultData[2]);
    }

}
