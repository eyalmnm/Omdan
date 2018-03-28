package com.em_projects.omdan.main.models;

import android.util.Log;

import com.em_projects.omdan.utils.TimeUtils;
import com.google.firebase.crash.FirebaseCrash;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by eyalmuchtar on 01/10/2017.
 */

public class HistoryDataHolder implements Serializable {
    private static final String TAG = "HistoryDataHolder";

    private String record;
    private Date date;
    private String insuredListName;
    private String customersName;
    private String employeeListName;
    private String suitNumber;
    private String fileStatusName;


    public HistoryDataHolder(String record, String date, String insuredListName
            , String customersName, String employeeListName, String suitNumber
            , String fileStatusName) {
        this.record = record;
        this.insuredListName = insuredListName;
        this.customersName = customersName;
        this.employeeListName = employeeListName;
        this.suitNumber = suitNumber;
        this.fileStatusName = fileStatusName;
        try {
            this.date = TimeUtils.parseToDate(date);
        } catch (ParseException e) {
            Log.e(TAG, "Constructor", e);
            FirebaseCrash.logcat(Log.ERROR, TAG, "Constructor");
            FirebaseCrash.report(e);
            FirebaseCrash.log("Data: " + date);
        }
    }

    public String getRecord() {
        return record;
    }

    public Date getDate() {
        return date;
    }

    public String getTimeStr() {
        return TimeUtils.getTimeStr(date);
    }

    public String getDateStr() {
        return TimeUtils.getDateStr(date);
    }

    public String getInsuredListName() {
        return insuredListName;
    }

    public String getCustomersName() {
        return customersName;
    }

    public String getEmployeeListName() {
        return employeeListName;
    }

    public String getSuitNumber() {
        return suitNumber;
    }

    public String getFileStatusName() {
        return fileStatusName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryDataHolder that = (HistoryDataHolder) o;

        if (record != null ? !record.equals(that.record) : that.record != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (insuredListName != null ? !insuredListName.equals(that.insuredListName) : that.insuredListName != null)
            return false;
        if (customersName != null ? !customersName.equals(that.customersName) : that.customersName != null)
            return false;
        if (employeeListName != null ? !employeeListName.equals(that.employeeListName) : that.employeeListName != null)
            return false;
        if (suitNumber != null ? !suitNumber.equals(that.suitNumber) : that.suitNumber != null)
            return false;
        return fileStatusName != null ? fileStatusName.equals(that.fileStatusName) : that.fileStatusName == null;
    }

    @Override
    public int hashCode() {
        int result = record != null ? record.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (insuredListName != null ? insuredListName.hashCode() : 0);
        result = 31 * result + (customersName != null ? customersName.hashCode() : 0);
        result = 31 * result + (employeeListName != null ? employeeListName.hashCode() : 0);
        result = 31 * result + (suitNumber != null ? suitNumber.hashCode() : 0);
        result = 31 * result + (fileStatusName != null ? fileStatusName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HistoryDataHolder{" +
                "record='" + record + '\'' +
                ", date=" + date +
                ", insuredListName='" + insuredListName + '\'' +
                ", customersName='" + customersName + '\'' +
                ", employeeListName='" + employeeListName + '\'' +
                ", suitNumber='" + suitNumber + '\'' +
                ", fileStatusName='" + fileStatusName + '\'' +
                '}';
    }
}
