package com.em_projects.omdan.config;

import android.os.Environment;

/**
 * Created by eyalmuchtar on 09/10/2017.
 */

public class Constants {
    // File System root filePath
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().toString() + "/omdan";

    // File System general directory filePath
    public static final String GENERAL_PATH = Environment.getExternalStorageDirectory().toString() + "/omdan_gen";

    // Server Constants
    public static final String error = "error";

    public static final String login = "api/Login";
    public static final String userName = "name";
    public static final String password = "password";
    public static final String uuid = "uuid";

    public static final String findFile = "api/FindFile";
    public static final String fileNumber = "fileNumber";
    public static final String creationDateStart = "creationDateStart";
    public static final String creationDateEnd = "creationDateEnd";
    public static final String insuredName = "insuredName";
    public static final String customer = "customer";
    public static final String employee = "employee";
    public static final String suitNumber = "suitNumber";
    public static final String fileStatus = "fileStatus";

    public static final String uploadImage = "api/ImageUpload";
    public static final String filePath = "filePath";
    //public static final String subDirectory = "sub_directory";
    public static final String image = "fileContent";
    public static final String fileName = "fileName";
    public static final String fileFullPath = "file_full_path";

    public static final String uploadImages = "api/ImagesUpload";
    public static final String images = "filesContent";
    public static final String fileNames = "fileNames";

    public static final String getFiles = "api/GetFiles";
    public static final String directory = "directory";
    public static final String filesLists = "files_list";

}
