package com.formakers.fomes.betatest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.formakers.fomes.R;
import com.formakers.fomes.common.network.vo.AwardRecord;
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
    public void addAll(List<AwardRecord> awardRecords) {
        this.awardItems = Stream.of(awardRecords)
                .sorted((item1, item2) -> item2.getTypeCode() - item1.getTypeCode())
                .reduce(new ArrayList<>(), (awardItems, awardRecord) -> {
                    if (awardItems.isEmpty() ||
                            !awardRecord.getTypeCode().equals(awardItems.get(awardItems.size() - 1).typeCode)) {
                        List<String> nickNames = new ArrayList();
                        nickNames.add(awardRecord.getNickName());

                        awardItems.add(new AwardItem(awardRecord.getTypeCode(),
                                null,
                                awardRecord.getReward().getDescription(),
                                1,
                                nickNames));
                    } else {
                        AwardItem awardItem = awardItems.get(awardItems.size() - 1);
                        awardItem.count += 1;
                        awardItem.nickNames.add(awardRecord.getNickName());
                    }

                    return awardItems;
                });
    }

    @Override
    public void addAllFromRewardItems(List<BetaTest.Rewards.RewardItem> rewardItems) {
        List<AwardItem> awardItems = new ArrayList<>();

        Collections.sort(rewardItems, (item1, item2) -> item2.getTypeCode() - item1.getTypeCode());

        for (BetaTest.Rewards.RewardItem rewardItem : rewardItems) {
            awardItems.add(new AwardItem(rewardItem.getTypeCode(),
                    rewardItem.getTitle(),
                    rewardItem.getContent(),
                    rewardItem.getCount(),
                    null));
        }

        this.awardItems = awardItems;
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

            ((TextView)view.findViewById(R.id.betatest_title_awards_best)).setText(awardItem.getTitle());

            ImageView crowdedPeopleImageView = view.findViewById(R.id.betatest_awards_crowded_people);
            ImageView awardsMedalImageView = view.findViewById(R.id.betatest_awards_medal);
            ViewGroup awardsMedalContentsLayout = view.findViewById(R.id.betatest_awards_medal_contents_layout);
            TextView awardsPriceTextView = view.findViewById(R.id.betatest_awards_price);
            TextView awardsNickNameTextView = view.findViewById(R.id.betatest_awards_nickname);
            TextView awardsNickNameEndTextView = view.findViewById(R.id.betatest_awards_nickname_end);
            TextView awardsNickNamesTextView = view.findViewById(R.id.betatest_awards_nicknames);
            ViewGroup awardsNoneMedalContentsLayout = view.findViewById(R.id.betatest_awards_none_medal_contents_layout);
            awardsNickNameTextView.setSelected(true);

            if(position > 0) {
                crowdedPeopleImageView.setVisibility(View.GONE);
            }

            if(awardItem.hasNickNames()) {
                if(awardItem.nickNames.size() == 1) {
                    awardsNickNameTextView.setText(awardItem.nickNames.get(0));
                    awardsNickNameEndTextView.setVisibility(View.VISIBLE);
                } else {
                    awardsMedalImageView.setVisibility(View.GONE);
                    crowdedPeopleImageView.setVisibility(View.GONE);
                    awardsMedalContentsLayout.setVisibility(View.GONE);

                    awardsNickNamesTextView.setText(Stream.of(awardItem.nickNames).collect(Collectors.joining(", ")) + " 님");
                    awardsNoneMedalContentsLayout.setVisibility(View.VISIBLE);
                }

                awardsPriceTextView.setText(awardItem.description);

            } else {
                String rewardCountText = (awardItem.count == null || awardItem.count == 0)? "참여자 전원" : awardItem.count + "명 선정";
                awardsPriceTextView.setText(awardItem.description + " (" + rewardCountText + ")");
            }

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

        public boolean hasNickNames() {
            return nickNames != null && nickNames.size() > 0;
        }

        public String getTitle() {
            if(title == null) {
                switch(typeCode) {
                    case 9000:
                        return "테스트 수석";
                    case 7000:
                        return "테스트 차석";
                    case 5000:
                        return "테스트 성실상";
                    case 3000:
                        return "참가상";
                    default :
                        return "기타";
                }
            } else {
                return title;
            }
        }
    }
}
