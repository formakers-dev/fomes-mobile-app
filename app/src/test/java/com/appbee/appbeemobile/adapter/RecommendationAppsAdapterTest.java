package com.appbee.appbeemobile.adapter;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static com.appbee.appbeemobile.adapter.RecommendationAppsAdapter.HEADER_VIEW_TYPE;
import static com.appbee.appbeemobile.adapter.RecommendationAppsAdapter.ITEM_VIEW_TYPE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecommendationAppsAdapterTest {
    private RecommendationAppsAdapter subject;

    @Inject
    ProjectService mockProjectService;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        List<Project> mockRecommendationProjectList = new ArrayList<>();
        mockRecommendationProjectList.add(new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("지그재그"), 0));
        mockRecommendationProjectList.add(new Project("projectId2", "유어커스텀2", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!",Collections.singletonList("지그재그2"), 0));
        mockRecommendationProjectList.add(new Project("projectId3", "유어커스텀3", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("지그재그3"), 0));
        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(mockRecommendationProjectList));

        subject = new RecommendationAppsAdapter(RuntimeEnvironment.application, mockProjectService);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void RecommendationAppsAdapter생성하면_입력한데이터가_바인딩된다() throws Exception {
        assertThat(subject.getItemCount()).isEqualTo(4);
        assertThat(subject.getItemViewType(0)).isEqualTo(HEADER_VIEW_TYPE);
        assertThat(subject.getItemViewType(1)).isEqualTo(ITEM_VIEW_TYPE);
        assertThat(subject.getItemViewType(2)).isEqualTo(ITEM_VIEW_TYPE);
        assertThat(subject.getItemViewType(3)).isEqualTo(ITEM_VIEW_TYPE);

        assertThat(subject.getItem(0).getProjectId()).isEqualTo("projectId1");
        assertThat(subject.getItem(0).getName()).isEqualTo("유어커스텀");
        assertThat(subject.getItem(0).getIntroduce()).isEqualTo("[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!");
        assertThat(subject.getItem(0).getApps().get(0)).isEqualTo("지그재그");
        assertThat(subject.getItem(0).getStatus()).isEqualTo(0);
    }
}