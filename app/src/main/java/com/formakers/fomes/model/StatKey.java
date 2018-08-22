package com.formakers.fomes.model;

import java.util.Objects;

public class StatKey {
    private String packageName;
    private String date;

    public StatKey(String packageName, String date) {
        this.packageName = packageName;
        this.date = date;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        return (packageName == null ? 0 : packageName.hashCode()) ^ (date == null ? 0 : date.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StatKey)) {
            return false;
        }
        StatKey p = (StatKey) o;
        return Objects.equals(p.getPackageName(), ((StatKey) o).getPackageName()) && Objects.equals(p.getDate(), ((StatKey) o).getDate());
    }
}