package com.formakers.fomes.common.model;

import java.util.Date;

public class FomesPoint {
    Date date;
    Long point;
    String type;
    String status;
    String description;

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

    public String getType() {
        return type;
    }

    public FomesPoint setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public FomesPoint setStatus(String status) {
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

    @Override
    public String toString() {
        return "Point{" +
                "date=" + date +
                ", point=" + point +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
