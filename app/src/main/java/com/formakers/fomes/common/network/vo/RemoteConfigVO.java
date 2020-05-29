package com.formakers.fomes.common.network.vo;

import java.util.ArrayList;

public class RemoteConfigVO {
    public static class MigrationNotice {
        String title;
        String description;
        ArrayList<String> descriptionImages;
        String guide;
        int versionCode;
        int noticeVersion;

        public MigrationNotice() {
        }

        public String getTitle() {
            return title;
        }

        public MigrationNotice setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public MigrationNotice setDescription(String description) {
            this.description = description;
            return this;
        }

        public ArrayList<String> getDescriptionImages() {
            return descriptionImages;
        }

        public MigrationNotice setDescriptionImages(ArrayList<String> descriptionImages) {
            this.descriptionImages = descriptionImages;
            return this;
        }

        public String getGuide() {
            return guide;
        }

        public MigrationNotice setGuide(String guide) {
            this.guide = guide;
            return this;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public MigrationNotice setVersionCode(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public int getNoticeVersion() {
            return noticeVersion;
        }

        public MigrationNotice setNoticeVersion(int noticeVersion) {
            this.noticeVersion = noticeVersion;
            return this;
        }

        @Override
        public String toString() {
            return "MigrationNotice{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", descriptionImages=" + descriptionImages +
                    ", guide='" + guide + '\'' +
                    ", versionCode=" + versionCode +
                    ", noticeVersion=" + noticeVersion +
                    '}';
        }
    }
}
