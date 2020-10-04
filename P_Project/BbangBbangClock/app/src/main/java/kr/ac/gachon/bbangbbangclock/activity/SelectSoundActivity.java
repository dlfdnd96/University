package kr.ac.gachon.bbangbbangclock.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.common.StatusCode;

/**
 * 알람음 설정 액티비티
 *
 * @since : 19-11-25
 * @author : 빵빵시계팀
 */
public class SelectSoundActivity extends Activity {

    @BindView(R.id.soundlist)
    ListView soundListView;

    ArrayList<String> soundList;
    ArrayAdapter<String> adapter;

    public static final String [] soundNameArray = {
            "기본알람음","차단음","벨소리1","몽롱함","스톱워치","방사능경고","방사능경고2","닭울음소리",
            "경보기","사이렌","시계소리","짧은시계소리"};
    public static final int [] soundFileArray = {0, R.raw.bcginn__alarm_clock,
            R.raw.hookhead__alarm_clock, R.raw.kizilsungur__sweetalertsound5,
            R.raw.kodack__beep_beep_1, R.raw.radio_illuminati__nuclear_alarm,
            R.raw.rene__nuclear_alarm, R.raw.rudmer_rotteveel__chicken_single_alarm_call,
            R.raw.sbyandiji__short_alarm_bell_in_school_hall_some_clock_ticks_etc,
            R.raw.sirplus__extreme_alarm, R.raw.xyzr_kx__alarm_clock,
            R.raw.zyrytsounds__alarm_clock_short};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sound);

        ButterKnife.bind(this);

        showAlarmList();

        soundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                //선택한것의 인덱스값을 결과로 넘겨서 DB에 저장하고 인덱스값으로 activity_select_sound 클래스의 id값, name값을 참조하도록 함
                Intent intent = new Intent(SelectSoundActivity.this, SettingActivity.class);
                Bundle bundle = new Bundle();

                bundle.putInt(StatusCode.SOUND_INDEX_DATA, index);
                bundle.putString(StatusCode.SOUND_NAME_DATA, soundNameArray[index]);
                intent.putExtras(bundle);

                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });
    }

    /**
     * ##################################################
     * 알람음 목록 보여주기
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void showAlarmList() {
        soundList = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, soundList);

        soundList.addAll(Arrays.asList(soundNameArray));

        soundListView.setAdapter(adapter);
    }

}
