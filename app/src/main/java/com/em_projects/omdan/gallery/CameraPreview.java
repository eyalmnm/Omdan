package com.em_projects.omdan.gallery;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;
import java.util.List;

// Ref: https://stackoverflow.com/questions/3841122/android-camera-preview-is-sideways
// Ref: https://stackoverflow.com/questions/38279166/how-to-resolve-the-android-camera-setparameters-failed
// Ref: https://stackoverflow.com/questions/6503106/add-zoom-controls-of-camera-in-camera
// Ref: https://stackoverflow.com/questions/3878294/camera-parameters-flash-mode-torch-replacement-for-android-2-1

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean isPreviewRunning = false;
    private boolean turnOnFlash = false;
    private float mDist = 0;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // create the surface and start camera preview
            if (mCamera == null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            FirebaseCrash.logcat(Log.ERROR, TAG, "surfaceCreated");
            FirebaseCrash.report(e);
        }
    }

    // https://github.com/badarsh2/AndroidDocumentScanner/blob/e1e0ebe5b0c86d41f2c003619cf48966dcc7c202/app/src/main/java/com/martin/opencv4android/CameraPreview.java
    // https://stackoverflow.com/questions/18594602/how-to-implement-pinch-zoom-feature-for-camera-preview
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /**
     * Determine the space between the first two fingers
     */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

//    @Override
//    public boolean performClick() {
//        // do what you want
//        return true;
//    }

    // Ref: https://stackoverflow.com/questions/3841122/android-camera-preview-is-sideways
    // Ref: https://stackoverflow.com/questions/38279166/how-to-resolve-the-android-camera-setparameters-failed
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        if (isPreviewRunning) {
//            mCamera.stopPreview();
//        }
//
//        Camera.Parameters parameters = mCamera.getParameters();
//        Display display = ((WindowManager) getContext().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
//
//        if (display.getRotation() == Surface.ROTATION_0) {
//            parameters.setPreviewSize(height, width);
//            mCamera.setDisplayOrientation(90);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_90) {
//            parameters.setPreviewSize(width, height);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_180) {
//            parameters.setPreviewSize(height, width);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_270) {
//            parameters.setPreviewSize(width, height);
//            mCamera.setDisplayOrientation(180);
//        }
//
//        mCamera.setParameters(parameters);
//        previewCamera();
//    }

//    public void previewCamera() {
//        try {
//            mCamera.setPreviewDisplay(mHolder);
//            mCamera.startPreview();
//            isPreviewRunning = true;
//        } catch (Exception e) {
//            Log.d(TAG, "Cannot start preview", e);
//        }
//    }

    public boolean isTurnOnFlash() {
        return turnOnFlash;
    }

    public void turnOnFlashLight(boolean on, Camera camera) {
        FirebaseCrash.log(TAG + " turnOnFlashLight");
        if (turnOnFlash != on) {
            turnOnFlash = on;
            refreshCamera(camera);
        }
    }

    public void refreshCamera(Camera camera) {
        FirebaseCrash.log(TAG + " refreshCamera");
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            if (isPreviewRunning) {
                mCamera.stopPreview();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error stopping camera preview: " + e.getMessage());
            FirebaseCrash.logcat(Log.ERROR, TAG, "refreshCamera");
            FirebaseCrash.report(e);
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        mCamera.setDisplayOrientation(90);
        try {
            Camera.Parameters p = mCamera.getParameters();
//            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//            p.setFlashMode(turnOnFlash ? Camera.Parameters.FLASH_MODE_AUTO : Camera.Parameters.FLASH_MODE_OFF);
//            setFlashlight(turnOnFlash);
            mCamera.setParameters(p);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            isPreviewRunning = true;
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            FirebaseCrash.logcat(Log.ERROR, TAG, "refreshCamera");
            FirebaseCrash.report(e);
        }
    }

    /***
     * Attempts to set camera flash torch/flashlight mode on/off
     * @return boolean whether or not we were able to set it
     */
    public boolean setFlashlight(boolean capture) {
        if (mCamera == null) {
            return false;
        }
        boolean isOn = capture && turnOnFlash;
        Camera.Parameters params = mCamera.getParameters();
        String value;
        if (isOn) { // we are being ask to turn it on
            value = Camera.Parameters.FLASH_MODE_TORCH;
        } else {  // we are being asked to turn it off
            value = Camera.Parameters.FLASH_MODE_OFF;
        }

        try {
            params.setFlashMode(value);
            mCamera.setParameters(params);
//            String nowMode = mCamera.getParameters().getFlashMode();
//            if (turnOnFlash && nowMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
//                return true;
//            }
//            if (!turnOnFlash && nowMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
//                return true;
//            }
            return true;
        } catch (Exception ex) {
            Log.e(TAG, this.getClass().getSimpleName() + " error setting flash mode to: " + value + " " + ex.toString());
        }
        return false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        refreshCamera(mCamera);
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        if (null != camera) {
            mCamera = camera;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // mCamera.release();
    }
}