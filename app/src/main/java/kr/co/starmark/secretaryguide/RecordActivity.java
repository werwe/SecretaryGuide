package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;


public class RecordActivity extends Activity implements TextureView.SurfaceTextureListener , MediaRecorder.OnInfoListener , MediaRecorder.OnErrorListener{

    public static final String LOGTAG = "VIDEOCAPTURE";

    private MediaRecorder recorder;
    private CamcorderProfile camcorderProfile;

    private Camera mCamera;
    private TextureView mTextureView;
    private Button mTakeVideo;
    boolean mRecording = false;
    boolean mUseCamera = true;
    boolean mPreviewRunning = false;


    int mCameraCount;
    int mSelectedCamera = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        if(!checkCameraHardware(getApplicationContext()))
        {
            //이용가능한 카메라가 없다.
            finish();
        }

        mTextureView = (TextureView) findViewById(R.id.textureview);
        mTextureView.setSurfaceTextureListener(this);
        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
        mTakeVideo = (Button) findViewById(R.id.takevideo);
        mTakeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordVideo();
            }
        });

        findViewById(R.id.switchbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        mCameraCount = Camera.getNumberOfCameras();

    }


    //Texture View initialize sequence
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            prepareRecorder(mSelectedCamera);
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(LOGTAG, "width:" + width + "/" + height);
        // Ignored, CameraDemoFragment does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new CameraDemoFragment preview frame
    }
    //texture view initialize sequence


    private void prepareRecorder(int cameraId) {
        if(recorder == null)
            recorder = new MediaRecorder();

        if (mUseCamera) {
            mCamera.unlock();
            recorder.setCamera(mCamera);
        }
        recorder.setOnInfoListener(this);
        recorder.setOnErrorListener(this);

        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        camcorderProfile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);

        recorder.setProfile(camcorderProfile);

        // This is all very sloppy
        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
            try {
                File newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStorageDirectory());
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            try {
                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else {
            try {
                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG, "Couldn't create file");
                e.printStackTrace();
                finish();
            }
        }
        //recorder.setMaxDuration(50000); // 50 seconds
        //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void recordVideo()
    {
        if (mRecording) {
            recorder.stop();
            if (mUseCamera) {
                try {
                    mCamera.reconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // recorder.release();
            mRecording = false;
            Log.v(LOGTAG, "Recording Stopped");
            // Let's prepareRecorder so we can record again
            prepareRecorder(mSelectedCamera);
        } else {
            mRecording = true;
            recorder.start();
            Log.v(LOGTAG, "Recording Started");
        }
    }

    private void switchCamera()
    {
        if(mSelectedCamera == 0)
            mSelectedCamera = 1;
        else
            mSelectedCamera = 0;

        Log.d(LOGTAG, "selected camera:" + mSelectedCamera);
        mCamera.lock();
        mCamera.stopPreview();
        mCamera.release();
        mCamera = Camera.open(mSelectedCamera);
        mCamera.setDisplayOrientation(90);

        try {
            mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();

        recorder.reset();
        prepareRecorder(mSelectedCamera);
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

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        Log.d(LOGTAG, "OnInfo:" + i + "/" + i2);
    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i2) {
        Log.d(LOGTAG, "OnError:" + i + "/" + i2);
    }
}
