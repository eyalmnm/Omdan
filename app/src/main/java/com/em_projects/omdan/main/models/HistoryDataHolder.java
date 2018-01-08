package com.em_projects.omdan.main.models;

import com.em_projects.omdan.utils.TimeUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by eyalmuchtar on 01/10/2017.
 */

public class HistoryDataHolder implements Serializable {

    private String record;
    private Date date;
    private String description;

    public HistoryDataHolder(String record, Date date) {
        this.record = record;
        this.date = date;
        this.description = "תאור התיק";
    }

    public HistoryDataHolder(String record, Date date, String description) {
        this.record = record;
        this.date = date;
        this.description = description;
    }

    public String getRecord() {
        return record;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTimeStr() {
        return TimeUtils.getTimeStr(date);
    }

    public String getDateStr() {
        return TimeUtils.getDateStr(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryDataHolder that = (HistoryDataHolder) o;

        if (!record.equals(that.record)) return false;
        if (!date.equals(that.date)) return false;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        int result = record.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "HistoryDataHolder{" +
                "record='" + record + '\'' +
                ", date=" + date +
                ", decription='" + description + '\'' +
                '}';
    }
}
