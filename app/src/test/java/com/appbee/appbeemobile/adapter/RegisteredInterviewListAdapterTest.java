package com.appbee.appbeemobile.adapter;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.MyInterviewActivity;
import com.appbee.appbeemobile.adapter.holder.RegisteredInterviewItemViewHolder;
import com.appbee.appbeemobile.helper.ResourceHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class RegisteredInterviewListAdapterTest {
    private static final int DECEMBER = 11, JANUARY = 0;
    private RegisteredInterviewListAdapter subject;

    @Mock
    private TimeHelper mockTimeHelper;

    @Mock
    private ResourceHelper mockResourceHelper;

    @Mock
    private MyInterviewActivity.OnItemClickListener mockListener;

    private RegisteredInterviewItemViewHolder holder;

    private Date mockInterviewDate1;
    private Date mockInterviewDate2;
    private final int SKY_BLUE_COLOR = 1;
    private final int LIGHT_GRAY_COLOR = 2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Date mockToday = createMockDate(2017, DECEMBER, 29);
        when(mockTimeHelper.getCurrentTime()).thenReturn(mockToday.getTime());

        mockInterviewDate1 = createMockDate(2018, JANUARY, 12);
        Date openDate = createMockDate(2017, DECEMBER, 1);
        Date closeDate = createMockDate(2017, DECEMBER, 31);

        Project.Person owner = new Project.Person("김앱비", null, "");

        Project.Interview interview1 = new Project.Interview(11L, null, "인터뷰소개", mockInterviewDate1, openDate, closeDate, "우면사업장", "C동 1층 107호 회의실", 0, null, "time15", "010-1234-5678", "오프라인");
        Project project1 = new Project("12345", "툰스토리", "", null, "", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", null, owner, "");
        project1.setInterview(interview1);

        mockInterviewDate2 = createMockDate(2017, DECEMBER, 30);

        Project.Interview interview2 = new Project.Interview(22L, null, "인터뷰소개", mockInterviewDate2, createMockDate(2017, DECEMBER, 27), createMockDate(2017, DECEMBER, 28), "수원사업장", "5층 회의실", 5, null, "time8", "010-1111-2222", "온라인");
        Project project2 = new Project("67890", "토토", "", null, "", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", null, owner, "");
        project2.setInterview(interview2);

        List<Project> projectList = new ArrayList<>();
        projectList.add(project1);
        projectList.add(project2);

        mockColorValue();

        subject = new RegisteredInterviewListAdapter(projectList, mockTimeHelper, mockListener, mockResourceHelper);
        holder = subject.onCreateViewHolder(new LinearLayout(RuntimeEnvironment.application), 0);
    }

    @Test
    public void 데이터가_바인딩되면_프로젝트정보를_뷰홀더에_표시한다() throws Exception {
        subject.onBindViewHolder(holder, 0);

        assertThat(holder.interviewNameTextView.getText()).isEqualTo("툰스토리 유저 인터뷰");
        assertThat(holder.interviewDDayTextView.getText()).isEqualTo("D-14");
        assertThat(holder.interviewDateLocationTextView.getText()).isEqualTo("1월 12일 (금) 우면사업장 15:00");
        assertThat(holder.interviewOpenDateTextView.getText()).isEqualTo("17.12.1 (금)");
        assertThat(holder.interviewCloseDateTextView.getText()).isEqualTo("17.12.31 (일)");
        assertThat(holder.interviewDateTextView.getText()).isEqualTo("18.1.12 (금)");
        assertThat(holder.interviewLocation.getText()).isEqualTo("* 인터뷰 위치 : 우면사업장 C동 1층 107호 회의실");
        assertThat(holder.emergencyPhone.getText()).isEqualTo("* 비상연락처 : 010-1234-5678");
    }

    @Test
    @Config(minSdk = 22)
    public void 인터뷰신청상태_데이터가_바인딩되면_인터뷰상태를_표시한다() throws Exception {
        // 신청상태 : openDate < 현재일자 < closeDate
        //  12.01 < 2017.12.29 < 12.31
        subject.onBindViewHolder(holder, 0);
        assertThat(holder.interviewOpenDateTextView.getCurrentTextColor()).isEqualTo(SKY_BLUE_COLOR);
        assertThat(holder.interviewOpenTitleTextView.getCurrentTextColor()).isEqualTo(SKY_BLUE_COLOR);
        assertThat(((ColorDrawable) holder.lineBetweenOpenCloseDateView.getBackground()).getColor()).isEqualTo(LIGHT_GRAY_COLOR);
        assertThat(holder.interviewCloseDateTextView.getCurrentTextColor()).isEqualTo(LIGHT_GRAY_COLOR);
        assertThat(holder.interviewCloseTitleTextView.getCurrentTextColor()).isEqualTo(LIGHT_GRAY_COLOR);
        assertThat(((ColorDrawable) holder.lineBetweenCloseInterviewDateView.getBackground()).getColor()).isEqualTo(LIGHT_GRAY_COLOR);
        assertThat(holder.interviewDateTextView.getCurrentTextColor()).isEqualTo(LIGHT_GRAY_COLOR);
        assertThat(holder.interviewDateTitleTextView.getCurrentTextColor()).isEqualTo(LIGHT_GRAY_COLOR);
    }

    @Test
    @Config(minSdk = 22)
    public void 인터뷰확정상태_데이터가_바인딩되면_인터뷰상태를_표시한다() throws Exception {
        // 확정상태 : closeDate < 현재일자 < interviewDate + 인터뷰시간
        //  12.28 < 12.29 < 12.30 08:00
        subject.onBindViewHolder(holder, 1);
        assertThat(holder.interviewOpenDateTextView.getCurrentTextColor()).isEqualTo(SKY_BLUE_COLOR);
        assertThat(holder.interviewOpenTitleTextView.getCurrentTextColor()).isEqualTo(SKY_BLUE_COLOR);
        assertThat(((ColorDrawable) holder.lineBetweenOpenCloseDateView.getBackground()).getColor()).isEqualTo(SKY_BLUE_COLOR);
        assertThat(holder.interviewCloseDateTextView.getCurrentTextColor()).isEqualTo(SKY_BLUE_COLOR);
        assertThat(holder.interviewCloseTitleTextView.getCurrentTextColor()).isEqualTo(SKY_BLUE_COLOR);
        assertThat(((ColorDrawable) holder.lineBetweenCloseInterviewDateView.getBackground()).getColor()).isEqualTo(LIGHT_GRAY_COLOR);
        assertThat(holder.interviewDateTextView.getCurrentTextColor()).isEqualTo(LIGHT_GRAY_COLOR);
        assertThat(holder.interviewDateTitleTextView.getCurrentTextColor()).isEqualTo(LIGHT_GRAY_COLOR);
    }

    @Test
    public void 프로젝트_설명_다시보기_버튼을_클릭하면_OnItemClickListener의_onClickInterviewDetail에_프로젝트ID를_전달한다() throws Exception {
        subject.onBindViewHolder(holder, 0);
        holder.showInterviewButton.performClick();
        verify(mockListener).onClickInterviewDetail(eq("12345"), eq(11L));

        subject.onBindViewHolder(holder, 1);
        holder.showInterviewButton.performClick();
        verify(mockListener).onClickInterviewDetail(eq("67890"), eq(22L));
    }

    @Test
    public void 취소하기_버튼을_클릭하면_OnItemClickListener의_onClickCancelInterview에_프로젝트ID를_전달한다() throws Exception {
        subject.onBindViewHolder(holder, 0);
        holder.cancelInterviewButton.performClick();
        verify(mockListener).onClickCancelInterview(eq("12345"), eq(11L), eq("time15"), eq("툰스토리"), eq("신청"), eq(mockInterviewDate1), eq("우면사업장"));

        subject.onBindViewHolder(holder, 1);
        holder.cancelInterviewButton.performClick();
        verify(mockListener).onClickCancelInterview(eq("67890"), eq(22L), eq("time8"), eq("토토"), eq("확정"), eq(mockInterviewDate2), eq("수원사업장"));
    }


    private Date createMockDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    private void mockColorValue() {
        when(mockResourceHelper.getColorValue(R.color.appbee_sky_blue)).thenReturn(SKY_BLUE_COLOR);
        when(mockResourceHelper.getColorValue(R.color.appbee_light_gray)).thenReturn(LIGHT_GRAY_COLOR);
    }
}