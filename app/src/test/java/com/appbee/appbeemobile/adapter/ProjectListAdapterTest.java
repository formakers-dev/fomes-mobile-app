package com.appbee.appbeemobile.adapter;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
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

import static com.appbee.appbeemobile.adapter.ProjectListAdapter.HEADER_VIEW_TYPE;
import static com.appbee.appbeemobile.adapter.ProjectListAdapter.ITEM_VIEW_TYPE;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectListAdapterTest {
    private ProjectListAdapter subject;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", "temporary"));
        mockProjectList.add(new Project("projectId2", "유어커스텀2", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", "temporary"));
        mockProjectList.add(new Project("projectId3", "유어커스텀3", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", "temporary"));

        subject = new ProjectListAdapter(mockProjectList);
    }

    @Test
    public void Adapter를_생성하면_입력한데이터가_바인딩된다() throws Exception {
        assertThat(subject.getItemCount()).isEqualTo(4);
        assertThat(subject.getItemViewType(0)).isEqualTo(HEADER_VIEW_TYPE);
        assertThat(subject.getItemViewType(1)).isEqualTo(ITEM_VIEW_TYPE);
        assertThat(subject.getItemViewType(2)).isEqualTo(ITEM_VIEW_TYPE);
        assertThat(subject.getItemViewType(3)).isEqualTo(ITEM_VIEW_TYPE);
        assertThat(subject.getItem(0).getProjectId()).isEqualTo("projectId1");
        assertThat(subject.getItem(0).getName()).isEqualTo("유어커스텀");
        assertThat(subject.getItem(0).getIntroduce()).isEqualTo("[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!");
        assertThat(subject.getItem(0).getStatus()).isEqualTo("temporary");
    }
}