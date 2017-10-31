package com.appbee.appbeemobile.adapter;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecommendationAppsAdapterTest {
    private RecommendationAppsAdapter subject;
    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;

    @Before
    public void setUp() throws Exception {
        subject = new RecommendationAppsAdapter(RuntimeEnvironment.application);

        List<Project> mockRecommendationProjectList = new ArrayList<>();
        mockRecommendationProjectList.add(new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_1"), Collections.singletonList("지그재그"), 0));
        mockRecommendationProjectList.add(new Project("projectId2", "유어커스텀2", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_2"), Collections.singletonList("지그재그2"), 0));
        mockRecommendationProjectList.add(new Project("projectId3", "유어커스텀3", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_3"), Collections.singletonList("지그재그3"), 0));

        subject.setProjectList(mockRecommendationProjectList);
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
        assertThat(subject.getItem(0).getImages().get(0)).isEqualTo("image_path_1");
        assertThat(subject.getItem(0).getApps().get(0)).isEqualTo("지그재그");
        assertThat(subject.getItem(0).getStatus()).isEqualTo(0);
    }
}