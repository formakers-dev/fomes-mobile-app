package com.appbee.appbeemobile.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.util.AppBeeConstants.CharacterType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OverviewFragment extends Fragment {

    public static final String EXTRA_APP_LIST_COUNT = "EXTRA_APP_LIST_COUNT";
    public static final String EXTRA_APP_LIST_COUNT_TYPE = "EXTRA_APP_LIST_COUNT_TYPE";
    public static final String EXTRA_APP_AVG_TIME = "EXTRA_APP_AVG_TIME";
    public static final String EXTRA_CHARACTER_TYPE = "EXTRA_CHARACTER_TYPE";
    public static final String EXTRA_APP_USAGE_TIME_TYPE = "EXTRA_APP_USAGE_TIME_TYPE";
    public static final String EXTRA_LONGEST_USED_APP_NAME = "EXTRA_LONGEST_USED_APP_NAME";
    public static final String EXTRA_LONGEST_USED_APP_DESCRIPTION = "EXTRA_LONGEST_USED_APP_DESCRIPTION";
    public static final String EXTRA_LONGEST_USED_APP_ICON_BITMAP = "EXTRA_LONGEST_USED_APP_ICON_BITMAP";
    public static final String EXTRA_APP_AVG_TIME_SHORT = "EXTRA_APP_AVG_TIME_SHORT";

    @BindView(R.id.app_count_textview)
    TextView appCountView;

    @BindView(R.id.app_count_title_textview)
    TextView appCountTitleView;

    @BindView(R.id.average_app_usage_time_textview)
    TextView averageAppUsageTimeView;

    @BindView(R.id.average_app_usage_time_title_textview)
    TextView averageAppUsageTimeTitleView;

    @BindView(R.id.average_app_usage_time_description_textview)
    TextView averageAppUsageTimeDescriptionView;

    @BindView(R.id.longest_used_app_name)
    TextView longestUsedAppNameView;

    @BindView(R.id.longest_used_app_description)
    TextView longestUsedAppDescriptionView;

    @BindView(R.id.longest_used_app_icon)
    ImageView longestUsedAppIcon;

    @BindView(R.id.character_type_icon)
    ImageView characterTypeIconView;

    @BindView(R.id.character_type_name)
    TextView characterTypeNameView;

    @BindView(R.id.character_type_simple_description)
    TextView characterTypeSimpleDescriptionView;

    @BindView(R.id.character_type_detail_description)
    TextView characterTypeDetailDescriptionView;

    @BindView(R.id.app_count_description_textview)
    TextView appCountDescriptionView;

    @BindView(R.id.honey_pot_image)
    ImageView honeyPotImageView;

    @BindView(R.id.hive_image)
    ImageView hiveImageView;

    private Unbinder binder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        binder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayCharacterTypeInformation((CharacterType) getArguments().getSerializable(EXTRA_CHARACTER_TYPE));
        displayAppListCountInformation(getArguments().getInt(EXTRA_APP_LIST_COUNT), getArguments().getInt(EXTRA_APP_LIST_COUNT_TYPE));
        displayAppUsageTimeInformation(getArguments().getInt(EXTRA_APP_AVG_TIME), getArguments().getInt(EXTRA_APP_USAGE_TIME_TYPE), getArguments().getInt(EXTRA_APP_AVG_TIME_SHORT));
        displayLongestUsedAppInformation(getArguments().getString(EXTRA_LONGEST_USED_APP_NAME), getArguments().getString(EXTRA_LONGEST_USED_APP_DESCRIPTION), getArguments().getParcelable(EXTRA_LONGEST_USED_APP_ICON_BITMAP));
    }

    private void displayCharacterTypeInformation(CharacterType characterType) {
        characterTypeIconView.setImageResource(characterType.image);
        characterTypeNameView.setText(characterType.title);
        characterTypeSimpleDescriptionView.setText(characterType.simpleDescription);
        characterTypeDetailDescriptionView.setText(characterType.detailDescription);
    }

    private void displayAppUsageTimeInformation(int appUsageAverageTime, int appUsageTimeType, int appUsageAverageTimeShort) {
        averageAppUsageTimeView.setText(appUsageAverageTime + "\n" + appUsageAverageTimeShort);
        final String appUsageTimeTypeName = getResources().getStringArray(R.array.app_usage_time_type_names)[appUsageTimeType];
        averageAppUsageTimeTitleView.setText(getString(R.string.overview_average_time_title, appUsageTimeTypeName));
        averageAppUsageTimeDescriptionView.setText(getResources().getStringArray(R.array.app_usage_time_type_descriptions)[appUsageTimeType]);
        if (appUsageAverageTime >= 480) {
            hiveImageView.setImageResource(R.drawable.full_hive);
        } else if (appUsageAverageTime >= 240) {
            hiveImageView.setImageResource(R.drawable.three_quater_hive);
        } else if (appUsageAverageTime >= 120) {
            hiveImageView.setImageResource(R.drawable.half_hive);
        } else if (appUsageAverageTime >= 60) {
            hiveImageView.setImageResource(R.drawable.quater_hive);
        } else {
            hiveImageView.setImageResource(R.drawable.poor_hive);
        }
    }

    private void displayAppListCountInformation(int appListCount, int appListCountType) {
        appCountView.setText(getString(R.string.overview_app_count_with_unit, appListCount));
        final String appCountTitle = getResources().getStringArray(R.array.app_list_count_type_names)[appListCountType];
        appCountTitleView.setText(getString(R.string.overview_app_count_title, appCountTitle));
        appCountDescriptionView.setText(getResources().getStringArray(R.array.app_list_count_type_descriptions)[appListCountType]);
        if (appListCount >= 150) {
            honeyPotImageView.setImageResource(R.drawable.full_honey_pot);
        } else if (appListCount >= 100) {
            honeyPotImageView.setImageResource(R.drawable.three_quater_honey_pot);
        } else if (appListCount >= 50) {
            honeyPotImageView.setImageResource(R.drawable.half_honey_pot);
        } else if (appListCount >= 25) {
            honeyPotImageView.setImageResource(R.drawable.quater_honey_pot);
        } else {
            honeyPotImageView.setImageResource(R.drawable.poor_honey_pot);
        }
    }

    private void displayLongestUsedAppInformation(String appName, String description, Bitmap iconBitmap) {
        longestUsedAppNameView.setText(getString(R.string.longest_used_app_title, appName));
        longestUsedAppDescriptionView.setText(description);
        if (iconBitmap != null) {
            longestUsedAppIcon.setImageBitmap(iconBitmap);
        }
    }

    @Override
    public void onDestroyView() {
        binder.unbind();
        super.onDestroyView();
    }
}
