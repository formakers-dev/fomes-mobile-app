package com.formakers.fomes.common.network.vo;

import java.util.Date;

public class Post {
    Integer order;
    Date openDate;
    Date closeDate;
    String title;
    String coverImageUrl;
    // 우선순위 : deeplink > contents
    String contents;
    String deeplink;

    public Integer getOrder() {
        return order;
    }

    public Post setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public Post setOpenDate(Date openDate) {
        this.openDate = openDate;
        return this;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public Post setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
        return this;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public Post setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Post setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public Post setDeeplink(String deeplink) {
        this.deeplink = deeplink;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Post setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String toString() {
        return "Post{" +
                "order=" + order +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", contents='" + contents + '\'' +
                ", deeplink='" + deeplink + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
