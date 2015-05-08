package com.shoheiaoki.animaru;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mCameraView;
    private Mat mOutputFrame;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mCameraView.setCvCameraViewListener(this);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "SCREEN_LOCK");
        wakeLock.acquire();
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        wakeLock.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mOutputFrame = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mOutputFrame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.Canny(inputFrame.gray(), mOutputFrame, 80, 100);
        Core.bitwise_not(mOutputFrame, mOutputFrame);
        return mOutputFrame;
    }


}

