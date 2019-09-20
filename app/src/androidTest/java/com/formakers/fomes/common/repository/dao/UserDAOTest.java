package com.formakers.fomes.common.repository.dao;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.repository.model.UserRealmObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class UserDAOTest {
    private Realm realm;
    private UserDAO subject;

    @Before
    public void setUp() throws Exception {
        String testDBName = "testDB";
        RealmConfiguration config = new RealmConfiguration.Builder().inMemory().name(testDBName).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        subject = new UserDAO();
    }

    @After
    public void tearDown() throws Exception {
        realm.beginTransaction();
        realm.delete(UserRealmObject.class);
        realm.commitTransaction();
        realm.close();
    }

    @Test
    public void updateUserInfo_호출시__유저정보를_업데이트한다() {
        insertDummyData();

        User newUserInfo = new User()
                .setNickName("newNickName")
                .setBirthday(1992)
                .setGender("male")
                .setEmail("new@email.com")
                .setJob(User.JobCategory.IT.getCode());
        subject.updateUserInfo(newUserInfo);

        UserRealmObject user = realm.where(UserRealmObject.class).findFirst();
        assertNotNull(user);
        assertEquals(user.getNickName(), "newNickName");
        assertEquals(user.getBirthday().intValue(), 1992);
        assertEquals(user.getGender(), "male");
        assertEquals(user.getEmail(), "new@email.com");
        assertEquals(user.getJob(), "newJob");
    }

    @Test
    public void updateUserInfo_호출시__셋팅되지않은_유저정보는_업데이트하지_않는다() {
        insertDummyData();

        User newUserInfo = new User()
                .setNickName("newNickName")
                .setJob(User.JobCategory.IT.getCode());

        subject.updateUserInfo(newUserInfo);

        UserRealmObject user = realm.where(UserRealmObject.class).findFirst();
        assertNotNull(user);
        assertEquals(user.getNickName(), "newNickName");
        assertEquals(user.getBirthday().intValue(), 1991);
        assertEquals(user.getGender(), "female");
        assertEquals(user.getEmail(), "test@email.com");
        assertEquals(user.getJob(), "newJob");
    }

    @Test
    public void getUserInfo_호출시__저장된_유저_정보를_리턴한다() {
        insertDummyData();

        User user = subject.getUserInfo().toBlocking().value();

        assertNotNull(user);
        assertEquals(user.getNickName(), "testUserNickName");
        assertEquals(user.getBirthday().intValue(), 1991);
        assertEquals(user.getGender(), "female");
        assertEquals(user.getEmail(), "test@email.com");
        assertEquals(user.getJob(), "testJob");
    }

    private void insertDummyData() {
        realm.executeTransaction((realmInstance) -> {
            UserRealmObject userRealmObject = new UserRealmObject();
            userRealmObject.setNickName("testUserNickName");
            userRealmObject.setBirthday(1991);
            userRealmObject.setGender("female");
            userRealmObject.setEmail("test@email.com");
            userRealmObject.setJob(User.JobCategory.IT.getCode());
            realmInstance.copyToRealmOrUpdate(userRealmObject);
        });
    }
}