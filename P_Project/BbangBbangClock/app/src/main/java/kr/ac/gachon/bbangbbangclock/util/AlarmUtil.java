package kr.ac.gachon.bbangbbangclock.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import kr.ac.gachon.bbangbbangclock.receiver.AlarmReceiver;

/**
 * 알람 서비스 유틸
 *
 * @since : 19-12-16
 * @author : 류일웅
 */
public class AlarmUtil {

    private static AlarmUtil _instance;

    private final static int FIVE_MINUTE = 300 * 1000;

    public static AlarmUtil getInstance() {
        if (_instance == null)
            _instance = new AlarmUtil();

        return _instance;
    }

    /**
     * ##################################################
     * 알람 취소
     *
     * @since : 19-12-16
     * @author : 류일웅
     * @param : context, requestCode, intent
     * @return :
     * ##################################################
     */
    public void cancelAlarm(Context context, int requestCode, Intent intent) {
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }

    /**
     * ##################################################
     * 사용자가 설정된 알람 세팅
     *
     * @since : 19-12-16
     * @author : 류일웅
     * @param : context, requestCode, intent, setTime
     * @return :
     * ##################################################
     */
    public void setSettingAlarm(
            Context context, int requestCode, Intent intent, long setTime) {
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        startSettingAlarm(context, pendingIntent, setTime);
    }

    /**
     * ##################################################
     * 5분뒤 알람 세팅
     *
     * @since : 19-12-16
     * @author : 류일웅
     * @param : context, requestCode
     * @return :
     * ##################################################
     */
    public void setFiveMinuteAlarm(Context context, Intent intent, int requestCode) {
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        startAlarm(context, pendingIntent, System.currentTimeMillis() + FIVE_MINUTE);
    }

    /**
     * ##################################################
     * 요일별 알람 시작
     *
     * @since : 19-12-16
     * @author : 류일웅
     * @param : context, intent, setTime
     * @return :
     * ##################################################
     */
    private void startSettingAlarm(Context context, PendingIntent pendingIntent, long setTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setTime, 24 * 7 * 60 * 60 * 1000, pendingIntent);
    }

    /**
     * ##################################################
     * 5분뒤 알람 시작
     *
     * @since : 19-12-16
     * @author : 류일웅
     * @param : context, intent, setTime
     * @return :
     * ##################################################
     */
    private void startAlarm(Context context, PendingIntent pendingIntent, long setTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, setTime, pendingIntent);
    }

}
