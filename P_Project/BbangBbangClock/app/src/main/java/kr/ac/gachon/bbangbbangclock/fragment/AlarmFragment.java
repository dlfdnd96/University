package kr.ac.gachon.bbangbbangclock.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.activity.SettingActivity;
import kr.ac.gachon.bbangbbangclock.adapter.AlarmAdapter;
import kr.ac.gachon.bbangbbangclock.common.StatusCode;
import kr.ac.gachon.bbangbbangclock.dao.AlarmSQLiteDAO;

/**
 * AlarmFragment
 *
 * @since : 19-12-05
 * @author : 류일웅
 */
public class AlarmFragment extends Fragment {

	FrameLayout fragmentAlarmsContainer;
	RecyclerView recyclerView;

	PieChart pieChart;
	TextView result;

	private RecyclerView.LayoutManager layoutManager;

	private AlarmAdapter adapter;
	private AlarmSQLiteDAO alarmSQLiteDAO;
	private ArrayAdapter<CharSequence> adspin1, adspin2, adspin3;
	private String choiceMonth = "12월";// 분기나 월을 저장하는 변수
	private String choiceType1 = "정류장별"; // 정류장별인지 노선별인지 저장하는변수
	private String choiceType2 = "월별"; // 월별인지 분기별인지 구분하는 변수
	
	/**
	 * Create a new instance of the fragment
	 */
	public static AlarmFragment newInstance(int index) {
		AlarmFragment fragment = new AlarmFragment();
		Bundle b = new Bundle();

		b.putInt("index", index);
		fragment.setArguments(b);

		return fragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getArguments().getInt("index", 0) == 0) {
			View view = inflater.inflate(R.layout.fragment_alarms, container, false);

			initAlarmList(view);

			return view;
		}
		else {
			View view = inflater.inflate(R.layout.fragment_statistics, container, false);

			initStatistics(view);

			return view;
		}
	}

	/**
	 * Init the fragment
	 */
	private void initAlarmList(View view) {
		fragmentAlarmsContainer = view.findViewById(R.id.fragment_alarms_container);
		recyclerView = view.findViewById(R.id.fragment_alarm_recycler_view);

		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(getActivity());
		alarmSQLiteDAO = new AlarmSQLiteDAO(getContext());
		recyclerView.setLayoutManager(layoutManager);

		showAlarms();
	}

	private void initStatistics(View view) {
		setStatisticsViews(view);
	}

	/**
	 * ##################################################
	 * 사용자 알람 목록 화면에 표시
	 *
	 * @since : 19-11-25
	 * @author : 빵빵시계팀
	 * @param :
	 * @return :
	 * ##################################################
	 */
	private void showAlarms() {
		final JSONArray alarmJsonArray = alarmSQLiteDAO.getAlarmDataList();

		adapter = new AlarmAdapter(alarmJsonArray, getActivity());

		adapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View v, int position) {
				final CheckBox checkBox = (CheckBox) v.findViewById(R.id.layout_item_alarm_check_box);
				final String getItem = checkBox.getText().toString();
				Intent intent = new Intent(getContext(), SettingActivity.class);
				Bundle bundle = new Bundle();

				bundle.putInt(StatusCode.DATABASE_INDEX_DATA, Integer.parseInt(getItem));
				bundle.putString(StatusCode.ALARM_SETTING_STATUS_DATA, "edit");
				intent.putExtras(bundle);

				getActivity().startActivityForResult(intent, StatusCode.EDIT_STATION);
			}
		});

		recyclerView.setAdapter(adapter);
	}

	/**
	 * ##################################################
	 * 통계 차트 화면에 표시
	 *
	 * @since : 19-12-17
	 * @author : 이용성, 류일웅
	 * @param : view
	 * @return :
	 * ##################################################
	 */
	private void setStatisticsViews(View view) {
		final Spinner spinner1 = (Spinner) view.findViewById(R.id.spinner1);
		final Spinner spinner2 = (Spinner) view.findViewById(R.id.spinner2);
		final Spinner spinner3 = (Spinner) view.findViewById(R.id.spinner3);
		pieChart = (PieChart) view.findViewById(R.id.piechart);
		result = (TextView)view.findViewById(R.id.Money);

		adspin1 = ArrayAdapter.createFromResource(
				getContext(), R.array.statistics_array1, android.R.layout.simple_spinner_dropdown_item);

		adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adspin1);
		spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (adspin1.getItem(position).equals("정류장별")) {
					choiceType1 = "정류장별";

					drawStationChart(choiceMonth);
				} else {
					choiceType1 = "노선별";

					drawRouteChart(choiceMonth);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		adspin2 = ArrayAdapter.createFromResource(
				getContext(), R.array.statistics_array2, android.R.layout.simple_spinner_dropdown_item);
		adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adspin2);
		spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (adspin2.getItem(position).equals("월별")) {
					choiceMonth = "월별";
					choiceType2 = "월별";
					adspin3 = ArrayAdapter.createFromResource(
							getContext(), R.array.statistics_array3, android.R.layout.simple_spinner_dropdown_item);

					adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner3.setAdapter(adspin3);
					//spin3.setSelection(11); //12월로 초기화
					spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							choiceMonth = adspin3.getItem(position).toString();

							//그래프 시험22
							if (choiceType1.equals(("정류장별")))
								drawStationChart(choiceMonth);
							else
								drawRouteChart(choiceMonth);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {}
					});
				}
				else if (adspin2.getItem(position).equals("분기별")) {
					choiceMonth = "분기별";
					choiceType2 = "분기별";
					adspin3 = ArrayAdapter.createFromResource(
							getContext(), R.array.statistics_array4, android.R.layout.simple_spinner_dropdown_item);

					adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner3.setAdapter(adspin3);
					spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							choiceMonth = adspin3.getItem(position).toString();

							if (choiceType1.equals(("정류장별")))
								drawStationChart(choiceMonth);
							else
								drawRouteChart(choiceMonth);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {}
					});
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	/**
	 * ##################################################
	 * 정류장별로 차트를 표시
	 *
	 * @since : 19-12-17
	 * @author : 이용성, 류일웅
	 * @param : choiceMonth
	 * @return :
	 * ##################################################
	 */
	private void drawStationChart(String choiceMonth) {
		String stationName = null;
		String countStationName = null;
		List<String> stationNameList = new ArrayList<>();
		List<Integer> countList = new ArrayList<>();
		Integer Counts = null;
		Integer Money = null;
		final StringBuilder builder = new StringBuilder();

		try {
			alarmSQLiteDAO = new AlarmSQLiteDAO(getContext());
			JSONArray getParam = alarmSQLiteDAO.getBoardingDataListStName(choiceMonth);

			for (int i = 0; i < getParam.length(); i++) {
				JSONObject boardingDataJson = getParam.getJSONObject(i);
				stationName = boardingDataJson.getString(StatusCode.DB_STATION_NAME);
				countStationName = boardingDataJson.getString("count");

				stationNameList.add(stationName);
				countList.add(Integer.parseInt(countStationName));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			alarmSQLiteDAO = new AlarmSQLiteDAO(getContext());
			JSONArray getParame = alarmSQLiteDAO.getBoardingDataCount(choiceMonth);

			for (int j = 0; j < getParame.length(); j++) {
				JSONObject CountsForMoney =  getParame.getJSONObject(j);
				Counts = CountsForMoney.getInt("count");
			}
		} catch (JSONException e){
			e.printStackTrace();
		}

		Money = Counts * 1250;

		pieChart.setUsePercentValues(true);
		pieChart.getDescription().setEnabled(false);
		pieChart.setExtraOffsets(5, 10, 5, 5);

		pieChart.setDragDecelerationFrictionCoef(0.95f);

		pieChart.setDrawHoleEnabled(false);
		pieChart.setHoleColor(Color.WHITE);
		pieChart.setTransparentCircleRadius(61f);

		ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

		for (int i = 0; i < stationNameList.size(); i++) {
			yValues.add(new PieEntry(countList.get(i), stationNameList.get(i)));
		}

		Description description = new Description();
		description.setText(this.choiceMonth + " " + choiceType1 + " 통계 자료"); //라벨
		description.setTextSize(20);
		pieChart.setDescription(description);

		pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

		PieDataSet dataSet = new PieDataSet(yValues, "정류장");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

		PieData data = new PieData((dataSet));
		data.setValueTextSize(10f);
		data.setValueTextColor(Color.YELLOW);

		pieChart.setData(data);

		//돈
		builder.delete(0, builder.length());
		Log.d(StatusCode.TAG, "알람 프레그먼트: "+Money);
		builder.append("이번달 교통비 예상 지출액은 ").append(Money).append("원 입니다.");
		result.setText(builder.toString());

	}

	/**
	 * ##################################################
	 * 노선별로 차트를 표시
	 *
	 * @since : 19-12-17
	 * @author : 이용성, 류일웅
	 * @param : choiceMonth
	 * @return :
	 * ##################################################
	 */
	private void drawRouteChart(String choiceMonth) {
		String routeName = null;
		String countStationName = null;
		List<String> RouteNumList = new ArrayList<>();
		List<Integer> CountList = new ArrayList<>();
		Integer Counts = null;
		Integer Money = null;
		final StringBuilder builder = new StringBuilder();

		try {
			alarmSQLiteDAO = new AlarmSQLiteDAO(getContext());
			JSONArray getParam = alarmSQLiteDAO.getBoardingDataListRouteNum(choiceMonth);

			for (int i = 0; i < getParam.length(); i++) {
				JSONObject countRouteNum = getParam.getJSONObject(i);
				routeName = countRouteNum.getString(StatusCode.DB_ROUTE_NAME);
				countStationName = countRouteNum.getString("count");

				RouteNumList.add(routeName);
				CountList.add(Integer.parseInt(countStationName));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			alarmSQLiteDAO = new AlarmSQLiteDAO(getContext());
			JSONArray getParame = alarmSQLiteDAO.getBoardingDataCount(choiceMonth);

			for(int j = 0; j < getParame.length(); j++){
				JSONObject CountsForMoney =  getParame.getJSONObject(j);
				Counts = CountsForMoney.getInt("count");
			}
		} catch (JSONException e){
			e.printStackTrace();
		}

		Money = Counts * 1250;

		pieChart.setUsePercentValues(true);
		pieChart.getDescription().setEnabled(false);
		pieChart.setExtraOffsets(5, 10, 5, 5);

		pieChart.setDragDecelerationFrictionCoef(0.95f);

		pieChart.setDrawHoleEnabled(false);
		pieChart.setHoleColor(Color.WHITE);
		pieChart.setTransparentCircleRadius(61f);

		ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

		for (int i = 0; i < RouteNumList.size(); i++) {
			yValues.add(new PieEntry(CountList.get(i), RouteNumList.get(i)));
		}

		Description description = new Description();
		description.setText(choiceMonth + " " + choiceType1 + " 통계 자료"); //라벨
		description.setTextSize(20);
		pieChart.setDescription(description);

		pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

		PieDataSet dataSet = new PieDataSet(yValues, "노선");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

		PieData data = new PieData((dataSet));
		data.setValueTextSize(10f);
		data.setValueTextColor(Color.YELLOW);

		pieChart.setData(data);

		builder.delete(0, builder.length());
		Log.d(StatusCode.TAG, "알람 프레그먼트: "+Money);
		builder.append("이번달 교통비 예상 지출은 ").append(Money).append("원 입니다."); //돈돈돈
		result.setText(builder.toString());
	}
	
	/**
	 * Refresh
	 */
	public void refresh() {
		if (getArguments().getInt("index", 0) > 0 && recyclerView != null)
			recyclerView.smoothScrollToPosition(0);
	}
	
	/**
	 * Called when a fragment will be displayed
	 */
	public void willBeDisplayed() {
		// Do what you want here, for example animate the content
		if (fragmentAlarmsContainer != null) {
			Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
			fragmentAlarmsContainer.startAnimation(fadeIn);
		}
	}
	
	/**
	 * Called when a fragment will be hidden
	 */
	public void willBeHidden() {
		if (fragmentAlarmsContainer != null) {
			Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);

			fragmentAlarmsContainer.startAnimation(fadeOut);
		}
	}

}
