package com.formakers.fomes.common.network.vo;

import com.formakers.fomes.model.AppInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecommendApp {

    public static final int RECOMMEND_TYPE_FAVORITE_APP = 1;
    public static final int RECOMMEND_TYPE_FAVORITE_DEVELOPER = 2;
    public static final int RECOMMEND_TYPE_FAVORITE_CATEGORY = 3;
    public static final int RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC = 4;

    int recommendType;
    List<String> criteria;
    int rank;
    @SerializedName("app") AppInfo appInfo;

    public RecommendApp() {
    }

    public int getRecommendType() {
        return recommendType;
    }

    public RecommendApp setRecommendType(int recommendType) {
        this.recommendType = recommendType;
        return this;
    }

    public List<String> getCriteria() {
        return criteria;
    }

    public RecommendApp setCriteria(List<String> criteria) {
        this.criteria = criteria;
        return this;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public RecommendApp setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
        return this;
    }

    @Override
    public String toString() {
        return "RecommendApp{" +
                "recommendType=" + recommendType +
                ", criteria=" + criteria +
                ", rank=" + rank +
                ", appInfo=" + appInfo +
                '}';
    }
}
