package com.formakers.fomes.betatest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinishedBetaTestAwardPagerAdapter extends PagerAdapter implements FinishedBetaTestAwardPagerAdapterContract.Model, FinishedBetaTestAwardPagerAdapterContract.View {

    private static final String TAG = "FinishedBetaTestRewardPagerAdapter";

    private Context context;
    private List<AwardItem> awardItems = new ArrayList<>();
    private FinishedBetaTestDetailContract.Presenter presenter;

    public FinishedBetaTestAwardPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void setPresenter(FinishedBetaTestDetailContract.Presenter presenter) { this.presenter = presenter; }

    @Override
    public void addAll(List<AwardItem> awardItems) {
        Collections.sort(awardItems, (item1, item2) -> item2.typeCode - item1.typeCode);
        this.awardItems = awardItems;
    }

    @Override
    public void addAllFromRewardItems(List<BetaTest.Rewards.RewardItem> rewardItems) {
        List<AwardItem> awardItems = new ArrayList<>();

        for (BetaTest.Rewards.RewardItem rewardItem : rewardItems) {
            awardItems.add(new AwardItem(rewardItem.getTypeCode(),
                    rewardItem.getTitle(),
                    rewardItem.getContent(),
                    rewardItem.getCount(),
                    null));
        }

        addAll(awardItems);
    }

    @Override
    public int getItemPosition(Object object) {
        return awardItems.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        AwardItem awardItem = awardItems.get(position);
        View view = null;

        if (context != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_finish_betatest_awards, container, false);

            ((TextView)view.findViewById(R.id.betatest_title_awards_best)).setText(awardItem.title);
            ((TextView)view.findViewById(R.id.betatest_awards_price)).setText(awardItem.description);

            TextView awardsNickNameTextView = view.findViewById(R.id.betatest_awards_nickname);
            awardsNickNameTextView.setSelected(true);
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return awardItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    class AwardItem {
        Integer typeCode;
        String title;
        String description;
        Integer count;
        List<String> nickNames;

        public AwardItem(Integer typeCode, String title, String description, Integer count, List<String> nickNames) {
            this.typeCode = typeCode;
            this.title = title;
            this.description = description;
            this.count = count;
            this.nickNames = nickNames;
        }
    }
}
