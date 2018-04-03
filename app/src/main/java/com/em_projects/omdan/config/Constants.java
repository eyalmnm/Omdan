package com.em_projects.omdan.config;

import android.os.Environment;

/**
 * Created by eyalmuchtar on 09/10/2017.
 */

public class Constants {
    // File System root directory
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().toString() + "/omdan";

    // Server Constants
    public static final String login = "api/Login";
    public static final String userName = "name";
    public static final String password = "password";
    public static final String uuid = "uuid";

    public static final String findFile = "api/FindFile";
    public static final String fileNumber = "fileNumber";

    public static final String uploadImage = "api/ImageUpload";
    public static final String directory = "directory";
    //public static final String subDirectory = "sub_directory";
    public static final String image = "image";
    public static final String fileName = "file_name";
}
