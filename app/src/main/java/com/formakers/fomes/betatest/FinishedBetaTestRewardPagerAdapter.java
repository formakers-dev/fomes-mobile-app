package com.formakers.fomes.betatest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.ArrayList;
import java.util.List;

public class FinishedBetaTestRewardPagerAdapter extends PagerAdapter implements FinishedBetaTestRewardPagerAdapterContract.Model, FinishedBetaTestRewardPagerAdapterContract.View {

    private static final String TAG = "FinishedBetaTestRewardPagerAdapter";

    private Context context;
    private List<BetaTest.Rewards.RewardItem> rewardItems;
    private FinishedBetaTestDetailContract.Presenter presenter;

    public FinishedBetaTestRewardPagerAdapter(Context context) {
        this.context = context;
        this.rewardItems = new ArrayList<>();
    }

    @Override
    public void setPresenter(FinishedBetaTestDetailContract.Presenter presenter) { this.presenter = presenter; }

    @Override
    public void addAll(List<BetaTest.Rewards.RewardItem> rewardItems) {
        this.rewardItems = rewardItems;
//        for (BetaTest.Rewards.RewardItem rewardItem : rewardItems) {
//            rewardItems.add(rewardItem);
//        }
    }

    @Override
    public int getItemPosition(Object object) {
        return rewardItems.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BetaTest.Rewards.RewardItem rewardItem = rewardItems.get(position);
        View view = null;

        if (context != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_finish_betatest_awards, container, false);

//            TextView textView = (TextView) view.findViewById(R.id.title) ;
//            textView.setText("TEXT " + position) ;
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
        return rewardItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
