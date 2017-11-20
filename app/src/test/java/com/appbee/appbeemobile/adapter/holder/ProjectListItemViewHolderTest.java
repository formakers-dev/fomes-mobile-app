package com.appbee.appbeemobile.adapter.holder;

import android.app.Application;
import android.view.LayoutInflater;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.DetailActivity;
import com.appbee.appbeemobile.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectListItemViewHolderTest {
    private ProjectListItemViewHolder subject;

    @Before
    public void setUp() throws Exception {
        subject = new ProjectListItemViewHolder(LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.item_card, null), RuntimeEnvironment.application);
        subject.bind(new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", "temporary"));
    }

    @Test
    public void ItemViewHolder를_생성하면_프로젝트정보를_셋팅한다() throws Exception {
        assertThat(subject.projectId).isEqualTo("projectId1");
        assertThat(subject.nameTextView.getText()).isEqualTo("유어커스텀");
        assertThat(subject.introduceTextView.getText()).isEqualTo("[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!");
//        assertThat(subject.itemCardTagTextView.getText()).isEqualTo("[지그재그] 앱을 사용하시는 분의 의견을 구합니다.");
    }

    @Test
    public void ItemView를_클릭하면_DetailActivity로_이동한다() throws Exception {
        subject.mView.performClick();
        assertThat(shadowOf(((Application) subject.context)).getNextStartedActivity().getComponent().getClassName()).contains(DetailActivity.class.getSimpleName());
    }
}