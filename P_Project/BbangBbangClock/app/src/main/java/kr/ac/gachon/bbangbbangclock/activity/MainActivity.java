package kr.ac.gachon.bbangbbangclock.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.ac.gachon.bbangbbangclock.R;
import kr.ac.gachon.bbangbbangclock.adapter.AlarmViewPagerAdapter;
import kr.ac.gachon.bbangbbangclock.common.StatusCode;
import kr.ac.gachon.bbangbbangclock.fragment.AlarmFragment;

/**
 * 메인 액티비티
 *
 * 모든 액티비티에 ButterKnife 라이브러리를 사용.
 * findViewById를 매번 호출할 필요 없이 ButterKnife.bind(this); 메서드로 한번에 바인딩이 가능.
 * 참고 URL: https://calyfactory.github.io/view-binding/
 *
 * Bottom Navigation을 ahbottomnavigation 라이브러리 사용.
 * 참고 URL: https://github.com/aurelhubert/ahbottomnavigation
 *
 * @since : 19-11-25
 * @author : 빵빵시게팀
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;
    @BindView(R.id.view_pager)
    AHBottomNavigationViewPager viewPager;
    @BindView(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.activity_main_coordinator_layout)
    View mainLayout;

    private Bundle bundle;
    private boolean useMenuResource = true;
    private int[] tabColors;
    private Handler handler = new Handler();
    private AHBottomNavigationAdapter navigationAdapter;
    private AlarmFragment currentFragment;
    private AlarmViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        bundle = new Bundle();

        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == StatusCode.SEARCH_STATION || resultCode == StatusCode.EDIT_STATION)
        initUI();
    }

    private void initUI() {
        if (useMenuResource) {
            tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
            navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
            navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
        }

        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.restoreBottomNavigation(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchStationActivity.class);

                bundle.putString(StatusCode.SEARCH_STATION_ACTIVITY_STATUS_DATA, "add");
                intent.putExtras(bundle);

                startActivityForResult(intent, StatusCode.SEARCH_STATION);
            }
        });

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (currentFragment == null)
                    currentFragment = adapter.getCurrentFragment();

                if (wasSelected) {
                    currentFragment.refresh();

                    return true;
                }

                if (currentFragment != null)
                    currentFragment.willBeHidden();

                viewPager.setCurrentItem(position, false);

                if (currentFragment == null)
                    return true;

                currentFragment = adapter.getCurrentFragment();
                currentFragment.willBeDisplayed();

                if (position == 0) {
                    bottomNavigation.setNotification("", 1);

                    floatingActionButton.setAlpha(0f);
                    floatingActionButton.setScaleX(0f);
                    floatingActionButton.setScaleY(0f);
                    floatingActionButton.show();
                    floatingActionButton.animate()
                            .alpha(1)
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(300)
                            .setInterpolator(new OvershootInterpolator())
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    floatingActionButton.setEnabled(true);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    floatingActionButton.animate()
                                            .setInterpolator(new LinearOutSlowInInterpolator())
                                            .start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {}

                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                            })
                            .start();

                } else {
                    if (floatingActionButton.getVisibility() == View.VISIBLE) {
                        floatingActionButton.animate()
                                .alpha(0)
                                .scaleX(0)
                                .scaleY(0)
                                .setDuration(300)
                                .setInterpolator(new LinearOutSlowInInterpolator())
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        floatingActionButton.setEnabled(false);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {}

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        floatingActionButton.hide();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {}
                                })
                                .start();
                    }
                }

                return true;
            }
        });

        viewPager.setOffscreenPageLimit(1);
        adapter = new AlarmViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        currentFragment = adapter.getCurrentFragment();
    }

}