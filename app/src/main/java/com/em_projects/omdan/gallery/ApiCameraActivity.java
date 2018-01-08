package com.em_projects.omdan.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.em_projects.omdan.R;
import com.em_projects.omdan.utils.StringUtils;
import com.em_projects.omdan.utils.TimeUtils;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 10/31/17.
 */

// Ref: https://examples.javacodegeeks.com/android/core/hardware/camera-hardware/android-camera-example/
// Ref: https://stackoverflow.com/questions/6503106/add-zoom-controls-of-camera-in-camera

public class ApiCameraActivity extends Activity {
    private static final String TAG = "ApiCameraActivity";
    // Zoom Controls
    int currentZoomLevel = 0, maxZoomLevel = 0;
    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private MediaActionSound sound = new MediaActionSound();

    private OnClickListener captureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mPreview.setFlashlight(true);
                mCamera.takePicture(null, null, mPicture);
                //MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);
                mPreview.setFlashlight(false);
            } catch (Throwable tr) {
                Log.e(TAG, "takePicture", tr);
                FirebaseCrash.logcat(Log.ERROR, TAG, "captureListener");
                FirebaseCrash.report(tr);
            }
        }
    };
    private Handler flashHandler;
    private Camera.Parameters params;
    private LayoutInflater controlInflater = null;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private String recordId = null;
    private String subDir = null;
    private ArrayList<String> bitmapHolders = new ArrayList<>();

    private OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //get the number of cameras
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };
    OnClickListener cancelListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            sound.release();
            Intent intent = new Intent();
            intent.putExtra("bitmaps", (bitmapHolders.size() > 0));
            ApiCameraActivity.this.setResult(Activity.RESULT_CANCELED, intent);
            ApiCameraActivity.this.finish();
        }
    };
    private ImageButton captureButton;
    private ImageButton switchCameraButton;
    private ImageButton cancelButton;
    private ImageButton flashToggle;
    OnClickListener flashToggleListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            flashToggle.setImageResource(mPreview.isTurnOnFlash() ?
                    R.drawable.ic_flash_on_white_18dp : R.drawable.ic_flash_off_white_18dp);
            flashHandler.sendEmptyMessage(100);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        initialize();
        Intent dataIntent = getIntent();
        if (null == dataIntent) {
            setResult(RESULT_CANCELED);
            finish();
            throw new NullPointerException("Missing intent");
        } else {
            recordId = dataIntent.getStringExtra("recordId");
            subDir = dataIntent.getStringExtra("subDir");
        }
        if (StringUtils.isNullOrEmpty(recordId) || null == subDir) {
            throw new NullPointerException("Missing data in ApiCameraActivity. Missing recordId and/or subDir");
        }

        flashHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mPreview.turnOnFlashLight(!mPreview.isTurnOnFlash(), mCamera);
            }
        };
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    cameraId = i;
                    cameraFront = true;
                    break;
                }
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "findFrontFacingCamera", e);
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "onResume -> Missing permissions");
            Toast.makeText(myContext, "Turn on Camera permissions manually", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        }
    }

    private void openCamera() {
        FirebaseCrash.log(TAG + " openCamera");
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCameraButton.setVisibility(View.GONE);
            }
            int cameraId = findBackFacingCamera();
            try {
                mCamera = Camera.open(cameraId);
            } catch (RuntimeException e) {
                Log.e(TAG, "onResume", e);
            }
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
//            inflateZoomControls();
        }
    }

    private void inflateZoomControls() {
        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.zoom_ctrl, null);
        LinearLayout.LayoutParams layoutParamsControl =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsControl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        this.addContentView(viewControl, layoutParamsControl);

        ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomControls1);
        params = mCamera.getParameters();
        if (params.isZoomSupported()) {
            final int maxZoomLevel = params.getMaxZoom();
            Log.i("max ZOOM ", "is " + maxZoomLevel);
            zoomControls.setIsZoomInEnabled(true);
            zoomControls.setIsZoomOutEnabled(true);

            zoomControls.setOnZoomInClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (currentZoomLevel < maxZoomLevel) {
                        currentZoomLevel++;
                        //mCamera.startSmoothZoom(currentZoomLevel);
                        params.setZoom(currentZoomLevel);
                        mCamera.setParameters(params);
                    }
                }
            });

            zoomControls.setOnZoomOutClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (currentZoomLevel > 0) {
                        currentZoomLevel--;
                        params.setZoom(currentZoomLevel);
                        mCamera.setParameters(params);
                    }
                }
            });
        } else
            zoomControls.setVisibility(View.GONE);
    }

    public void initialize() {
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        captureButton = (ImageButton) findViewById(R.id.captureButton);
        captureButton.setOnClickListener(captureListener);

        switchCameraButton = (ImageButton) findViewById(R.id.changeCameraButton);
        switchCameraButton.setOnClickListener(switchCameraListener);

        cancelButton = (ImageButton) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(cancelListener);

        flashToggle = (ImageButton) findViewById(R.id.flashToggle);
        flashToggle.setOnClickListener(flashToggleListener);
    }

    public void chooseCamera() {
        //if the camera preview is the front
        FirebaseCrash.log(TAG + " chooseCamera");
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private PictureCallback getPictureCallback() {
        PictureCallback picture = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                String fileName = "IMG_" + TimeUtils.imageFormatedTime(System.currentTimeMillis()) + ".png";
                bitmapHolders.add(fileName);
                new BitmapHolder(data, recordId, subDir, fileName);
                FirebaseCrash.log(TAG + " onPictureTaken");
                //mPreview.setFlashlight(false);
                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onBackPressed() {
        cancelButton.performClick();
    }
}
