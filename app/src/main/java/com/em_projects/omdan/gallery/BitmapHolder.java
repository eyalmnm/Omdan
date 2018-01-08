package com.em_projects.omdan.gallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.utils.FileUtils;
import com.em_projects.omdan.utils.ImageUtils;
import com.em_projects.omdan.utils.StringUtils;

import java.io.File;

/**
 * Created by eyalmuchtar on 09/10/2017.
 */

// Ref: https://alvinalexander.com/android/asynctask-examples-parameters-callbacks-executing-canceling

public class BitmapHolder {

    private String recordId;
    private String fullPath;
    private String fileName;
    private String directory;
    private String subRecord;
    private boolean fromFile;

    private AsyncTask loaderTask;

    public BitmapHolder(final byte[] data, String recordId, String subRecord, final String fileName) {
        this.recordId = recordId;
        this.subRecord = subRecord;
        String theSub = true == StringUtils.isNullOrEmpty(subRecord) ? "" : File.separator + subRecord;
        directory = Constants.BASE_PATH + File.separator + recordId + theSub;
        this.fileName = fileName;
        this.fromFile = false;

        this.fullPath = directory + File.separator + fileName;

        // Save the new image into the designated directory
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageUtils.byteArray2Bitmap(data);
                FileUtils.writeBitmapToFile(directory, fileName, bitmap);
            }
        }).start();
    }

    public BitmapHolder(String recordId, String subRecord, String fileName) {
        this.recordId = recordId;
        this.subRecord = subRecord;
        String theSub = true == StringUtils.isNullOrEmpty(subRecord) ? "" : File.separator + subRecord;
        directory = Constants.BASE_PATH + File.separator + recordId + theSub;
        this.fileName = fileName;
        this.fromFile = fromFile;

        this.fullPath = directory + File.separator + fileName;
    }

    public BitmapHolder(Bitmap bitmap, String recordId, String subRecord, String fileName, boolean fromFile) {
        this.recordId = recordId;
        this.subRecord = subRecord;
        String theSub = true == StringUtils.isNullOrEmpty(subRecord) ? "" : File.separator + subRecord;
        directory = Constants.BASE_PATH + File.separator + recordId + theSub;
        this.fileName = fileName;
        this.fromFile = fromFile;

        this.fullPath = directory + File.separator + fileName;

        if (false == fromFile) {
            loaderTask = new ImageStoringAsyncTask();
            loaderTask.execute(bitmap);
        }
    }

    public String getRecordId() {
        return recordId;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDirectory() {
        return directory;
    }

    public boolean isFromFile() {
        return fromFile;
    }

    public String getSubRecord() {
        return subRecord;
    }

    @Override
    public String toString() {
        return "BitmapHolder{" +
                "recordId='" + recordId + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", directory='" + directory + '\'' +
                ", subRecord='" + subRecord + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitmapHolder that = (BitmapHolder) o;

        if (!recordId.equals(that.recordId)) return false;
        if (!fullPath.equals(that.fullPath)) return false;
        if (!fileName.equals(that.fileName)) return false;
        if (!directory.equals(that.directory)) return false;
        return subRecord.equals(that.subRecord);
    }

    @Override
    public int hashCode() {
        int result = recordId.hashCode();
        result = 31 * result + fullPath.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + directory.hashCode();
        result = 31 * result + subRecord.hashCode();
        return result;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (null != loaderTask) {
            loaderTask.cancel(true);
        }
    }

    private class ImageStoringAsyncTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            FileUtils.writeBitmapToFile(directory, fileName, bitmaps[0]);
            return null;
        }
    }
}
