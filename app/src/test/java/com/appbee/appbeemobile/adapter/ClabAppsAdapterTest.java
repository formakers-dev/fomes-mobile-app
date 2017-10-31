package com.appbee.appbeemobile.adapter;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.model.Project;
import com.google.common.collect.Lists;

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
public class ClabAppsAdapterTest {

    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ITEM_VIEW_TYPE = 1;
    private ClabAppsAdapter subject;

    @Before
    public void setUp() throws Exception {
        subject = new ClabAppsAdapter(RuntimeEnvironment.application);

        List<Project> mockClabProjectList = new ArrayList<>();
        mockClabProjectList.add(new Project("projectId4", "리얼포토", "증강현실로 한장의 사진에 담는 나만의 추억", Collections.singletonList("image_path_1"), Lists.newArrayList("Foodie", "Viva video"), 0));
        mockClabProjectList.add(new Project("projectId5", "엔빵", "모임별로 엔빵해", Collections.singletonList("image_path_2"), Lists.newArrayList("카카오뱅크", "토스"), 0));
        mockClabProjectList.add(new Project("projectId6", "겜돌이", "게임하자", Collections.singletonList("image_path_3"), Lists.newArrayList("클래시로얄", "리니지"), 0));

        subject.setProjectList(mockClabProjectList);
    }

    @Test
    public void clabAppsAdapter생성하면_입력한데이터가_바인딩된다() throws Exception {
        assertThat(subject.getItemCount()).isEqualTo(4);
        assertThat(subject.getItemViewType(0)).isEqualTo(HEADER_VIEW_TYPE);
        assertThat(subject.getItemViewType(1)).isEqualTo(ITEM_VIEW_TYPE);
        assertThat(subject.getItemViewType(2)).isEqualTo(ITEM_VIEW_TYPE);
        assertThat(subject.getItemViewType(3)).isEqualTo(ITEM_VIEW_TYPE);

        assertThat(subject.getItem(0).getProjectId()).isEqualTo("projectId4");
        assertThat(subject.getItem(0).getName()).isEqualTo("리얼포토");
        assertThat(subject.getItem(0).getIntroduce()).isEqualTo("증강현실로 한장의 사진에 담는 나만의 추억");
        assertThat(subject.getItem(0).getImages().get(0)).isEqualTo("image_path_1");
        assertThat(subject.getItem(0).getApps().get(0)).isEqualTo("Foodie");
        assertThat(subject.getItem(0).getStatus()).isEqualTo(0);
    }
}