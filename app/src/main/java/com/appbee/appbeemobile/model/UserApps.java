package com.appbee.appbeemobile.model;

import java.util.List;

public class UserApps {
    private String userId;
    private List<AppInfo> apps;

    public UserApps(String userId, List<AppInfo> apps) {
        this.userId = userId;
        this.apps = apps;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<AppInfo> getApps() {
        return apps;
    }
    public void setApps(List<AppInfo> apps) {
        this.apps = apps;
    }
}
