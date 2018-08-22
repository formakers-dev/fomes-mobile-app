package com.formakers.fomes.fragment;

import android.os.Bundle;

import com.formakers.fomes.BuildConfig;
import com.google.android.youtube.player.YouTubePlayer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.util.FragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectYoutubePlayerFragmentTest {
    ProjectYoutubePlayerFragment subject;

    @Before
    public void setUp() throws Exception {
        subject = new ProjectYoutubePlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ProjectYoutubePlayerFragment.EXTRA_YOUTUBE_ID, "abcYouTubeId");
        subject.setArguments(bundle);

        startFragment(subject);
    }

    @Test
    public void Fragment생성시_Bundle로_넘어온_유투브ID를_임시저장한다() throws Exception {
        assertThat(subject.youTubeId).isEqualTo("abcYouTubeId");
    }

    @Test
    public void onInitalizedListener의_초기화성공시_유투브플레이어에_Bundle로_넘어온_유투브ID를_등록한다() throws Exception {
        YouTubePlayer mockYouTubePlayer = mock(YouTubePlayer.class);
        subject.onInitializedListener.onInitializationSuccess(null, mockYouTubePlayer, true);

        verify(mockYouTubePlayer).cueVideo(eq("abcYouTubeId"));
    }
}