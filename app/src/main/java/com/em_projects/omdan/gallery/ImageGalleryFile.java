package com.em_projects.omdan.gallery;

import com.em_projects.omdan.config.Constants;

/**
 * Created by eyalmuchtar on 11/26/17.
 */

public class ImageGalleryFile {
    private static final String TAG = "ImageGalleryFile";

    private String fullPath;
    private String fileName;
    private boolean isDirectory;

    public ImageGalleryFile(String fullPath, String fileName, boolean isDirectory) {
        this.fullPath = fullPath;
        this.fileName = fileName;
        this.isDirectory = isDirectory;
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
}
