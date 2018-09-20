package com.formakers.fomes.settings.model;

public class SettingsItem {
    int id;
    String title;
    String sideInfo;

    private SettingsItem(int id, String title, String sideInfo) {
        this.id = id;
        this.title = title;
        this.sideInfo = sideInfo;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSideInfo() {
        return sideInfo;
    }

    public static class Builder {
        int id;
        String title;
        String sideInfo;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSideInfo(String sideInfo) {
            this.sideInfo = sideInfo;
            return this;
        }

        public SettingsItem build() {
            return new SettingsItem(this.id, this.title, this.sideInfo);
        }
    }
}