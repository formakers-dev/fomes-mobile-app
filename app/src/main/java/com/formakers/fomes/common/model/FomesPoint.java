package com.formakers.fomes.common.model;

import java.util.Date;

public class FomesPoint {

    public static final int TYPE_SAVE = 1;
    public static final int TYPE_EXCHANGE = 2;

    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_REQUEST = 10;

    Date date;
    Long point;
    Integer type;
    Integer status;
    String description;

    // for exchange type
    String phoneNumber;

    public FomesPoint() {
    }

    public Date getDate() {
        return date;
    }

    public FomesPoint setDate(Date date) {
        this.date = date;
        return this;
    }

    public Long getPoint() {
        return point;
    }

    public FomesPoint setPoint(Long point) {
        this.point = point;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public FomesPoint setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public FomesPoint setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public FomesPoint setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public FomesPoint setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @Override
    public String toString() {
        return "FomesPoint{" +
                "date=" + date +
                ", point=" + point +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
