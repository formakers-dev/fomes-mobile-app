package com.formakers.fomes.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.settings.adapter.SettingsListAdapter;
import com.formakers.fomes.settings.model.SettingsItem;
import com.formakers.fomes.util.FomesConstants.Settings.*;
import com.formakers.fomes.common.view.decorator.ContentDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SettingsActivity extends FomesBaseActivity implements SettingsListAdapter.OnItemClickListener{

    @BindView(R.id.settings_recyclerview) RecyclerView settingsRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("설정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        settingsRecyclerView.setLayoutManager(linearLayoutManager);

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, new ContextThemeWrapper(this, R.style.FomesMainTabTheme_RecommendDivider).getTheme()));
        settingsRecyclerView.addItemDecoration(dividerItemDecoration);

        SettingsListAdapter settingsListAdapter = new SettingsListAdapter(createSettingsList(), this);
        settingsRecyclerView.setAdapter(settingsListAdapter);
    }

    private List<SettingsItem> createSettingsList() {
        List<SettingsItem> settingsItems = new ArrayList<>();

        settingsItems.add(new SettingsItem.Builder().setId(Menu.VERSION).setTitle("포메스 정보")
                .setSideInfo(BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE).setClickable(false).build());
        settingsItems.add(new SettingsItem.Builder().setId(Menu.TNC_USAGE).setTitle("이용약관").build());
        settingsItems.add(new SettingsItem.Builder().setId(Menu.TNC_PRIVATE).setTitle("개인정보 취급 방침").build());
        settingsItems.add(new SettingsItem.Builder().setId(Menu.CONTACTS_US).setTitle("이메일로 문의하기").build());

        return settingsItems;
    }

    @Override
    public void onItemClick(SettingsItem item) {
        switch (item.getId()) {
            case Menu.CONTACTS_US: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@fomakers.net"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[문의]");
                intent.putExtra(Intent.EXTRA_TEXT, "포메스 팀에게 문의해주세요");
                startActivity(intent);
                break;
            }
        }
    }
}
