package com.em_projects.omdan.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.gallery.ImageGalleryActivity;
import com.em_projects.omdan.gallery.ImageGalleryFile;
import com.em_projects.omdan.utils.ErrorsUtils;
import com.em_projects.omdan.utils.FileUtils;
import com.em_projects.omdan.utils.JSONUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 4/11/18.
 */

public class ImagesUpLoaderService extends Service {
    private static final String TAG = "ImagesUpLoaderService";

    // Thread's properties
    private Looper serviceLopper;
    private Handler serviceHandler;
    //    private Object monitor = new Object();
    private int MESSAGE_CODE = 100;

    // Request Data
    private ArrayList<ImageGalleryFile> filesToUpload;
    private int i = 0;

    private Context context;

    // UI Components
    private Point displaySize = new Point();
    private LayoutInflater inflater;
    private WindowManager windowManager;
    private WindowManager.LayoutParams mainWindow;
    private View floatingWidget;
    private Button abortButton;
    private TextView loadingDialogFileNameTextView;
    private ProgressBar loadingDialogSimpleProgressBar;
    private TextView loadingDialogCurrent;
    private TextView loadingDialogOfNumber;

    // Upload File Preoperies
    private String directory;
    private String fileName;
    private String base64Image;

    // Move file properties
    private File src;
    private String baseDirectory;
    private File dest;

    private boolean abortUpload = false;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // Init service handler
        initHandler();

        // Init UI
        initUI();
    }

    private void initHandler() {
        HandlerThread thread = new HandlerThread("OpenWeatherServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLopper = thread.getLooper();
        serviceHandler = new Handler(serviceLopper) {
            public void handleMessage(Message msg) {
                Log.d(TAG, "New message arraived");
                if ((filesToUpload.size() > i) && (false == abortUpload)) {
                    Log.d(TAG, "loading image: " + i);
                    uploadFile();
                    i++;
                } else {
                    Log.d(TAG, "Loading compleated");
                    hidesLoadingDialog();
                    sendBroadcast();
                    stopSelf();
                }
            }
        };
    }

    private void sendBroadcast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(ImageGalleryActivity.loadingImagesReceiverAction);
        intent.putParcelableArrayListExtra("filesToUpload", filesToUpload);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void hidesLoadingDialog() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                floatingWidget.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        filesToUpload = intent.getParcelableArrayListExtra("filesToUpload");
        i = 0;
        loadingDialogSimpleProgressBar.setMax(filesToUpload.size());
        showLoadingDialog(filesToUpload.size(), 0, "");
        serviceHandler.sendMessageDelayed(Message.obtain(serviceHandler, MESSAGE_CODE), 250);
        return Service.START_REDELIVER_INTENT;
    }

    private void showLoadingDialog(final int size, final int current, final String fileName) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadingDialogFileNameTextView.setText(String.valueOf(fileName));
                loadingDialogCurrent.setText(String.valueOf(current));
                loadingDialogOfNumber.setText(String.valueOf(size));
                loadingDialogSimpleProgressBar.setProgress(current);
            }
        });
    }

    private void uploadFile() {
        try {
            directory = ImageGalleryFile.getDirectory(filesToUpload.get(i).getFullPath(), filesToUpload.get(i).getFileName());
            fileName = filesToUpload.get(i).getFileName();
            showLoadingDialog(filesToUpload.size(), (i + 1), fileName);
            Log.d(TAG, "Loading image: " + fileName + " file index: " + i);
            base64Image = FileUtils.getStringFile(new File(filesToUpload.get(i).getFullPath()));
            ServerUtilities.getInstance().uploadImage(base64Image, directory, fileName, new CommListener() {
                @Override
                public void newDataArrived(String response) {
                    Log.d(TAG, "newDataArrived response: " + response);
                    if (response.contains(Constants.error)) {
                        int errorNo = ErrorsUtils.getError(response);
                        Log.d(TAG, "newDataArrived with error: " + errorNo);
                    } else {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);
                        } catch (JSONException e) {
                            Log.e(TAG, "newDataArrived: failed to parse: " + response, e);
                            FirebaseCrash.logcat(Log.ERROR, TAG, "uploadFile -> newDataArrived");
                            FirebaseCrash.report(e);
                        }
                        if (null != json) {
                            String directory = JSONUtils.getStringValue(json, Constants.directory);
                            String fileName = JSONUtils.getStringValue(json, Constants.fileName);
                            boolean res = moveFileToGeneralDirectory(fileName, directory);
                            if (true == res) {
                                Log.d(TAG, "newDataArrived: moving file success");
                            } else {
                                Log.d(TAG, "newDataArrived: moving file failed");
                            }
                        }
                    }
                    serviceHandler.sendEmptyMessage(MESSAGE_CODE);
                }

                @Override
                public void exceptionThrown(Throwable throwable) {
                    Log.d(TAG, "exceptionThrown: " + throwable);
                    serviceHandler.sendEmptyMessage(MESSAGE_CODE);
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, "uploadFile", ex);
            serviceHandler.sendEmptyMessage(MESSAGE_CODE);
        }
    }

    private boolean moveFileToGeneralDirectory(String fileName, String directory) {
        String imageFileName = Constants.BASE_PATH + File.separator + directory + File.separator + fileName;
        src = new File(imageFileName);
        baseDirectory = Constants.GENERAL_PATH + File.separator + directory;
        dest = new File(baseDirectory + File.separator + fileName);
        return FileUtils.moveFile(src, dest, baseDirectory);
    }

    private void initUI() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "wizecall_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "WizeCall Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
        // Init UI
        windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        if (Build.VERSION.SDK_INT == 26) {
            mainWindow = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT);
        } else {
            mainWindow = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT);
        }
        mainWindow.gravity = Gravity.CENTER;

        // Loading UI Components
        inflater = LayoutInflater.from(this);
        floatingWidget = inflater.inflate(R.layout.dialog_loading_layout, null);
        abortButton = floatingWidget.findViewById(R.id.abortButton);
        loadingDialogFileNameTextView = floatingWidget.findViewById(R.id.loadingDialogFileNameTextView);
        loadingDialogSimpleProgressBar = floatingWidget.findViewById(R.id.loadingDialogSimpleProgressBar);
        loadingDialogCurrent = floatingWidget.findViewById(R.id.loadingDialogCurrent);
        loadingDialogOfNumber = floatingWidget.findViewById(R.id.loadingDialogOfNumber);

        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abortUpload = true;
                hidesLoadingDialog();
            }
        });

        floatingWidget.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.w(TAG, "ACTION_DOWN");
                        initialX = mainWindow.x;
                        initialY = mainWindow.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        if (Xdiff > 10 || Ydiff > 10) {
                            Log.w(TAG, "XDiff: " + Xdiff + "  YDiff: " + Ydiff);
                            if (Math.abs(Xdiff) > Math.abs(Ydiff * 5)) {  // Swipe to Hide
                                floatingWidget.setVisibility(View.GONE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mainWindow.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mainWindow.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingWidget, mainWindow);
                        return true;
                }
                return false;
            }
        });
        windowManager.addView(floatingWidget, mainWindow);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}