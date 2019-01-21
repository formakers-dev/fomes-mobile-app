package com.formakers.fomes.common.network.vo;

public class EventLog {
    String code;
    String ref;

    public EventLog() {
    }

    public String getCode() {
        return code;
    }

    public EventLog setCode(String code) {
        this.code = code;
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
                "code='" + code + '\'' +
                ", ref='" + ref + '\'' +
                '}';
    }
}