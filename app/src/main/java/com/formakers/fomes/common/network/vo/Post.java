package com.formakers.fomes.common.network.vo;

public class Post {
    Integer index;
    String overviewImageUrl;
    String contents;

    public Integer getIndex() {
        return index;
    }

    public Post setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public String getOverviewImageUrl() {
        return overviewImageUrl;
    }

    public Post setOverviewImageUrl(String overviewImageUrl) {
        this.overviewImageUrl = overviewImageUrl;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Post setContents(String contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public String toString() {
        return "Post{" +
                "index=" + index +
                ", overviewImageUrl='" + overviewImageUrl + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
