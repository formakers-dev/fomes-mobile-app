package com.formakers.fomes.settings.model;

public class SettingsItem {
    int id;
    String title;
    String sideInfo;
    boolean isClickable;

    private SettingsItem(int id, String title, String sideInfo, boolean isClickable) {
        this.id = id;
        this.title = title;
        this.sideInfo = sideInfo;
        this.isClickable = isClickable;
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

    public boolean isClickable() {
        return isClickable;
    }

    public static class Builder {
        int id;
        String title;
        String sideInfo;
        boolean isClickable = true;

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

        public Builder setClickable(boolean clickable) {
            isClickable = clickable;
            return this;
        }

        public SettingsItem build() {
            return new SettingsItem(this.id, this.title, this.sideInfo, this.isClickable);
        }
    }
}