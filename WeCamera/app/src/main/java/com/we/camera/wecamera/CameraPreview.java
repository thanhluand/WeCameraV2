package com.we.camera.wecamera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;

/**
 * Created by Le Quang Tuan on 15-May-16.
 */

/*
 * Dùng để Preview Camera trước khi quay video hoặc chụp hình
 *
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    final String TAG = "YOUR-TAG-NAME";
    private SurfaceHolder mHolder;
    private Camera mCamera;


    public CameraPreview(Context context, Camera camera) {
        super(context);

        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCameraDisplayOrientation(Activity activity,
                                    int cameraId, Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:   degrees = 0;   break;
            case Surface.ROTATION_90:  degrees = 90;  break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            int m = info.orientation;
            result = (info.orientation - degrees + 360) % 360;
        }

        CameraActivity.orientation = result;
        camera.setDisplayOrientation(result);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            setCameraDisplayOrientation(((Activity) getContext()), 0, mCamera);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {

        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
