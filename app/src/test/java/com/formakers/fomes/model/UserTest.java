package com.formakers.fomes.model;

import com.formakers.fomes.R;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UserTest {

    User subject;

    @Before
    public void setUp() throws Exception {
        subject = new User();
        subject.setBirthday(1991);
        subject.setGender("female");
    }

    @Test
    public void getAge_호출시__나이대를_반환한다() {
        int age = subject.getAge();

        assertThat(age).isEqualTo(20);
    }

    @Test
    public void getGenderToStringResId_호출시__성별_문자_리소스_아이디를_반환한다() {
        int stringResId = subject.getGenderToStringResId();

        assertThat(stringResId).isEqualTo(R.string.common_female);
    }
}