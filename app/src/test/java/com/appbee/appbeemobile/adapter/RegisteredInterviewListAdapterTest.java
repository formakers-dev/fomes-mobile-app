package com.appbee.appbeemobile.adapter;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.activity.MyInterviewActivity;
import com.appbee.appbeemobile.adapter.holder.RegisteredInterviewItemViewHolder;
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
    private MyInterviewActivity.ActionListener mockListener;

    private RegisteredInterviewItemViewHolder holder;

    private Date mockInterviewDate1;
    private Date mockInterviewDate2;
    private int skyBlueColorId;
    private int lightGrayColorId;

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
        Project project1 = new Project("12345", "툰스토리", "", null, "", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", null, owner, "", interview1);

        mockInterviewDate2 = createMockDate(2017, DECEMBER, 30);

        Project.Interview interview2 = new Project.Interview(22L, null, "인터뷰소개", mockInterviewDate2, createMockDate(2017, DECEMBER, 27), createMockDate(2017, DECEMBER, 28), "수원사업장", "5층 회의실", 5, null, "time8", "010-1111-2222", "온라인");
        Project project2 = new Project("67890", "토토", "", null, "", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", null, owner, "", interview2);

        List<Project> projectList = new ArrayList<>();
        projectList.add(project1);
        projectList.add(project2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            skyBlueColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_sky_blue, null);
            lightGrayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_light_gray, null);
        } else {
            skyBlueColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_sky_blue);
            lightGrayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_light_gray);
        }

        subject = new RegisteredInterviewListAdapter(projectList, mockTimeHelper, mockListener);
        holder = subject.onCreateViewHolder(new LinearLayout(RuntimeEnvironment.application), 0);
    }

    private Date createMockDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    @Test
    public void 데이터가_바인딩되면_프로젝트정보를_뷰홀더에_표시한다() throws Exception {
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

    @Test
    @Config(minSdk = 22)
    public void 인터뷰신청상태_데이터가_바인딩되면_인터뷰상태를_표시한다() throws Exception {
        // 신청상태 : openDate < 현재일자 < closeDate
        //  12.01 < 2017.12.29 < 12.31
        subject.onBindViewHolder(holder, 0);
        assertThat(holder.interviewOpenDateTextView.getCurrentTextColor()).isEqualTo(skyBlueColorId);
        assertThat(((ColorDrawable) holder.lineBetweenOpenCloseDateView.getBackground()).getColor()).isEqualTo(lightGrayColorId);
        assertThat(holder.interviewCloseDateTextView.getCurrentTextColor()).isEqualTo(lightGrayColorId);
        assertThat(((ColorDrawable) holder.lineBetweenCloseInterviewDateView.getBackground()).getColor()).isEqualTo(lightGrayColorId);
        assertThat(holder.interviewDateTextView.getCurrentTextColor()).isEqualTo(lightGrayColorId);
    }

    @Test
    @Config(minSdk = 22)
    public void 인터뷰확정상태_데이터가_바인딩되면_인터뷰상태를_표시한다() throws Exception {
        // 확정상태 : closeDate < 현재일자 < interviewDate + 인터뷰시간
        //  12.28 < 12.29 < 12.30 08:00
        subject.onBindViewHolder(holder, 1);
        assertThat(holder.interviewOpenDateTextView.getCurrentTextColor()).isEqualTo(skyBlueColorId);
        assertThat(((ColorDrawable) holder.lineBetweenOpenCloseDateView.getBackground()).getColor()).isEqualTo(skyBlueColorId);
        assertThat(holder.interviewCloseDateTextView.getCurrentTextColor()).isEqualTo(skyBlueColorId);
        assertThat(((ColorDrawable) holder.lineBetweenCloseInterviewDateView.getBackground()).getColor()).isEqualTo(lightGrayColorId);
        assertThat(holder.interviewDateTextView.getCurrentTextColor()).isEqualTo(lightGrayColorId);
    }

    @Test
    public void 프로젝트_설명_다시보기_버튼을_클릭하면_ActionListener의_onSelectProject에_프로젝트ID를_전달한다() throws Exception {
        subject.onBindViewHolder(holder, 0);
        holder.showInterviewButton.performClick();
        verify(mockListener).onSelectProject(eq("12345"));

        subject.onBindViewHolder(holder, 1);
        holder.showInterviewButton.performClick();
        verify(mockListener).onSelectProject(eq("67890"));
    }

    @Test
    public void 취소하기_버튼을_클릭하면_ActionListener의_onRequestToCancelInterview에_프로젝트ID를_전달한다() throws Exception {
        subject.onBindViewHolder(holder, 0);
        holder.cancelInterviewButton.performClick();
        verify(mockListener).onRequestToCancelInterview(eq("12345"), eq(11L), eq("time15"), eq("툰스토리"), eq("신청"), eq(mockInterviewDate1), eq("우면사업장"));

        subject.onBindViewHolder(holder, 1);
        holder.cancelInterviewButton.performClick();
        verify(mockListener).onRequestToCancelInterview(eq("67890"), eq(22L), eq("time8"), eq("토토"), eq("확정"), eq(mockInterviewDate2), eq("수원사업장"));
    }
}