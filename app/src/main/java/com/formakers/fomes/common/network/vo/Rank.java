package com.formakers.fomes.common.network.vo;

public class Rank {
    public static final int CONVERT_TYPE_SECONDS = 1;
    public static final int CONVERT_TYPE_MINUTES = 2;
    public static final int CONVERT_TYPE_HOURS = 3;
    public static final int CONVERT_TYPE_DAYS = 4;

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

    public float getContent(int convertedType) {
        float converted = this.content;

        switch (convertedType) {
            case CONVERT_TYPE_DAYS:
                converted = converted / 24;
            case CONVERT_TYPE_HOURS:
                converted = converted / 60;
            case CONVERT_TYPE_MINUTES:
                converted = converted / 60;
            case CONVERT_TYPE_SECONDS:
                converted =  converted / 1000;
                break;
            default:
                converted = getContent();
        }

        return (float) Math.ceil(converted * 10) / 10;
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