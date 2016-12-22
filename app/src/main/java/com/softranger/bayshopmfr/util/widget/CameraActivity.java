package com.softranger.bayshopmfr.util.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.softranger.bayshopmfr.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends AppCompatActivity implements Camera.PictureCallback {

    private static final String TAG = "CameraActivity";
    public static final String ACTION_CAPTURED = "com.softranger.bayshopmfr.util.widget.IMAGE_CAPTURED";

    @BindView(R.id.cameraHolder) FrameLayout mCameraHolder;
    @BindView(R.id.circleFrameView) CircleOverlayView mOverlayView;
    @BindView(R.id.captureBtn) ImageButton mCaptureBtn;
    @BindView(R.id.cancelBtn) Button mCancelBtn;
    @BindView(R.id.noOverlayBg) LinearLayout mNoOverlayBg;

    private byte[] imageData;

    private boolean resizeImage;
    private int angle = -90;

    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        resizeImage = intent.hasExtra("resizeImage");

        if (!resizeImage) {
            mOverlayView.setVisibility(View.GONE);
            mNoOverlayBg.setVisibility(View.VISIBLE);
        }

        if (checkCameraHardware(this)) {
            mCamera = getCameraInstance();
            configureCamera(mCamera.getParameters());
            CameraPreview cameraPreview = new CameraPreview(this, mCamera);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mCameraHolder.addView(cameraPreview, params);
        }
    }

    private void configureCamera(Camera.Parameters parameters) {
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
    }

    @OnClick(R.id.captureBtn)
    void captureImage() {
        if (mCaptureBtn.isSelected()) {
            if (imageData != null) {
                try {
                    Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    Bitmap rotated = rotateBitmap(image, angle);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    rotated.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    File imageFile = new File(getCacheDir(), resizeImage ? "user_avatar.jpg" : System.currentTimeMillis() + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    byte[] resizedData = bos.toByteArray();
                    outputStream.write(resizedData);
                    outputStream.flush();
                    outputStream.close();
                    Intent resultData = new Intent(ACTION_CAPTURED);
                    resultData.putExtra("imagePath", imageFile.getAbsolutePath());
                    setResult(RESULT_OK, resultData);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            mCamera.takePicture(null, null, this);
        }
    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        Bitmap rotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        int dimension = getSquareCropDimensionForBitmap(rotated);

        if (resizeImage) {
            rotated = ThumbnailUtils.extractThumbnail(rotated, dimension, dimension);
        }

        return rotated;
    }

    //I added this method because people keep asking how
//to calculate the dimensions of the bitmap...see comments below
    public int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

    @OnClick(R.id.cancelBtn)
    void discardPicture() {
        mCamera.startPreview();
        mCaptureBtn.setSelected(false);
        mCancelBtn.setVisibility(View.GONE);
        imageData = null;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        imageData = bytes;
        mCancelBtn.setVisibility(View.VISIBLE);
        mCamera.stopPreview();
        mCaptureBtn.setSelected(true);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(){
        Camera c = null;
        if (resizeImage) {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        c = Camera.open(camIdx);
                        c.setDisplayOrientation(90);
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                    }
                }
            }
        } else {
            try {
                c = Camera.open();
                c.setDisplayOrientation(90);
                angle = 90;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
}
