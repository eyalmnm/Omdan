package com.em_projects.omdan.gallery;

import android.os.Parcel;
import android.os.Parcelable;

import com.em_projects.omdan.config.Constants;

import java.io.File;

/**
 * Created by eyalmuchtar on 11/26/17.
 */

public class ImageGalleryFile implements Parcelable {
    public static final Creator<ImageGalleryFile> CREATOR = new Creator<ImageGalleryFile>() {
        @Override
        public ImageGalleryFile createFromParcel(Parcel source) {
            return new ImageGalleryFile(source);
        }

        @Override
        public ImageGalleryFile[] newArray(int size) {
            return new ImageGalleryFile[size];
        }
    };
    private static final String TAG = "ImageGalleryFile";
    private String fullPath;
    private String fileName;
    private boolean isDirectory;

    public ImageGalleryFile(String fullPath, String fileName, boolean isDirectory) {
        this.fullPath = fullPath;
        this.fileName = fileName;
        this.isDirectory = isDirectory;
    }

    public ImageGalleryFile(File file) {
        fullPath = file.getAbsolutePath();
        fileName = file.getName();
        isDirectory = file.isDirectory();
    }

    protected ImageGalleryFile(Parcel in) {
        this.fullPath = in.readString();
        this.fileName = in.readString();
        this.isDirectory = in.readByte() != 0;
    }

    public static String getDirectory(String fullPath, String fileName) {
        int startIdx = fullPath.indexOf(Constants.BASE_PATH);
        if (0 <= startIdx) {
            startIdx += Constants.BASE_PATH.length() + 1;
        }
        int lastIdx = fullPath.indexOf(fileName);
        if (0 < lastIdx) {
            return fullPath.substring(startIdx, lastIdx - 1);
        }
        return null;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageGalleryFile that = (ImageGalleryFile) o;

        if (isDirectory != that.isDirectory) return false;
        if (!fullPath.equals(that.fullPath)) return false;
        return fileName.equals(that.fileName);
    }

    @Override
    public int hashCode() {
        int result = fullPath.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + (isDirectory ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImageGalleryFile{" +
                "fullPath='" + fullPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isDirectory=" + isDirectory +
                '}';
    }

    // Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullPath);
        dest.writeString(this.fileName);
        dest.writeByte(this.isDirectory ? (byte) 1 : (byte) 0);
    }
}
