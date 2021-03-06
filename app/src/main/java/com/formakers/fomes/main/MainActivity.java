package com.formakers.fomes.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.betatest.BetaTestFragment;
import com.formakers.fomes.betatest.FinishedBetaTestFragment;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.formakers.fomes.common.view.custom.SwipeViewPager;
import com.formakers.fomes.common.view.custom.adapter.FragmentPagerAdapter;
import com.formakers.fomes.common.view.webview.WebViewActivity;
import com.formakers.fomes.more.MenuListFragment;
import com.formakers.fomes.provisioning.login.LoginActivity;
import com.formakers.fomes.recommend.RecommendFragment;
import com.formakers.fomes.settings.MyInfoActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_WISHLIST = 1000;
    public static final int REQUEST_CODE_ANALYSIS = 2000;

    public static final int EVENT_AUTO_SLIDE_MILLISECONDS = 3000;

    @BindView(R.id.main_event_view_pager)       SwipeViewPager eventViewPager;
    @BindView(R.id.main_toolbar)                Toolbar toolbar;
    @BindView(R.id.main_bottom_navigation)      BottomNavigationView bottomNavigationView;
    @BindView(R.id.main_contents_view_pager)    SwipeViewPager contentsViewPager;

    private FragmentPagerAdapter contentsViewPagerAdapter;

    private Subscription eventPagerAutoSlideSubscription;
    private EventPagerAdapterContract.View eventPagerAdapterView;

    @Inject MainContract.Presenter presenter;

    private HashMap<Integer, Fragment> contentsFragmentHashMap = new HashMap<>();

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

        // ????????? ?????? ????????? ????????? ???????????? ????????? ????????????
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

        this.presenter.checkNeedToUpdateUserInfo();
        this.presenter.checkNeedToShowMigrationDialog();

        boolean isRegisteredSendDataJob = presenter.checkRegisteredSendDataJob();
        Log.i(TAG, "isRegisteredSendDataJob=" + isRegisteredSendDataJob);
        if (!isRegisteredSendDataJob) {
            this.presenter.registerSendDataJob();
        }

        setSupportActionBar(toolbar);

        // for Main Tab Pager
        contentsViewPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());

        BetaTestFragment betaTestFragment = new BetaTestFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("IS_DEFAULT_PAGE", true);
        betaTestFragment.setArguments(bundle);

        FinishedBetaTestFragment finishedBetaTestFragment = new FinishedBetaTestFragment();
        RecommendFragment recommendFragment = new RecommendFragment();

        contentsViewPagerAdapter.addFragment(BetaTestFragment.TAG, betaTestFragment, getString(R.string.main_tab_betatest));
        contentsViewPagerAdapter.addFragment(FinishedBetaTestFragment.TAG, finishedBetaTestFragment, getString(R.string.main_tab_finished_betatest));
        contentsViewPagerAdapter.addFragment(RecommendFragment.TAG, recommendFragment, getString(R.string.main_tab_recommend));

        contentsViewPager.setAdapter(contentsViewPagerAdapter);
        contentsViewPager.setOffscreenPageLimit(3);
        contentsViewPager.setEnableSwipe(false);

        // for Bottom Menu
        contentsFragmentHashMap.put(R.id.action_betatest, betaTestFragment);
        contentsFragmentHashMap.put(R.id.action_awards, finishedBetaTestFragment);
        contentsFragmentHashMap.put(R.id.action_recommend, recommendFragment);
        contentsFragmentHashMap.put(R.id.action_more, new MenuListFragment());

        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);

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

    // TODO : [????????????] ?????????????????? ???????????? ?????? ????????????
    private void showContentsViewPager(boolean isShow) {
        this.findViewById(R.id.main_contents_container).setVisibility(isShow? View.GONE : View.VISIBLE);
        this.findViewById(R.id.main_event_view_container).setVisibility(isShow ? View.VISIBLE : View.GONE);
        this.contentsViewPager.setVisibility(isShow ? View.VISIBLE : View.GONE);
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
//            super.onBackPressed();
            finishAffinity();
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
        menu.findItem(R.id.fomes_postbox).getIcon().setTint(getResources().getColor(R.color.fomes_text_dark));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onNavigationItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fomes_postbox: {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(EXTRA_TITLE, getString(R.string.postbox_title));
                intent.putExtra(EXTRA_CONTENTS, this.presenter.getInterpretedUrl(BuildConfig.DEBUG ?
                        "https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&entry.1042588232={email}"
                        : "https://docs.google.com/forms/d/e/1FAIpQLSf2qOJq-YpCBP-S16RLAmPGN3Geaj7g8-eiIpsMrwzvgX-hNQ/viewform?usp=pp_url&entry.1223559684={email}"));
                startActivity(intent);
            }
            case R.id.action_betatest: {
                presenter.sendEventLog(FomesConstants.FomesEventLog.Code.MAIN_ACTIVITY_TAP_BETA_TEST);
                contentsViewPager.setCurrentItem(contentsViewPagerAdapter.getPosition(BetaTestFragment.TAG));
                showContentsViewPager(true);
                return true;
            }
            case R.id.action_awards: {
                presenter.sendEventLog(FomesConstants.FomesEventLog.Code.MAIN_ACTIVITY_TAP_FINISHED_BETA_TEST);
                contentsViewPager.setCurrentItem(contentsViewPagerAdapter.getPosition(FinishedBetaTestFragment.TAG));
                showContentsViewPager(true);
                return true;
            }
            case R.id.action_recommend: {
                presenter.sendEventLog(FomesConstants.FomesEventLog.Code.MAIN_ACTIVITY_TAP_RECOMMEND);
                contentsViewPager.setCurrentItem(contentsViewPagerAdapter.getPosition(RecommendFragment.TAG));
                showContentsViewPager(true);
                return true;
            }
            case R.id.action_more: {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.main_contents_container, contentsFragmentHashMap.get(R.id.action_more));
                fragmentTransaction.commit();
                showContentsViewPager(false);
                return true;
            }
        }

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

    @Override
    public void showMigrationNoticeDialog(Bundle migrationNoticeDialogBundle, View.OnClickListener clickListener) {
        FomesNoticeDialog migrationNoticeDialog = new FomesNoticeDialog();

        String positiveButtonText = migrationNoticeDialogBundle.getString("POSITIVE_BUTTON_TEXT");

        migrationNoticeDialog.setArguments(migrationNoticeDialogBundle);
        migrationNoticeDialog.setPositiveButton(positiveButtonText, clickListener);

        migrationNoticeDialog.show(getSupportFragmentManager(), "MigrationNoticeDialog");
    }

    /*** start of ????????? ??????????????? ???????????? ????????? ?????? ?????????????????? ????????? ??????...?????? ***/
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
    /*** end of ????????? ??????????????? ???????????? ????????? ?????? ?????????????????? ????????? ??????...?????? ***/

    private void verifyAccessToken() {
        addToCompositeSubscription(
            presenter.requestVerifyAccessToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, e -> {
                    if (e instanceof HttpException) {
                        int code = ((HttpException) e).code();

                        // 401 : ????????? ????????????????????? ???????????? ?????? ????????? ???????????? ?????????.
                        // 403 : ?????? ??? ?????? ?????? ??????????????? ??????????????? ????????? ??? ???????????????. -> ?????? ?????? ????????? ??? ??? ?????? ???????????? ?????????. (????????? ??????)
                        if (code == 401 || code == 403) {
                            Log.d(TAG, "?????? ????????? ?????????????????????. ??????????????? ???????????????.");
                            startActivity(LoginActivity.class);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                            return;
                        }
                    }

                    Log.e(TAG, "????????? ?????? ????????? ?????????????????????. e=" + String.valueOf(e));
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
            Fragment selectedFragment = contentsViewPagerAdapter.getItem(selectedTab);

            Bundle arguemnts = new Bundle();
            arguemnts.putString("EXTRA_SELECTED_ITEM_ID", selectedItemId);

            selectedFragment.setArguments(arguemnts);

            contentsViewPager.postDelayed(() -> {
                Log.d(TAG, "contentsViewPager.postDelayed) contentsViewPager = " + contentsViewPager + ", selectedFragment = " + selectedFragment + ", tagPagerAdapter = " + contentsViewPagerAdapter);

                if (contentsViewPager == null) {
                    return;
                }

                Log.d(TAG, "contentsViewPager.postDelayed) contentsViewPager.getAdapter() = " + contentsViewPager.getAdapter());

                Integer bottomNavItemId = getKeyByValue(contentsFragmentHashMap, selectedFragment);
                bottomNavigationView.setSelectedItemId(bottomNavItemId == null ? 0 : bottomNavItemId);
                contentsViewPager.setCurrentItem(contentsViewPagerAdapter.getPosition(selectedFragment));
                contentsViewPager.getAdapter().notifyDataSetChanged();
            }, 100);
        }
    }

    @Override
    public void moveToUserUpdate() {
        startActivity(MyInfoActivity.class);
    }

    // TODO : go to common/utils
    private <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
