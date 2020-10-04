package kr.ac.gachon.bbangbbangclock.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.adapter.BusLaneDialogAdapter;
import kr.ac.gachon.bbangbbangclock.adapter.SearchStationAdapter;
import kr.ac.gachon.bbangbbangclock.adapter.SubwayNearbyStationDialogAdapter;
import kr.ac.gachon.bbangbbangclock.common.ApiCall;
import kr.ac.gachon.bbangbbangclock.common.StatusCode;
import kr.ac.gachon.bbangbbangclock.data.SearchStationData;

/**
 * 대중교통 정류장 결과를 보여주는 액티비티
 *
 * Dialog 사용 시 dialogplus 라이브러리를 사용함.
 * 참고 URL: https://github.com/orhanobut/dialogplus
 *
 * 검색뷰 MaterialSearchView 라이브러리를 사용함.
 * 참고 URL: https://github.com/MiguelCatalan/MaterialSearchView
 *
 * 로딩뷰 Loading 라이브러리를 사용함.
 * 참고 URL: https://github.com/yankai-victor/Loading
 *
 * @since : 19-11-25
 * @author : 빵빵시게팀
 */
public class SearchStationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.activity_search_station_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.rotate_loading)
    RotateLoading rotateLoading;

    private SearchStationAdapter searchStationAdapter;
    private List<SearchStationData> searchStationDataList;
    private LinearLayoutManager mLinearLayoutManager;
    private Handler handler;
    private Intent intent;
    private Bundle bundle;
    private ApiCall apiCall;
    private String stationId, CID, arsId, cityName, stationName, laneName, localStationId;
    private String resultJsonData;
    private String activityStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_station);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("정류장을 검색하세요");

        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.roundedbutton);
        searchView.setEllipsize(true);
        searchView.setHint("정류장을 검색하세요");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStation(query);

                getSupportActionBar().setTitle(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {}

            @Override
            public void onSearchViewClosed() {}
        });

        mLinearLayoutManager
                = new LinearLayoutManager(SearchStationActivity.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        intent = getIntent();
        bundle = intent.getExtras();
        activityStatus = bundle.getString(StatusCode.SEARCH_STATION_ACTIVITY_STATUS_DATA);

        handler = new Handler();
        apiCall = new ApiCall();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem helpItem = menu.findItem(R.id.action_chart);

        searchView.setMenuItem(searchItem);
        searchItem.setVisible(true);
        helpItem.setVisible(false);

        searchView.showSearch();

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen())
            searchView.closeSearch();
        else
            super.onBackPressed();
    }

    /**
     * ##################################################
     * 사용자가 입력한 값으로 대중교통 정류장을 검색
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void searchStation(String inputText) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rotateLoading.start();
                    }
                });

                // API를 통하여 대중교통 정류장 정보를 검색
                resultJsonData = apiCall.searchStation(
                        SearchStationActivity.this, inputText
                );

                searchStationDataList = new ArrayList<>();

                initializeStationData();

                // 정류장 정보를 받아 왔으면 화면에 출력
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        searchStationAdapter = new SearchStationAdapter(
                                searchStationDataList
                        );
                        mRecyclerView.setAdapter(searchStationAdapter);

                        final DividerItemDecoration dividerItemDecoration
                                = new DividerItemDecoration(mRecyclerView.getContext(),
                                mLinearLayoutManager.getOrientation());
                        mRecyclerView.addItemDecoration(dividerItemDecoration);

                        searchStationAdapter.setOnItemClickListener(new SearchStationAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                final String stationType
                                        = searchStationDataList.get(position).getStationClass();

                                stationId = searchStationDataList.get(position).getStationID();
                                stationName = searchStationDataList.get(position).getStationName();
                                CID = searchStationDataList.get(position).getCID();
                                arsId = searchStationDataList.get(position).getArsID();
                                cityName = searchStationDataList.get(position).getCityName();
                                laneName = searchStationDataList.get(position).getLaneName();
                                localStationId = searchStationDataList.get(position).getLocalStationID();

                                if (stationType.equals("버스정류장")) {
                                    searchBusLane(stationId);
                                } else if (stationType.equals("지하철역")) {
                                    searchSubwayNearbyStation(stationId);
                                }
                            }
                        });

                        rotateLoading.stop();
                    }
                });
                // 1초 딜레이 설정
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * ##################################################
     * 버스정류장의 모든 노선 정보를 검색
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : stationID
     * @return :
     * ##################################################
     */
    // 아래 함수 구조는 searchStation()와 동일함. by 류일웅
    private void searchBusLane(String stationID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                resultJsonData = apiCall.searchBusLane(
                        SearchStationActivity.this, stationID
                );

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showBusLaneDialog(resultJsonData);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * ##################################################
     * 지하철의 이전역, 다음역 정보를 검색
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : stationID
     * @return :
     * ##################################################
     */
    // 아래 함수 구조는 searchStation()와 동일함. by 류일웅
    private void searchSubwayNearbyStation(String stationID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                resultJsonData = apiCall.searchSubwayInfo(
                        SearchStationActivity.this, stationID
                );

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showSubwayDialog(resultJsonData);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * ##################################################
     * API를 통해 가져온 대중교통 정류장 정보를 List에 저장
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param :
     * @return :
     * ##################################################
     */
    private void initializeStationData() {
        /*
        API에서 가져온 station class를 한국어로 변환시키기 위해 정의
        참고: https://lab.odsay.com/guide/releaseReference?platform=android
        by 류일웅
         */
        final String[] stationName = {
                "버스정류장", "지하철역", "기차역", "고속버스터미널", "공항", "시외버스터미널"
        };

        try {
            final JSONObject jsonObject = new JSONObject(resultJsonData);
            final JSONArray stationArray = jsonObject.getJSONArray("station");

            for (int i=0; i<stationArray.length(); i++) {
                final JSONObject stationObject = stationArray.getJSONObject(i);
                final String originalStationClass = stationObject.getString("stationClass");
                final String changeStationClass
                        = stationName[Integer.parseInt(originalStationClass) - 1];
                arsId = (!stationObject.getString("arsID").isEmpty()) ?
                        stationObject.getString("arsID") : "고유번호 확인 불가";

                // 정류장이 지하철역이면 노선 이름까지 사용자에게 보여줌
                if (originalStationClass.equals("2")) {
                    searchStationDataList.add(new SearchStationData(
                            changeStationClass,
                            stationObject.getString("stationName"),
                            stationObject.getString("stationID"),
                            stationObject.getString("CID"),
                            arsId,
                            stationObject.getString("cityName"),
                            stationObject.getString("laneName"),
                            null,
                            stationObject.getString("dong")
                    ));
                } else {
                    if (stationObject.has("localStationID"))
                        searchStationDataList.add(new SearchStationData(
                                changeStationClass,
                                stationObject.getString("stationName"),
                                stationObject.getString("stationID"),
                                stationObject.getString("CID"),
                                arsId,
                                stationObject.getString("cityName"),
                                null,
                                stationObject.getString("localStationID"),
                                stationObject.getString("dong")
                        ));
                    else
                        searchStationDataList.add(new SearchStationData(
                                changeStationClass,
                                stationObject.getString("stationName"),
                                stationObject.getString("stationID"),
                                stationObject.getString("CID"),
                                arsId,
                                stationObject.getString("cityName"),
                                null,
                                null,
                                stationObject.getString("dong")
                        ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ##################################################
     * List에 저장된 버스 노선 정보를 dialog로 표시
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : data
     * @return :
     * ##################################################
     */
    private void showBusLaneDialog(String data) {
        final Holder holder = new ListHolder();
        final JSONObject jsonObject;
        final JSONArray busLaneArray;

        try {
            jsonObject = new JSONObject(data);
            busLaneArray = jsonObject.getJSONArray("lane");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        BusLaneDialogAdapter adapter = new BusLaneDialogAdapter(
                SearchStationActivity.this, busLaneArray, busLaneArray.length()
        );

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setHeader(R.layout.layout_dialog_header)
                .setFooter(R.layout.layout_dialog_footer)
                .setAdapter(adapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    // 버스 노선을 클릭하면 설정창에서 값을 보여줌
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position == -1)
                            return;

                        TextView busTextView = view.findViewById(R.id.layout_bus_list_info);
                        TextView busRouteIdView = view.findViewById(R.id.layout_bus_list_route_id);

                        final String sendData = stationName + ", " + busTextView.getText().toString();
                        intent = new Intent(
                                SearchStationActivity.this, SettingActivity.class
                        );
                        bundle = new Bundle();

                        bundle.putString(StatusCode.BUS_LANE_DATA, sendData);
                        bundle.putString(StatusCode.ALARM_SETTING_STATUS_DATA, "add");
                        bundle.putString(StatusCode.BUS_CLASS_DATA, "1");
                        bundle.putString(StatusCode.STATION_ID_DATA, stationId);
                        bundle.putString(StatusCode.STATION_CID_DATA, CID);
                        bundle.putString(StatusCode.STATION_ARSID_DATA, arsId);
                        bundle.putString(StatusCode.LOCAL_STATION_ID_DATA, localStationId);
                        bundle.putString(StatusCode.STATION_CITY_NAME_DATA, cityName);
                        bundle.putString(StatusCode.BUS_ROUTE_ID_DATA, busRouteIdView.getText().toString());

                        if (activityStatus.equals("add")) {
                            bundle.putString(StatusCode.STATION_CLASS_DATA, "bus");
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                        else if (activityStatus.equals("edit")) {
                            intent.putExtras(bundle);

                            setResult(StatusCode.SELECT_BUS, intent);
                        }

                        finish();
                    }
                })
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .setExpanded(false)
                .create();
        dialog.show();
    }

    /**
     * ##################################################
     * List에 저장된 지하철 정보를 dialog로 표시
     *
     * @since : 19-11-25
     * @author : 류일웅
     * @param : data
     * @return :
     * ##################################################
     */
    // 아래 함수 구조는 showBusLaneDialog()와 동일함. by 류일웅
    private void showSubwayDialog(String data) {
        final Holder holder = new ListHolder();
        final JSONObject jsonObject;
        final JSONArray subwayRouteArray;

        try {
            jsonObject = new JSONObject(data);
            subwayRouteArray = jsonObject.getJSONArray("subwayInfo");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        SubwayNearbyStationDialogAdapter adapter = new SubwayNearbyStationDialogAdapter(
                SearchStationActivity.this, subwayRouteArray, subwayRouteArray.length()
        );

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setAdapter(adapter)
                .setHeader(R.layout.layout_dialog_header)
                .setFooter(R.layout.layout_dialog_footer)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        if (position == -1)
                            return;

                        TextView subwayTextView = view.findViewById(R.id.layout_subway_list_info);
                        final String sendData = stationName
                                + ";"
                                + laneName
                                + ";"
                                + subwayTextView.getText().toString();

                        TextView subwayDirectionTextView = view.findViewById(R.id.layout_subway_list_direction);
                        final String direction = subwayDirectionTextView.getText().toString();

                        intent = new Intent(
                                SearchStationActivity.this, SettingActivity.class
                        );
                        bundle = new Bundle();

                        bundle.putString(StatusCode.SUBWAY_STATION_DATA, sendData);
                        bundle.putString(StatusCode.ALARM_SETTING_STATUS_DATA, "add");
                        bundle.putString(StatusCode.SUBWAY_CLASS_DATA, "2");
                        bundle.putString(StatusCode.STATION_CID_DATA, CID);
                        bundle.putString(StatusCode.STATION_ID_DATA, stationId);
                        bundle.putString(StatusCode.STATION_ARSID_DATA, arsId);
                        bundle.putString(StatusCode.STATION_CITY_NAME_DATA, cityName);
                        bundle.putString(StatusCode.STATION_DIRECTION_DATA, direction);

                        if (activityStatus.equals("add")) {
                            bundle.putString(StatusCode.STATION_CLASS_DATA, "subway");
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                        else if (activityStatus.equals("edit")) {
                            intent.putExtras(bundle);

                            setResult(StatusCode.SELECT_SUBWAY, intent);
                        }

                        finish();
                    }
                })
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        dialog.dismiss();
                    }
                })
                .setExpanded(false)
                .create();
        dialog.show();
    }

}
