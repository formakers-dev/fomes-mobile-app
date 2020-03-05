package com.formakers.fomes.common.repository.dao;

import android.text.TextUtils;

import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.repository.model.UserRealmObject;
import com.formakers.fomes.common.util.Log;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.exceptions.RealmException;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class UserDAO {

    public static final String TAG = UserDAO.class.getSimpleName();

    private Gson gson = new Gson();
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public UserDAO(SharedPreferencesHelper sharedPreferencesHelper) {
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    public void updateUserInfo(User userInfo) {
        Log.d(TAG, "setUserInfo");
        try (Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction(realm -> {
                UserRealmObject userRealmObject = realm.where(UserRealmObject.class).findFirst();

//                realm.createObjectFromJson(UserRealmObject.class, gson.toJson(userInfo));

                if (userRealmObject == null) {
                    userRealmObject = new UserRealmObject();
                }

                if (!TextUtils.isEmpty(userInfo.getName())) {
                    userRealmObject.setName(userInfo.getName());
                }

                if (!TextUtils.isEmpty(userInfo.getNickName())) {
                    this.sharedPreferencesHelper.setUserNickName(userInfo.getNickName());
                    userRealmObject.setNickName(userInfo.getNickName());
                }

                if (!TextUtils.isEmpty(userInfo.getEmail())) {
                    this.sharedPreferencesHelper.setUserEmail(userInfo.getEmail());
                    userRealmObject.setEmail(userInfo.getEmail());
                }

                Integer birthday = userInfo.getBirthday();
                if (birthday != null && birthday > 0) {
                    userRealmObject.setBirthday(userInfo.getBirthday());
                }

                Integer job = userInfo.getJob();
                if (job != null && job > 0) {
                    userRealmObject.setJob(job);
                }

                if (!TextUtils.isEmpty(userInfo.getGender())) {
                    userRealmObject.setGender(userInfo.getGender());
                }

                if (userInfo.getLifeApps() != null && userInfo.getLifeApps().size() > 0) {
                    RealmList<String> lifeApps = new RealmList<>();
                    lifeApps.addAll(userInfo.getLifeApps());
                    userRealmObject.setLifeApps(lifeApps);
                }

                Log.d(TAG, "[Realm] Update UserInfo=" + userRealmObject);
                realm.copyToRealmOrUpdate(userRealmObject);
            });
        } catch (RealmException e) {
            Log.e(TAG, String.valueOf(e) + "\n" + e.getCause());
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e) + "\n" + e.getCause());
        }
    }

    public Single<User> getUserInfo() {
        Single<User> userSingle = Single.create(emitter -> {
            try (Realm realmInstance = Realm.getDefaultInstance()) {
                UserRealmObject userRealmObject = realmInstance.where(UserRealmObject.class).findFirst();
                User user = gson.fromJson(gson.toJson(realmInstance.copyFromRealm(userRealmObject)), User.class);
                Log.d(TAG, "[Realm] Get UserInfo=" + user);

                emitter.onSuccess(user);
            } catch (RealmException e) {
                Log.e(TAG, String.valueOf(e) + "\n" + e.getCause());
                emitter.onError(e);
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e) + "\n" + e.getCause());
                emitter.onError(e);
            }
        });

        return userSingle.subscribeOn(Schedulers.io());
    }
}
