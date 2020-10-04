package kr.ac.gachon.bbangbbangclock.adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.nightonke.jellytogglebutton.EaseTypes.EaseType;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.JellyTypes.Jelly;
import com.nightonke.jellytogglebutton.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.common.StatusCode;
import kr.ac.gachon.bbangbbangclock.dao.AlarmSQLiteDAO;
import kr.ac.gachon.bbangbbangclock.receiver.AlarmReceiver;
import kr.ac.gachon.bbangbbangclock.util.AlarmUtil;

import static android.content.Context.ALARM_SERVICE;

/**
 * 메인화면 리스트뷰 adapter
 *
 * Switch 사용 시 JellyToggleButton 라이브러리 사용함.
 * 참고 URL: https://github.com/Nightonke/JellyToggleButton
 *
 * @since : 19-11-25
 * @author : 빵빵시계팀
*/
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private Activity activity;
    private JSONArray alarmJsonArray;
    private JSONObject alarmJson;
    private AlarmSQLiteDAO db;
    private Snackbar snackbar;
    private Map<Integer, Integer> checkedList;
    private static OnItemClickListener mListener = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_item_alarm_check_box)
        CheckBox checkBox;
        @BindView(R.id.layout_item_alarm_broadcast_id)
        TextView broadcastId;
        @BindView(R.id.layout_item_alarm_time)
        TextView time;
        @BindView(R.id.layout_item_alarm_days)
        TextView days;
        @BindView(R.id.layout_item_alarm_station_name)
        TextView stationName;
        @BindView(R.id.layout_item_alarm_route_name)
        TextView routeName;
        @BindView(R.id.layout_item_alarm_switch_button)
        JellyToggleButton switchButton;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (mListener != null)
                        mListener.onItemClick(v, position);
                }
            });
        }

    }

    public AlarmAdapter(JSONArray alarmJsonArray, Activity activity) {
        this.alarmJsonArray = alarmJsonArray;
        this.activity = activity;
        checkedList = new HashMap<>();
        db = new AlarmSQLiteDAO(this.activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            alarmJson = alarmJsonArray.getJSONObject(position);

            final long setTime = (Integer.parseInt(alarmJson.getString(StatusCode.DB_HOUR)) - 9)
                    * 60 * 60 * 1000
                    + Integer.parseInt(alarmJson.getString(StatusCode.DB_MINUTE)) * 60 * 1000;
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("aa hh:mm");
            final String resultTime = simpleDateFormat.format(new Date(setTime));
            final String dayValue = alarmJson.getString(StatusCode.DB_DAY)
                    .replace(";", " ");

            holder.checkBox.setText(alarmJson.getString(StatusCode.DB_PK_NUM));
            holder.time.setText(resultTime);
            holder.days.setText(dayValue);
            holder.stationName.setText(alarmJson.getString(StatusCode.DB_STATION_NAME));
            holder.routeName.setText(alarmJson.getString(StatusCode.DB_ROUTE_NAME)
                    .replace(";", " "));

            holder.switchButton.setJelly(Jelly.ITSELF);
            holder.switchButton.setEaseType(EaseType.EaseInSine);
            holder.switchButton.setLeftBackgroundColor(Color.BLACK);
            holder.switchButton.setRightBackgroundColor(Color.BLUE);
            holder.switchButton.setLeftThumbColor(Color.RED);
            holder.switchButton.setRightThumbColor(Color.GREEN);
            holder.switchButton.setDuration(500);

            if (alarmJson.getString(StatusCode.DB_CHECKED).equals("1"))
                holder.switchButton.setChecked(true);
            else
                holder.switchButton.setChecked(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    checkedList.put(position, Integer.parseInt(compoundButton.getText().toString()));

                    showSnackBar();
                }
                else {
                    checkedList.remove(position);

                    if (checkedList.size() == 0)
                        snackbar.dismiss();
                }
            }
        });

        holder.switchButton.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                final Intent intent = new Intent(activity, AlarmReceiver.class);
                final Bundle bundle = new Bundle();
                String uuidString = "";

                try {
                    AlarmSQLiteDAO alarmSQLiteDAO = new AlarmSQLiteDAO(activity);

                    alarmJson = alarmJsonArray.getJSONObject(position);

                    final int alarmPrimaryKey = Integer.parseInt(alarmJson.getString(StatusCode.DB_PK_NUM));
                    final int hour = Integer.parseInt(alarmJson.getString(StatusCode.DB_HOUR));
                    final int minute = Integer.parseInt(alarmJson.getString(StatusCode.DB_MINUTE));
                    final String days = alarmJson.getString(StatusCode.DB_DAY);
                    final String[] splitDay = days.split(";");
                    int uniqueId = alarmPrimaryKey * 2000;

                    bundle.putInt(StatusCode.ALARM_RECEIVER_CODE_DATA, alarmPrimaryKey);
                    intent.putExtras(bundle);

                    if (state.equals(State.RIGHT)) {//false->true : 알람설정
                        for (String e : splitDay) {
                            Calendar calendar = Calendar.getInstance();

                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            if (e.equals("월")) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                                calendar = checkOverTime(calendar);

                                AlarmUtil.getInstance().setSettingAlarm(
                                        activity, uniqueId + 2, intent, calendar.getTimeInMillis());

                                uuidString += uniqueId + 2 + ";";
                            }
                            else if (e.equals("화")) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

                                calendar = checkOverTime(calendar);

                                AlarmUtil.getInstance().setSettingAlarm(
                                        activity, uniqueId + 3, intent, calendar.getTimeInMillis());

                                uuidString += uniqueId + 3 + ";";
                            }
                            else if (e.equals("수")) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

                                calendar = checkOverTime(calendar);

                                AlarmUtil.getInstance().setSettingAlarm(
                                        activity, uniqueId + 4, intent, calendar.getTimeInMillis());

                                uuidString += uniqueId + 4 + ";";
                            }
                            else if (e.equals("목")) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

                                calendar = checkOverTime(calendar);

                                AlarmUtil.getInstance().setSettingAlarm(
                                        activity, uniqueId + 5, intent, calendar.getTimeInMillis());

                                uuidString += uniqueId + 5 + ";";
                            }
                            else if (e.equals("금")) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

                                calendar = checkOverTime(calendar);

                                AlarmUtil.getInstance().setSettingAlarm(
                                        activity, uniqueId + 6, intent, calendar.getTimeInMillis());

                                uuidString += uniqueId + 6 + ";";
                            }
                            else if (e.equals("토")) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

                                calendar = checkOverTime(calendar);

                                AlarmUtil.getInstance().setSettingAlarm(
                                        activity, uniqueId + 7, intent, calendar.getTimeInMillis());

                                uuidString += uniqueId + 7 + ";";
                            }
                            else if (e.equals("일")) {
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

                                calendar = checkOverTime(calendar);

                                AlarmUtil.getInstance().setSettingAlarm(
                                        activity, uniqueId + 1, intent, calendar.getTimeInMillis());

                                uuidString += uniqueId + 1 + ";";
                            }

                            Date currentDateTime = calendar.getTime();
                            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                            Log.d(StatusCode.TAG,date_text + "으로 알람이 설정되었습니다!");
                        }

                        Log.d(StatusCode.TAG, "알람 어댑터 uuid: "+uuidString);

                        holder.broadcastId.setText(uuidString);

                        alarmSQLiteDAO.updateChecked(alarmPrimaryKey, 1);
                    }
                    else {//true->false : 알람해제
                        String uuidText = holder.broadcastId.getText().toString();
                        String[] uuidTextSplit = uuidText.split(";");

                        for (String e : uuidTextSplit) {
                            Log.d(StatusCode.TAG, "알람 어댑터 해제: "+ e);

                            if (e.equals(""))
                                continue;
                            else
                                AlarmUtil.getInstance().cancelAlarm(
                                        activity, Integer.parseInt(e), intent);
                        }

                        alarmSQLiteDAO.updateChecked(alarmPrimaryKey, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_alarm,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return alarmJsonArray.length();
    }

    /**
     * ##################################################
     * 사용자 알람 삭제
     *
     * @since : 19-11-25
     * @author : 빵빵시계팀
     * @param :
     * @return :
     * ##################################################
     */
    private void deleteAlarm() {
        final AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);

        Set set = checkedList.entrySet();
        Iterator iterator = set.iterator();
        int i = 0;

        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            int key = (int)entry.getKey();
            int value = (int)entry.getValue();

            db.deleteAlarmData(value);

            Intent intent = new Intent(activity, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(activity
                    , key, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pIntent);

            key = key - i;

            alarmJsonArray.remove(key);
            notifyItemRemoved(key);
            notifyItemRangeChanged(key, 1);

            i++;
        }

        checkedList.clear();
    }

    /**
     * ##################################################
     * 삭제 알림 스낵바 표시
     *
     * @since : 19-12-15
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void showSnackBar() {
        if (checkedList.size() > 1)
            return;

        snackbar = Snackbar.make(
                activity.findViewById(R.id.activity_main_coordinator_layout),
                "알람을 삭제하시겠습니까?", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("삭제", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlarm();

                snackbar.dismiss();
                // TODO: floating action button 위치 수정하기
            }
        });
        snackbar.show();
    }

    private Calendar checkOverTime(Calendar calendar) {
        // 현재 시간이 설정한 시간보다 늦을 경우 알람이 바로 울리기 때문에 내일로 넘기는 코드
        if (calendar.getTimeInMillis() < System.currentTimeMillis())
            calendar.add(Calendar.DAY_OF_WEEK, 7);

        return calendar;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    /**
     * OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
     *
     * @since : 19-12-15
     * @author : 류일웅
     * @param : listener
     * @return :
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

}