package com.appbee.appbeemobile.adapter;

import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainListAdapterTest {

    private MainListAdapter subject;

    @Before
    public void setUp() throws Exception {
        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered"));
        mockProjectList.add(new Project("projectId2", "릴루미노2", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)2", imageObject, "안녕하세요 릴루미노팀입니다.2", imageObjectList, owner, "registered"));


        subject = new MainListAdapter(mockProjectList, MainListAdapter.INTERVIEW_ITEM_VIEW_TYPE);
        subject.setHeaderView(mock(View.class));
    }

    @Test
    public void Adapter를_생성하면_입력한데이터가_바인딩된다() throws Exception {
        assertThat(subject.getItemCount()).isEqualTo(3);

        assertThat(subject.getItemViewType(0)).isEqualTo(MainListAdapter.HEADER_VIEW_TYPE);
        assertThat(subject.getItemViewType(1)).isEqualTo(MainListAdapter.INTERVIEW_ITEM_VIEW_TYPE);
        assertThat(subject.getItemViewType(2)).isEqualTo(MainListAdapter.INTERVIEW_ITEM_VIEW_TYPE);

        assertThat(subject.getItem(1).getProjectId()).isEqualTo("projectId");
        assertThat(subject.getItem(1).getName()).isEqualTo("릴루미노");
        assertThat(subject.getItem(1).getIntroduce()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");
        assertThat(subject.getItem(1).getStatus()).isEqualTo("registered");

        assertThat(subject.getItem(2).getProjectId()).isEqualTo("projectId2");
        assertThat(subject.getItem(2).getName()).isEqualTo("릴루미노2");
        assertThat(subject.getItem(2).getIntroduce()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)2");
        assertThat(subject.getItem(2).getStatus()).isEqualTo("registered");
    }
}