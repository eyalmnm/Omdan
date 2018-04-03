package com.em_projects.omdan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 09/10/2017.
 */

// Ref: https://stackoverflow.com/questions/28758014/how-to-convert-a-file-to-base64

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String fSa = File.separator;
    private static String[] imageNameExtensions = {".png", ".jpg", ".gif", ".bmp"};

    public static ArrayList<String> getDirectories(String path) {
        File rootDir = new File(path);
        if (true == rootDir.exists() && rootDir.isDirectory()) {
            ArrayList<String> subDirs = new ArrayList<>();
            File[] subs = rootDir.listFiles();
            for (File sub : subs) {
                if (true == sub.isDirectory()) {
                    subDirs.add(sub.getName());
                }
            }
            return subDirs;
        } else {
            return null;
        }
    }

    public static void findDirectories(File dir, String name, ArrayList<String> names) {
        if (true == dir.isDirectory()) {
            names.add(name);
            Log.d(TAG, "dir name: " + name);
            File[] files = dir.listFiles();
            if (null == files || 0 == files.length) return;
            for (File file : files) {
                findDirectories(file, name + fSa + file.getName(), names);
            }
        }
    }

    public static void writeBitmapToFile(String dir, String fileName, byte[] imageData) {
        File root = new File(dir);
        boolean resulst = false;
        if (!root.exists()) {
            resulst = root.mkdir();
        }
        Log.d(TAG, "resulst " + resulst);
        File bitmapFile = new File(root, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(bitmapFile);
            fos.write(imageData);
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "writeToLogFile", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeBitmapToFile(String dir, String fileName, Bitmap bitmap) {
        File root = new File(dir);
        boolean resulst = false;
        if (!root.exists()) {
            resulst = root.mkdir();
        }
        Log.d(TAG, "resulst " + resulst);
        File bitmapFile = new File(root, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.e(TAG, "writeToLogFile", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap readBitmapFromFile(String dir, String fileName) {
        Bitmap bitmap = null;
        File root = new File(dir);
        boolean resulst = false;
        if (!root.exists()) {
            return null;
        }
        Log.d(TAG, "resulst " + resulst);
        File bitmapFile = new File(root, fileName);
        if (!bitmapFile.exists()) {
            return null;
        }
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(bitmapFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f, int size) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = size;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return bitmap;
        } catch (FileNotFoundException e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "decodeFile");
            FirebaseCrash.report(e);
        } catch (OutOfMemoryError e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "decodeFile");
            FirebaseCrash.report(e);
        } catch (Throwable e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "decodeFile");
            FirebaseCrash.report(e);
        }
        return null;
    }

    public static boolean containsImages(String dirName) throws Exception {
        File dirFile = new File(dirName);
        if (dirFile.exists()) {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (true == isImageFile(file)) {
                    return true;
                }
            }
            return false;
        } else {
            throw new Exception("Unknown directory " + dirName);
        }
    }

    public static boolean isImageFile(File file) {
        String fileName = file.getName();
        for (String imgExt : imageNameExtensions) {
            if (true == StringUtils.containsIgnureCase(fileName, imgExt)) {
                return true;
            }
        }
        return false;
    }

    public static boolean removeFile(String fileName) {
        File file = new File(fileName);
        file.delete();
        return true;
    }

    /**
     * Converting File to Base64.encode String type using Method
     */
    public static String getStringFile(File f) throws IOException {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        inputStream = new FileInputStream(f.getAbsolutePath());

        byte[] buffer = new byte[10240];//specify the size to allow
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output64.write(buffer, 0, bytesRead);
        }
        output64.close();
        encodedFile = output.toString();
        lastVal = encodedFile;
        return lastVal;
    }

}
