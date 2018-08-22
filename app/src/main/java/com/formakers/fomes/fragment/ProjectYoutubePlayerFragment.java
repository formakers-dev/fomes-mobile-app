package com.formakers.fomes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.BuildConfig;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class ProjectYoutubePlayerFragment extends YouTubePlayerFragment {

    public static final String EXTRA_YOUTUBE_ID = "YOUTUBE_ID";
    String youTubeId;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);

        youTubeId = getArguments().getString(EXTRA_YOUTUBE_ID, "");

        this.initialize(BuildConfig.YOUTUBE_API_KEY, onInitializedListener);

        return view;
    }

    YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            youTubePlayer.cueVideo(youTubeId);
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        }
    };
}