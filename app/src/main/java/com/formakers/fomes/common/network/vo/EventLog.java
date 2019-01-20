package com.formakers.fomes.common.network.vo;

public class EventLog {
    String where;
    String action;
    String ref;

    public EventLog() {
    }

    public String getWhere() {
        return where;
    }

    public EventLog setWhere(String where) {
        this.where = where;
        return this;
    }

    public String getAction() {
        return action;
    }

    public EventLog setAction(String action) {
        this.action = action;
        return this;
    }

    public String getRef() {
        return ref;
    }

    public EventLog setRef(String ref) {
        this.ref = ref;
        return this;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "where='" + where + '\'' +
                ", action='" + action + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }
}