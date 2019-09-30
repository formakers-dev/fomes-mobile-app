package com.formakers.fomes.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class EventPagerAdapter extends PagerAdapter implements EventPagerAdapterContract.Model, EventPagerAdapterContract.View {

    private static final String TAG = "EventPagerAdapter";

    private Context context;
    private List<EventPagerAdapter.BannerItem> events = new ArrayList<>();
    private MainContract.Presenter presenter;

    public EventPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) { this.presenter = presenter; }

    public void addEvent(Post post) {
        ImageView promotionImageView = new ImageView(context);

        Glide.with(context).load(post.getCoverImageUrl())
                .apply(new RequestOptions().centerCrop())
                .into(promotionImageView);

        events.add(new BannerItem(promotionImageView, post));
    }

    @Override
    public void addAll(List<Post> promotions) {
        for (Post post : promotions) {
            this.addEvent(post);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return events.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.v(TAG, "instantiateItem) position=" + position);

        BannerItem event = events.get(position);
        View coverView = event.coverView;

        Context context = container.getContext();

        coverView.setOnClickListener(v -> {
            presenter.getAnalytics().sendClickEventLog(FomesConstants.Main.Log.TARGET_EVENT_BANNER, event.post.getObjectId(), event.post.getTitle());

            Intent intent;

            if (TextUtils.isEmpty(event.post.getDeeplink())) {
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(FomesConstants.WebView.EXTRA_TITLE, event.post.getTitle());
                intent.putExtra(FomesConstants.WebView.EXTRA_CONTENTS, event.post.getContents());
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.post.getDeeplink()));
            }

            context.startActivity(intent);
        });

        container.addView(coverView);

        return coverView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.v(TAG, "destroyItem) position=" + position);

        container.removeView(events.get(position).coverView);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    class BannerItem {
        View coverView;
        Post post;

        public BannerItem(View coverView, Post post) {
            this.coverView = coverView;
            this.post = post;
        }
    }
}
