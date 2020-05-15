package com.formakers.fomes.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.RecentAnalysisReportActivity;
import com.formakers.fomes.betatest.BetaTestFragment;
import com.formakers.fomes.betatest.FinishedBetaTestFragment;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.custom.SwipeViewPager;
import com.formakers.fomes.common.view.custom.adapter.FragmentPagerAdapter;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.formakers.fomes.provisioning.login.LoginActivity;
import com.formakers.fomes.recommend.RecommendFragment;
import com.formakers.fomes.settings.MyInfoActivity;
import com.formakers.fomes.settings.SettingsActivity;
import com.formakers.fomes.wishList.WishListActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnPageChange;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.formakers.fomes.common.constant.FomesConstants.WebView.EXTRA_CONTENTS;
import static com.formakers.fomes.common.constant.FomesConstants.WebView.EXTRA_TITLE;

public class MainActivity extends FomesBaseActivity implements MainContract.View,
        NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_WISHLIST = 1000;
    public static final int REQUEST_CODE_ANALYSIS = 2000;

    public static final int EVENT_AUTO_SLIDE_MILLISECONDS = 3000;

    @BindView(R.id.main_drawer_layout)          DrawerLayout drawerLayout;
    @BindView(R.id.main_side_bar_layout)        NavigationView navigationView;
    @BindView(R.id.main_event_view_pager)       SwipeViewPager eventViewPager;
    @BindView(R.id.main_toolbar)                Toolbar toolbar;
    @BindView(R.id.main_tab_layout)             TabLayout tabLayout;
    @BindView(R.id.main_contents_view_pager)    ViewPager contentsViewPager;

    private FragmentPagerAdapter tabPagerAdapter;

    private Subscription eventPagerAutoSlideSubscription;
    private EventPagerAdapterContract.View eventPagerAdapterView;

    @Inject MainContract.Presenter presenter;

    public interface FragmentCommunicator {
        void onSelectedPage();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate");

        this.setTitle(R.string.common_empty_string);
        this.setContentView(R.layout.activity_main);

        this.injectDependency();

        // 혹시나 모를 상황을 대비해 런치화면 진입시 토큰체크
        verifyAccessToken();
    }

    protected void injectDependency() {
        DaggerMainDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new MainDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.v(TAG,"onPostCreate");

        boolean isRegisteredSendDataJob = presenter.checkRegisteredSendDataJob();
        Log.i(TAG, "isRegisteredSendDataJob=" + isRegisteredSendDataJob);
        if (!isRegisteredSendDataJob) {
            this.presenter.registerSendDataJob();
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerToggle.setDrawerSlideAnimationEnabled(true);
        drawerToggle.syncState();

        // for NavigationView
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.main_nav);
        this.presenter.bindUserInfo();

        // for Main Tab Pager
        tabPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());

        BetaTestFragment betaTestFragment = new BetaTestFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("IS_DEFAULT_PAGE", true);
        betaTestFragment.setArguments(bundle);

        tabPagerAdapter.addFragment(BetaTestFragment.TAG, betaTestFragment, getString(R.string.main_tab_betatest));
        tabPagerAdapter.addFragment(FinishedBetaTestFragment.TAG, new FinishedBetaTestFragment(), getString(R.string.main_tab_finished_betatest));
        tabPagerAdapter.addFragment(RecommendFragment.TAG, new RecommendFragment(), getString(R.string.main_tab_recommend));

        contentsViewPager.setAdapter(tabPagerAdapter);
        contentsViewPager.setOffscreenPageLimit(3);

        this.tabLayout.setupWithViewPager(contentsViewPager);
        this.tabLayout.addOnTabSelectedListener(this);

        // for Main Event Banner Pager
        EventPagerAdapter eventPagerAdapter = new EventPagerAdapter(this);
        eventPagerAdapter.setPresenter(this.presenter);
        eventViewPager.setAdapter(eventPagerAdapter);
        eventViewPager.setEnableSwipe(true);
        eventViewPager.setOnSwipeListener(() -> {
            Log.i(TAG, "EventBanner Touch Swipe! Initialize auto-swipe");
            stopEventPagerAutoSlide();
            startEventPagerAutoSlide(0);
        });
        this.presenter.setEventPagerAdapterModel(eventPagerAdapter);
        this.eventPagerAdapterView = eventPagerAdapter;

        presenter.requestPromotions();


        // for Event Logging
        if (getIntent().getBooleanExtra(FomesConstants.EXTRA.IS_FROM_NOTIFICATION, false)) {
            presenter.sendEventLog(FomesConstants.FomesEventLog.Code.NOTIFICATION_TAP);
        } else {
            presenter.sendEventLog(FomesConstants.FomesEventLog.Code.MAIN_ACTIVITY_ENTER);
        }

        // for Deeplink
        handleDeeplink(getIntent().getExtras());
    }

    @Override
    public void setUserInfoToNavigationView(String email, String nickName) {
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email))
                .setText(email);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_nickname))
                .setText(nickName);
    }

    private void startEventPagerAutoSlide(long additionalInitDelay) {
        Log.v(TAG, "startEventPagerAutoSlide");
        eventPagerAutoSlideSubscription = Observable.interval(EVENT_AUTO_SLIDE_MILLISECONDS + additionalInitDelay, EVENT_AUTO_SLIDE_MILLISECONDS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(seq -> showNextEventBanner(), e -> Log.e(TAG, String.valueOf(e)));
    }

    private void stopEventPagerAutoSlide() {
        Log.v(TAG, "stopEventPagerAutoSlide");
        if (eventPagerAutoSlideSubscription != null && !eventPagerAutoSlideSubscription.isUnsubscribed()) {
            eventPagerAutoSlideSubscription.unsubscribe();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Start Event AutoSlide
        startEventPagerAutoSlide(2000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleDeeplink(intent.getExtras());
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Stop Event AutoSlide
        stopEventPagerAutoSlide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (presenter != null) {
            presenter.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.fomes_postbox).setVisible(true);
        menu.findItem(R.id.my_wish_list).setVisible(false);
        menu.findItem(R.id.my_recent_analysis).setVisible(false);
        menu.findItem(R.id.my_wish_list).getIcon().setTint(getResources().getColor(R.color.fomes_white));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onNavigationItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_info: {
                startActivity(new Intent(this, MyInfoActivity.class));
                break;
            }
            case R.id.my_recent_analysis: {
                startActivity(new Intent(this, RecentAnalysisReportActivity.class));
                break;
            }
            case R.id.my_wish_list: {
                startActivityForResult(new Intent(this, WishListActivity.class), REQUEST_CODE_WISHLIST);
                break;
            }
            case R.id.settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
            case R.id.fomes_postbox: {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(EXTRA_TITLE, getString(R.string.postbox_title));
                intent.putExtra(EXTRA_CONTENTS, this.presenter.getInterpretedUrl(BuildConfig.DEBUG ?
                        "https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&entry.1042588232={email}"
                        : "https://docs.google.com/forms/d/e/1FAIpQLSf2qOJq-YpCBP-S16RLAmPGN3Geaj7g8-eiIpsMrwzvgX-hNQ/viewform?usp=pp_url&entry.1223559684={email}"));
                startActivity(intent);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_WISHLIST || requestCode == REQUEST_CODE_ANALYSIS) {
            Fragment fragment = ((FragmentPagerAdapter) contentsViewPager.getAdapter()).getItem(RecommendFragment.TAG);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        presenter.sendEventLog((tab.getPosition() == 0) ?
                FomesConstants.FomesEventLog.Code.MAIN_ACTIVITY_TAP_BETA_TEST :
                FomesConstants.FomesEventLog.Code.MAIN_ACTIVITY_TAP_RECOMMEND);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void showNextEventBanner() {
        if (eventViewPager == null || presenter.getPromotionCount() < 2)
            return;

        int nextItem = (eventViewPager.getCurrentItem() < presenter.getPromotionCount() - 1) ? eventViewPager.getCurrentItem() + 1 : 0;

        Log.v(TAG, "showNextEventBanner) position=" + nextItem);
        eventViewPager.setCurrentItem(nextItem);
    }

    @Override
    public void refreshEventPager() {
        this.eventPagerAdapterView.notifyDataSetChanged();
    }

    /*** start of 사실상 프래그먼트 페이저를 가지는 모든 액티비티에서 쓰이는 코드...ㅋㅋ ***/
    public boolean isSelectedFragment(Fragment fragment) {
        if (this.contentsViewPager == null) {
            return false;
        }

        return this.contentsViewPager.getCurrentItem() == contentsViewPager.getAdapter().getItemPosition(fragment);
    }

    @OnPageChange(value = R.id.main_contents_view_pager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onSelectedPage(int position) {
        android.util.Log.i("FA", "yenarue screen_view MainActivity onSelectedPage(" + position + ")");
        getFragmentCommunicator(position).onSelectedPage();
    }

    private FragmentCommunicator getCurrentFragmentCommunicator() {
        return getFragmentCommunicator(contentsViewPager.getCurrentItem());
    }

    private FragmentCommunicator getFragmentCommunicator(int position) {
        Fragment currentFragment = ((FragmentPagerAdapter) contentsViewPager.getAdapter()).getItem(position);

        if (currentFragment instanceof FragmentCommunicator) {
            return (FragmentCommunicator) currentFragment;
        } else {
            throw new IllegalArgumentException("Current Fragment didn't implement FragmentCommunicator!");
        }
    }
    /*** end of 사실상 프래그먼트 페이저를 가지는 모든 액티비티에서 쓰이는 코드...ㅋㅋ ***/

    private void verifyAccessToken() {
        addToCompositeSubscription(
            presenter.requestVerifyAccessToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, e -> {
                    if (e instanceof HttpException) {
                        int code = ((HttpException) e).code();

                        // 401 : 토큰이 만료되었으므로 재발급을 위해 로그인 화면으로 보낸다.
                        // 403 : 만료 외 토큰 인증 실패이므로 메인화면에 진입할 수 없어야한다. -> 아무 권한 없이도 볼 수 있는 화면으로 보낸다. (로그인 화면)
                        if (code == 401 || code == 403) {
                            Log.d(TAG, "인증 오류가 발생하였습니다. 재로그인이 필요합니다.");
                            startActivity(LoginActivity.class);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                            return;
                        }
                    }

                    Log.e(TAG, "예상치 못한 에러가 발생하였습니다. e=" + String.valueOf(e));
                })
        );
    }

    private <T> void startActivity(Class<T> destActivity) {
        Intent intent = new Intent(this, destActivity);
        startActivity(intent);
    }

    private void handleDeeplink(Bundle bundle) {
        if (bundle == null) {
            Log.d(TAG, "handleDeeplink) bundle is null. maybe it is not from deeplink");
            return;
        }

        String selectedTab = bundle.getString("EXTRA_SELECTED_TAB", BetaTestFragment.TAG);
        String selectedItemId = bundle.getString("EXTRA_SELECTED_ITEM_ID");

        if (!TextUtils.isEmpty(selectedTab)) {
            Fragment selectedFragment = tabPagerAdapter.getItem(selectedTab);

            Bundle arguemnts = new Bundle();
            arguemnts.putString("EXTRA_SELECTED_ITEM_ID", selectedItemId);

            selectedFragment.setArguments(arguemnts);

            contentsViewPager.postDelayed(() -> {
                Log.d(TAG, "contentsViewPager.postDelayed) contentsViewPager = " + contentsViewPager + ", selectedFragment = " + selectedFragment + ", tagPagerAdapter = " + tabPagerAdapter);

                if (contentsViewPager == null) {
                    return;
                }

                Log.d(TAG, "contentsViewPager.postDelayed) contentsViewPager.getAdapter() = " + contentsViewPager.getAdapter());

                contentsViewPager.setCurrentItem(tabPagerAdapter.getPosition(selectedFragment));
                contentsViewPager.getAdapter().notifyDataSetChanged();
            }, 100);
        }
    }
}
