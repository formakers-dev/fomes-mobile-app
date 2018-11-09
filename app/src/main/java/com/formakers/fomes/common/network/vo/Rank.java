package com.formakers.fomes.common.network.vo;

public class Rank {
    String userId;
    Integer rank;
    Long content;

    public Rank(String userId, Integer rank, Long content) {
        this.userId = userId;
        this.rank = rank;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Long getContent() {
        return content;
    }

    public void setContent(Long content) {
        this.content = content;
    }

    public Boolean isValid() {
        return rank >= 0;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "userId='" + userId + '\'' +
                ", rank=" + rank +
                ", content=" + content +
                '}';
    }
}