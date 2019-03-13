package com.formakers.fomes.model;

import android.support.annotation.StringRes;

import com.formakers.fomes.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User {
    public enum JobCategory {
        MANAGER("관리자", 1000, true),

        PROFESSIONAL("전문가", 2000, true),
        IT("IT 종사자", PROFESSIONAL.getCode() + 1, true),

        OFFICE_WORK("사무 종사자", 3000, true),
        SERVICE("서비스 종사자", 4000, true),

        STUDENT("학생", 5000, false),
        ELEMENTARY_STUDENT("초등학생", STUDENT.getCode() + 3, true),
        MIDDLE_AND_HIGH_SCHOOL_STUDENT("중고등학생", STUDENT.getCode() + 1, true),
        UNIVERSITY_STUDENT("대학생", STUDENT.getCode() + 2, true),

        SALES("판매 종사자", 6000, true),
        AGRICULTURE_FORESTRY_FISHERY("농림/어업 숙련 종사자", 7000, true),
        TECHNICIAN("기능원 및 관련 기능 종사자", 8000, true),
        OPERATOR("장치/기계 조작 및 조립 종사자", 9000, true),
        SIMPLE_WORK("단순노무 종사자", 10000, true),
        MILITARY("군인", 11000, true),

        ETC("기타", 12000, false),
        HOMEMAKER("주부", ETC.getCode() + 1, true),
        INOCCUPATION("무직", ETC.getCode() + 2, true);

        final private String name;
        final private int code;
        final private boolean isSelectable;

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }

        public boolean getSelectable() {
            return isSelectable;
        }

        JobCategory(String name, int code, boolean isSelectable) {
            this.name = name;
            this.code = code;
            this.isSelectable = isSelectable;
        }

        private static Map<Integer, JobCategory> jobCategoryMap = new HashMap<>();

        public static JobCategory get(Integer code) {
            if (jobCategoryMap.size() == 0) {
                for (User.JobCategory job : User.JobCategory.values())
                    jobCategoryMap.put(job.getCode(), job);
            }

            return jobCategoryMap.get(code);
        }

        public static JobCategory get(String name) {
            for (JobCategory job : JobCategory.values())
                if (job.getName().equals(name))
                    return job;
            return null;
        }
    }

    private String name;
    private String nickName;
    private String email;
    private Integer birthday;
    private Integer job;
    private String gender;
    private String registrationToken;
    private List<String> lifeApps;
    private String appVersion;

    public User() {
    }

    public User(String email, String registrationToken) {
        this.email = email;
        this.registrationToken = registrationToken;
    }

    public User(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public Integer getBirthday() {
        return birthday;
    }

    public User setBirthday(Integer birthday) {
        this.birthday = birthday;
        return this;
    }

    public Integer getJob() {
        return job;
    }

    public User setJob(Integer job) {
        this.job = job;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public User setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public User setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
        return this;
    }

    public List<String> getLifeApps() {
        return lifeApps;
    }

    public User setLifeApps(List<String> lifeApps) {
        this.lifeApps = lifeApps;
        return this;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public User setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public int getAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return ((currentYear - this.birthday) / 10) * 10;
    }

    public @StringRes int getGenderToStringResId() {
        return "male".equals(this.gender) ? R.string.common_male : R.string.common_female;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getName(), user.getName()) &&
                Objects.equals(getNickName(), user.getNickName()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                Objects.equals(getBirthday(), user.getBirthday()) &&
                Objects.equals(getJob(), user.getJob()) &&
                Objects.equals(getGender(), user.getGender()) &&
                Objects.equals(getRegistrationToken(), user.getRegistrationToken()) &&
                Objects.equals(getLifeApps(), user.getLifeApps());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getNickName(), getEmail(), getBirthday(), getJob(), getGender(), getRegistrationToken(), getLifeApps());
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", job='" + job + '\'' +
                ", gender='" + gender + '\'' +
                ", registrationToken='" + registrationToken + '\'' +
                ", lifeApps=" + lifeApps +
                '}';
    }
}
