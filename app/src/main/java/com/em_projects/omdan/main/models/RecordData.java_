package com.em_projects.omdan.main.models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by eyalmuchtar on 01/10/2017.
 */

public class RecordData implements Serializable {

    private String recordId;
    private HashMap<String, String> recordData;

    public RecordData(String recordId) {
        this.recordId = recordId;
    }

    public String getRecordId() {
        return recordId;
    }

    public HashMap<String, String> getRecordData() {
        return recordData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordData that = (RecordData) o;

        if (!recordId.equals(that.recordId)) return false;
        return recordData.equals(that.recordData);
    }

    @Override
    public int hashCode() {
        int result = recordId.hashCode();
        result = 31 * result + recordData.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RecordData{" +
                "recordId='" + recordId + '\'' +
                ", recordData=" + recordData +
                '}';
    }
}
