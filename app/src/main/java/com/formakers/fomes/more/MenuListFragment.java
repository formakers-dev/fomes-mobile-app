package com.formakers.fomes.more;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.RecentAnalysisReportActivity;
import com.formakers.fomes.common.constant.FomesConstants.More;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseFragment;
import com.formakers.fomes.common.view.custom.adapter.MenuListAdapter;
import com.formakers.fomes.common.view.custom.adapter.MenuListAdapter.MenuItem;
import com.formakers.fomes.main.MainActivity;
import com.formakers.fomes.point.history.PointHistoryActivity;
import com.formakers.fomes.point.exchange.PointExchangeActivity;
import com.formakers.fomes.settings.MyInfoActivity;
import com.formakers.fomes.settings.SettingsActivity;
import com.formakers.fomes.wishList.WishListActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class MenuListFragment extends BaseFragment implements MenuListContract.View, MenuListAdapter.OnItemClickListener {

    private static final String TAG = "MenuListFragment";

    private static final int REQUEST_CODE_EXCHANGE = 1001;

    @BindView(R.id.more_email)
    TextView emailTextView;
    @BindView(R.id.more_nickname)
    TextView nickNameTextView;
    @BindView(R.id.more_participation_count)
    TextView participationCountTextView;
    @BindView(R.id.my_available_point)
    TextView availablePointTextView;
    @BindView(R.id.more_menu_list)
    RecyclerView menuListView;
    @BindView(R.id.exchange_point_button)
    Button exchangePointButton;

    @Inject
    MenuListContract.Presenter presenter;
    private MenuListAdapter menuListAdapter;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DaggerMenuListDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this.getActivity()).getComponent())
                .module(new MenuListDagger.Module(this))
                .build()
                .inject(this);

        return inflater.inflate(R.layout.activity_more, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.presenter.bindUserInfo();
        this.presenter.bindCompletedBetaTestsCount();
        this.presenter.bindAvailablePoint();
        this.setMenuListView();

        availablePointTextView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PointHistoryActivity.class);
            context.startActivity(intent);
        });

        exchangePointButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PointExchangeActivity.class);
            startActivityForResult(intent, REQUEST_CODE_EXCHANGE);
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

        if (requestCode == REQUEST_CODE_EXCHANGE
                && resultCode == Activity.RESULT_OK) {
            this.presenter.bindAvailablePoint();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setPresenter(MenuListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setMenuListView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        menuListView.setLayoutManager(linearLayoutManager);

        this.menuListAdapter = new MenuListAdapter(createMenuList(), R.layout.item_menu, this);
        this.menuListView.setAdapter(menuListAdapter);
    }

    private List<MenuListAdapter.MenuItem> createMenuList() {
        List<MenuListAdapter.MenuItem> menuItemList = new ArrayList<>();

        menuItemList.add(new MenuItem(More.MENU_HOW_TO_PC, MenuItem.MENU_TYPE_PLAIN).setTitle("PC로 설문 참여하려면?").setIconImageDrawable(context.getDrawable(R.drawable.icon_new)));
        menuItemList.add(new MenuItem(More.MENU_PROFILE, MenuItem.MENU_TYPE_PLAIN).setTitle("프로필 수정").setIconImageDrawable(context.getDrawable(R.drawable.icon_my_info)));
        menuItemList.add(new MenuItem(More.MENU_GAME_ANALYSIS, MenuItem.MENU_TYPE_PLAIN).setTitle("게임 성향 분석").setIconImageDrawable(context.getDrawable(R.drawable.icon_my_recent_analysis)));
        menuItemList.add(new MenuItem(More.MENU_WISH_LIST, MenuItem.MENU_TYPE_PLAIN).setTitle("관심 게임 리스트").setIconImageDrawable(context.getDrawable(R.drawable.icon_bookmark)));
        menuItemList.add(new MenuItem(More.MENU_SETTINGS, MenuItem.MENU_TYPE_PLAIN).setTitle("설정").setIconImageDrawable(context.getDrawable(R.drawable.icon_settings)));

        return menuItemList;
    }

    @Override
    public void setUserInfo(String email, String nickName) {
        if (context == null) {
            return;
        }

        emailTextView.setText(email);
        nickNameTextView.setText(nickName);
    }

    @Override
    public void setCompletedBetaTestsCount(int count) {
        if (context == null) {
            return;
        }

        participationCountTextView.setText(String.format(context.getString(R.string.my_completed_betatest_count_format), count));
        participationCountTextView.startAnimation(getFadeInAnimation(300));
    }

    @Override
    public void setAvailablePoint(long point) {
        availablePointTextView.setText(String.format("%s P", NumberFormat.getInstance().format(point)));
        availablePointTextView.startAnimation(getFadeInAnimation(300));

        exchangePointButton.setEnabled(point >= 5000L);
    }

    @Override
    public void onItemClick(MenuListAdapter.MenuItem item, View view) {
        Intent intent = new Intent();

        switch (item.getId()) {
            case More.MENU_HOW_TO_PC: {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.notion.so/formakers/PC-4e409af0c2df4dfa9734328c9817f317"));
                break;
            }
            case More.MENU_PROFILE: {
                intent.setClass(context, MyInfoActivity.class);
                break;
            }
            case More.MENU_GAME_ANALYSIS: {
                intent.setClass(context, RecentAnalysisReportActivity.class);
                break;
            }
            case More.MENU_WISH_LIST: {
                intent.setClass(context, WishListActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_WISHLIST);
                return;
            }
            case More.MENU_SETTINGS: {
                intent.setClass(context, SettingsActivity.class);
                break;
            }
        }

        startActivity(intent);
    }

    @Override
    public void onSwitchClick(MenuItem item, CompoundButton switchView, boolean isChecked) {

    }
}
