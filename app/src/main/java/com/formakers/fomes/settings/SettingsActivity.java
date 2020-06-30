package com.formakers.fomes.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.constant.FomesConstants.Settings.Menu;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.custom.adapter.MenuListAdapter;
import com.formakers.fomes.common.view.custom.adapter.MenuListAdapter.MenuItem;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class SettingsActivity extends FomesBaseActivity
        implements SettingsContract.View, MenuListAdapter.OnItemClickListener {

    @BindView(R.id.settings_recyclerview) RecyclerView settingsRecyclerView;

    @Inject SettingsContract.Presenter presenter;

    private MenuListAdapter settingsListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("설정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerSettingsDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new SettingsDagger.Module(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        settingsRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(this, R.style.FomesMainTabTheme_GrayDivider).getTheme()));
        settingsRecyclerView.addItemDecoration(dividerItemDecoration);

        settingsListAdapter = new MenuListAdapter(createSettingsList(), R.layout.item_settings, this);
        settingsRecyclerView.setAdapter(settingsListAdapter);
    }

    private List<MenuItem> createSettingsList() {
        List<MenuItem> settingsItems = new ArrayList<>();

        settingsItems.add(new MenuItem(Menu.VERSION, MenuItem.MENU_TYPE_PLAIN)
                .setTitle(getString(R.string.settings_menu_version))
                .setSideInfo(BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE)
                .setClickable(false));
        settingsItems.add(new MenuItem(Menu.NOTIFICATION_PUBLIC, MenuItem.MENU_TYPE_SWITCH)
                .setTitle(getString(R.string.settings_menu_notification_topic_all))
                .setSubTitle(getString(R.string.settings_menu_notification_topic_all_message))
                .setSwitchChecked(this.presenter.isSubscribedTopic(FomesConstants.Notification.TOPIC_NOTICE_ALL)));
        settingsItems.add(new MenuItem(Menu.TNC_USAGE, MenuItem.MENU_TYPE_PLAIN)
                .setTitle(getString(R.string.settings_menu_usage)));
        settingsItems.add(new MenuItem(Menu.TNC_PRIVATE, MenuItem.MENU_TYPE_PLAIN)
                .setTitle(getString(R.string.settings_menu_private)));
        settingsItems.add(new MenuItem(Menu.CONTACTS_US, MenuItem.MENU_TYPE_PLAIN)
                .setTitle(getString(R.string.settings_menu_contact_us)));

        return settingsItems;
    }

    @Override
    public void onItemClick(MenuListAdapter.MenuItem item, View view) {
        switch (item.getId()) {
            case Menu.TNC_USAGE: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1hcU3MQvJf0L2ZNl3T3H1n21C4E82FM2ppesDwjo-Wy4/edit?usp=sharing"));
                startActivity(intent);
                break;
            }
            case Menu.TNC_PRIVATE: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1QxWuv2zlKYsGYglUTiP5d5IFxZUy6HwKGObXC8x0034/edit?usp=sharing"));
                startActivity(intent);
                break;
            }
            case Menu.CONTACTS_US: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@formakers.net"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[문의]");
                intent.putExtra(Intent.EXTRA_TEXT, "포메스 팀에게 문의해주세요");
                startActivity(intent);
                break;
            }
        }

        sendClickEventLog(item, null);
    }

    private void sendClickEventLog(MenuItem item, Long value) {
        if (this.presenter != null && this.presenter.getAnalytics() != null && item != null) {
            this.presenter.getAnalytics().sendClickEventLog(FomesConstants.Settings.Log.TARGET, String.valueOf(item.getId()), item.getTitle(), value);
        }
    }

    @Override
    public void onSwitchClick(MenuItem item, CompoundButton switchView, boolean isChecked) {
        switch (item.getId()) {
            case Menu.NOTIFICATION_PUBLIC: {
                if (isChecked) {
                    // TODO : 흠... 이런 방법말고 뭔가 더 편한 방법이 있었는데... 기억이......
                    item.setSwitchChecked(true);
                    switchView.setChecked(true);
                    this.showAlertDialog("더 이상 게임 테스트 오픈, 종료 및 보상 수령 관련 등의 메시지들을 전달받으실 수 없습니다.\n수신 거부하시겠습니까?",
                            (dialog, which) -> {
                                this.presenter.toggleNotification(FomesConstants.Notification.TOPIC_NOTICE_ALL);
                                item.setSwitchChecked(false);
                                switchView.setChecked(false);

                                sendClickEventLog(item, FomesConstants.Settings.Log.VALUE_UNCHECKED);
                            },
                            (dialog, which) -> dialog.dismiss());
                } else {
                    this.presenter.toggleNotification(FomesConstants.Notification.TOPIC_NOTICE_ALL);
                    item.setSwitchChecked(true);
                    switchView.setChecked(true);

                    sendClickEventLog(item, FomesConstants.Settings.Log.VALUE_CHECKED);
                }
                break;
            }
        }
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAlertDialog(String message, DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("확인", positiveClickListener)
                .setNegativeButton("취소", negativeClickListener)
                .show();
    }
}
