package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BrainFragmentTest {

    private BrainFragment subject;
    private Unbinder binder;

    @BindView(R.id.most_installed_category_summary)
    TextView mostInstalledCategorySummaryView;

    @BindView(R.id.most_installed_category_description)
    TextView mostInstalledCategoryDescriptionView;

    @BindView(R.id.most_installed_category1)
    TextView mostInstalledCategory1View;

    @BindView(R.id.most_installed_category2)
    TextView mostInstalledCategory2View;

    @BindView(R.id.most_installed_category3)
    TextView mostInstalledCategory3View;

    @BindView(R.id.least_installed_category)
    TextView leastInstalledCategoryView;

    @BindView(R.id.brain_image_layout)
    RelativeLayout brainImageLayout;

    @BindView(R.id.rank1_layout)
    RelativeLayout rank1Layout;

    @BindView(R.id.rank2_layout)
    RelativeLayout rank2Layout;

    @BindView(R.id.rank3_layout)
    RelativeLayout rank3Layout;

    @BindView(R.id.last_rank_layout)
    RelativeLayout lastRankLayout;

    public void setUpWithEnoughData() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostInstalledCategoryList = new ArrayList<>();
        mostInstalledCategoryList.add("금융");
        mostInstalledCategoryList.add("게임");
        mostInstalledCategoryList.add("만화");
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, mostInstalledCategoryList);

        ArrayList<String> leastInstalledCategoryList = new ArrayList<>();
        leastInstalledCategoryList.add("건강");
        bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, leastInstalledCategoryList);

        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY, "금융 앱이 전체 앱 개수의 25%");
        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, "금융앱 설명입니다");

        createFragment(bundle);
    }

    public void setUpWithNotEnoughData() throws Exception {
        Bundle bundle = new Bundle();

        ArrayList<String> mostInstalledCategoryList = new ArrayList<>();
        mostInstalledCategoryList.add("게임");
        bundle.putStringArrayList(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORIES, mostInstalledCategoryList);

        ArrayList<String> leastInstalledCategoryList = new ArrayList<>();
        leastInstalledCategoryList.add("게임");
        bundle.putStringArrayList(BrainFragment.EXTRA_LEAST_INSTALLED_CATEGORIES, leastInstalledCategoryList);

        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_SUMMARY, "금융 앱이 전체 앱 개수의 25%");
        bundle.putString(BrainFragment.EXTRA_MOST_INSTALLED_CATEGORY_DESCRIPTION, "금융앱 설명입니다");

        createFragment(bundle);
    }

    private void createFragment(Bundle bundle) {
        subject = new BrainFragment();
        subject.setArguments(bundle);

        FragmentController.of(subject).create();
        binder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }

    @Test
    public void onViewCreated호출시_가장많이설치한카테고리목록이_나타난다() throws Exception {
        setUpWithEnoughData();

        assertThat(mostInstalledCategory1View.getText()).isEqualTo("금융");
        assertThat(mostInstalledCategory2View.getText()).isEqualTo("게임");
        assertThat(mostInstalledCategory3View.getText()).isEqualTo("만화");
    }

    @Test
    public void 세건미만의카테고리정보가주어지는경우_onViewCreated호출시_카테고리정보부족_레이아웃이_나타난다() throws Exception {
        setUpWithNotEnoughData();

        assertThat(brainImageLayout.getBackground()).isEqualTo(subject.getResources().getDrawable(R.drawable.no_brain_background, null));

        assertThat(rank1Layout.getVisibility()).isEqualTo(View.GONE);
        assertThat(rank2Layout.getVisibility()).isEqualTo(View.GONE);
        assertThat(rank3Layout.getVisibility()).isEqualTo(View.GONE);
        assertThat(lastRankLayout.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void onViewCreated호출시_가장적게설치한카테고리목록이_나타난다() throws Exception {
        setUpWithEnoughData();

        assertThat(leastInstalledCategoryView.getText()).isEqualTo("건강");
    }

    @Test
    public void onViewCreated호출시_가장_많이_설치된_카테고리에_대한_정보를_표시한다() throws Exception {
        setUpWithEnoughData();

        assertThat(String.valueOf(mostInstalledCategorySummaryView.getText())).isEqualTo("금융 앱이 전체 앱 개수의 25%");
        assertThat(mostInstalledCategoryDescriptionView.getText()).isEqualTo("금융앱 설명입니다");
    }
}
