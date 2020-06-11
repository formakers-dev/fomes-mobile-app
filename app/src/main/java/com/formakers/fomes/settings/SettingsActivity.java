package com.formakers.fomes.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants.Settings.Menu;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.common.view.custom.adapter.MenuListAdapter;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SettingsActivity extends FomesBaseActivity implements MenuListAdapter.OnItemClickListener {

    @BindView(R.id.settings_recyclerview) RecyclerView settingsRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("설정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        MenuListAdapter settingsListAdapter = new MenuListAdapter(createSettingsList(), R.layout.item_settings, this);
        settingsRecyclerView.setAdapter(settingsListAdapter);
    }

    private List<MenuListAdapter.MenuItem> createSettingsList() {
        List<MenuListAdapter.MenuItem> settingsItems = new ArrayList<>();

        settingsItems.add(new MenuListAdapter.MenuItem(Menu.VERSION).setTitle(getString(R.string.settings_menu_version))
                .setSideInfo(BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE).setClickable(false));
        settingsItems.add(new MenuListAdapter.MenuItem(Menu.TNC_USAGE).setTitle(getString(R.string.settings_menu_usage)));
        settingsItems.add(new MenuListAdapter.MenuItem(Menu.TNC_PRIVATE).setTitle(getString(R.string.settings_menu_private)));
        settingsItems.add(new MenuListAdapter.MenuItem(Menu.CONTACTS_US).setTitle(getString(R.string.settings_menu_contact_us)));

        return settingsItems;
    }

    @Override
    public void onItemClick(MenuListAdapter.MenuItem item) {
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
    }
}
