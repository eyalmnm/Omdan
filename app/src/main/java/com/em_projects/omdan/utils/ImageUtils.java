package com.em_projects.omdan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by eyalmuchtar on 18/06/2017.
 */

// Ref: https://stackoverflow.com/questions/28758014/how-to-convert-a-file-to-base64

public class ImageUtils {

//    public static final Bitmap defaultAvatar = ImageUtils.getCircleBitmap(BitmapFactory.decodeResource(context().getResources(), R.drawable.default_avatar));

    private static final String TAG = "ImageUtils";

    public static String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap convertBase64ToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    /**
     * @param bitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    /**
     * @param url
     * @return
     */
    public static Bitmap loadBitmap(String url) {
        if (true == StringUtils.isNullOrEmpty(url)) return null;
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bis) {
                try {
                    bis.close();
                } catch (IOException e) {
                    Log.e(TAG, "loadBitmap", e);
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "loadBitmap", e);
                }
            }
        }
        return bm;
    }

    /**
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap, int width, int height) {
        Bitmap croppedBitmap = scaleCenterCrop(bitmap, width, height);
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        int radius = 0;
        if (width > height) {
            radius = height / 2;
        } else {
            radius = width / 2;
        }

        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(croppedBitmap, rect, rect, paint);

        return output;
    }

    /**
     * @param source
     * @param newHeight
     * @param newWidth
     * @return
     */
    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    /**
     * @param bytes
     * @return
     */
    public static Bitmap byteArray2Bitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
