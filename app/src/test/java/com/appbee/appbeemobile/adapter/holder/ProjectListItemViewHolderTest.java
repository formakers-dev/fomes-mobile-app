package com.appbee.appbeemobile.adapter.holder;

import android.app.Application;
import android.view.LayoutInflater;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.ProjectDetailActivity;
import com.appbee.appbeemobile.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectListItemViewHolderTest {
    private ProjectListItemViewHolder subject;

    @Before
    public void setUp() throws Exception {
        subject = new ProjectListItemViewHolder(LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.interview_item_card, null), RuntimeEnvironment.application);

        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered");

        subject.bind(project);
    }

    @Test
    public void ItemViewHolder를_생성하면_프로젝트정보를_셋팅한다() throws Exception {
        assertThat(subject.projectId).isEqualTo("projectId");
        assertThat(subject.nameTextView.getText()).isEqualTo("릴루미노");
        assertThat(subject.introduceTextView.getText()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");
//        assertThat(subject.projectDescriptionTextView.getText()).isEqualTo("[지그재그] 앱을 사용하시는 분의 의견을 구합니다.");
    }

    @Test
    public void ItemView를_클릭하면_DetailActivity로_이동한다() throws Exception {
        subject.mView.performClick();
        assertThat(shadowOf(((Application) subject.context)).getNextStartedActivity().getComponent().getClassName()).contains(ProjectDetailActivity.class.getSimpleName());
    }
}