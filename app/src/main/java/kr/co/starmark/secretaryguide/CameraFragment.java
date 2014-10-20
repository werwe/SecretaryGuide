package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;


public class CameraFragment extends Fragment implements TextureView.SurfaceTextureListener ,Camera.AutoFocusCallback{
    
    public static interface RecordCallback{
        public void onRecordStart(File videoPath);
        public void onRecordStop();
    }

    public static final String TAG = "CameraFragment";
    Logger logger = LoggerFactory.getLogger(CameraFragment.class);

    private MediaRecorder mRecorder;
    private CamcorderProfile mProfile;

    private Camera mCamera;

    TextureView mTextureView;

    int mCameraID = -1;

    int mFrontCameraID = -1;
    int mBackCameraID = -1;

    boolean mIsRecording = false;

    int mPreviewWidth;
    int mPreviewHeight;

    PreviewFrameLayout mPreviewContainer;
    public RecordCallback mRecordCallback;

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CameraFragment() {
    }

    public void setRecordCallback(RecordCallback callback) {
        this.mRecordCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findCameraID();
    }

    private void findCameraID() {
        int numberOfCamera = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCamera; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                mFrontCameraID = i;

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                mBackCameraID = i;
        }
        if(mBackCameraID != -1)
            mCameraID = mBackCameraID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPreviewContainer = (PreviewFrameLayout)inflater.inflate(R.layout.camera_fragment, container, false);
        mTextureView = ButterKnife.findById(mPreviewContainer, R.id.textureview);
        mTextureView.setSurfaceTextureListener(this);
        return mPreviewContainer;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() { super.onDestroy(); }

    /// TextureView.SurfaceTextureListener Callback
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
//        logger.debug("onSurfaceTextureAvailable:{},{}", width, height);
//        Log.d(TAG, String.format("onSurfaceTextureAvailable:%s,%s", width, height));
        openCamera();
        getVideoPreviewSize();
        mPreviewContainer.setPreviewSize(mPreviewWidth,mPreviewHeight);
        startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
//        Log.d(TAG, String.format("onSurfaceTextureSizeChanged:%s,%s", width, height));
//        logger.debug("onSurfaceTextureSizeChanged:{},{}", width, height);
//        getVideoPreviewSize();
        mPreviewContainer.setPreviewSize(mPreviewWidth,mPreviewHeight);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mCamera.stopPreview();
        mCamera.release();
        if(mRecorder != null)
            mRecorder.release();

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
    /// TextureView.SurfaceTextureListener Callback


    /**
     *
     * @param b
     * @param camera
     */
    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        if(b) {
            Toast.makeText(getActivity(), "auto focused", Toast.LENGTH_SHORT).show();
        }
    }

    public void getVideoPreviewSize() {
        float ratio = (float) mTextureView.getWidth() / (float) mTextureView.getHeight();
        Camera.Parameters param = mCamera.getParameters();
        List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
//        for (Camera.Size s : sizes) {
//            Log.d(TAG, s.width + "/" + s.height);
//        }
        Camera.Size preferedSize = Util.getOptimalPreviewSize(getActivity(), mCamera.getParameters().getSupportedPreviewSizes(), ratio);
        mPreviewHeight = preferedSize.height;
        mPreviewWidth = preferedSize.width;
    }

    public void openCamera() {
        try {
            mCamera = Camera.open(mCameraID);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
            Camera.Parameters params = mCamera.getParameters();
            getVideoPreviewSize();

            if(mCameraID == mBackCameraID) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            params.setRotation(90);
            params.setPreviewSize(mPreviewWidth, mPreviewHeight);
            mCamera.setParameters(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPreview() {
        mCamera.startPreview();
    }

    public void stopPreview() {
        mCamera.stopPreview();
    }

    public void switchCamera() {
        if (mCameraID == mBackCameraID)
            mCameraID = mFrontCameraID;
        else if (mCameraID == mFrontCameraID)
            mCameraID = mBackCameraID;

        mCamera.stopPreview();
        mCamera.release();

        openCamera();

        Camera.Size size = mCamera.getParameters().getPreviewSize();
        Log.d(TAG, "size(" + size.width + "/" + size.height);
        startPreview();

    }

    public boolean isRecording()
    {
        return mIsRecording;
    }

    public void record ()throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            throw new UnsupportedOperationException(
                    "Video recording supported only on API Level 11+");
        }

        try {
            mRecorder = new MediaRecorder();
            mCamera.unlock();
            mRecorder.setCamera(mCamera);

            mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);


            mProfile = CamcorderProfile.get(mCameraID, CamcorderProfile.QUALITY_720P);
            mRecorder.setProfile(mProfile);

            // This is all very sloppy
            File newFile = null;
            if (mProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
                try {
                    newFile = File.createTempFile("video-"+System.currentTimeMillis(), ".3gp", getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES));
                    mRecorder.setOutputFile(newFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.v(TAG, "Couldn't create file");
                    e.printStackTrace();
                }
            } else if (mProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
                try {
                    newFile = File.createTempFile("video-"+System.currentTimeMillis(), ".mp4", getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES));
                    mRecorder.setOutputFile(newFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.v(TAG, "Couldn't create file");
                    e.printStackTrace();
                }
            } else {
                try {
                    newFile = File.createTempFile("video-"+System.currentTimeMillis(), ".mp4", getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES));
                    mRecorder.setOutputFile(newFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.v(TAG, "Couldn't create file");
                    e.printStackTrace();
                }
            }

            mRecorder.setOrientationHint(90);
            mRecorder.prepare();
            mRecorder.start();
            mIsRecording = true;

            if (mRecordCallback != null) {
                mRecordCallback.onRecordStart(newFile);
            }
        } catch (IOException e) {
            mRecorder.release();
            mRecorder = null;
            throw e;
        }
    }

    public void stopRecording() throws IOException {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            throw new UnsupportedOperationException(
                    "Video recording supported only on API Level 11+");
        }
        mIsRecording = false;
        MediaRecorder tempRecorder = mRecorder;
        mRecorder = null;
        tempRecorder.stop();
        tempRecorder.release();
        mCamera.reconnect();
        if (mRecordCallback != null) {
            mRecordCallback.onRecordStop();
        }
    }
}