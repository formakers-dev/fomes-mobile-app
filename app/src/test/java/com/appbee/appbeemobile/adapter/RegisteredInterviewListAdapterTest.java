package com.appbee.appbeemobile.adapter;

import android.widget.LinearLayout;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.holder.RegisteredInterviewItemViewHolder;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RegisteredInterviewListAdapterTest {

    private RegisteredInterviewListAdapter subject;

    @Inject
    TimeHelper mockTimeHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);

        final int DECEMBER = 11, JANUARY = 0;

        Date mockToday = createMockDate(2017, DECEMBER, 29);
        when(mockTimeHelper.getCurrentTime()).thenReturn(mockToday.getTime());

        Date interviewDate = createMockDate(2018, JANUARY, 12);
        Date openDate = createMockDate(2017, DECEMBER, 1);
        Date closeDate = createMockDate(2017, DECEMBER, 31);

        Project.Person owner = new Project.Person("김앱비", null, "");
        Project.Interview interview = new Project.Interview(0, null, interviewDate, openDate, closeDate, "우면사업장", "C동 1층 107호 회의실", 0, null, "time15", "010-1234-5678", "오프라인");

        Project project = new Project("1", "툰스토리", "", null, "", null, owner, "", interview);
        List<Project> projectList = new ArrayList<>();
        projectList.add(project);

        subject = new RegisteredInterviewListAdapter(projectList, mockTimeHelper);
    }

    private Date createMockDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    @Test
    public void 데이터가_바인딩되면_프로젝트정보를_뷰홀더에_표시한다() throws Exception {
        RegisteredInterviewItemViewHolder holder = subject.onCreateViewHolder(new LinearLayout(RuntimeEnvironment.application), 0);

        subject.onBindViewHolder(holder, 0);

        assertThat(holder.interviewNameTextView.getText()).isEqualTo("툰스토리 유저 인터뷰");
        assertThat(holder.interviewDDayTextView.getText()).isEqualTo("D-14");
        assertThat(holder.interviewDateLocationTextView.getText()).isEqualTo("1월 12일 (금) 우면사업장 15:00");
        assertThat(holder.interviewOpenDateTextView.getText()).isEqualTo("신청\n17.12.1 (금)");
        assertThat(holder.interviewCloseDateTextView.getText()).isEqualTo("확정\n17.12.31 (일)");
        assertThat(holder.interviewDateTextView.getText()).isEqualTo("완료\n18.1.12 (금)");
        assertThat(holder.interviewLocation.getText()).isEqualTo("* 인터뷰 위치 : 우면사업장 C동 1층 107호 회의실");
        assertThat(holder.emergencyPhone.getText()).isEqualTo("* 비상연락처 : 010-1234-5678");
    }

    //TODO: D-Day 경과 시 미표시, 취소하기처리, 버튼 처리, 프로젝트 설명 다시보기 처리
}