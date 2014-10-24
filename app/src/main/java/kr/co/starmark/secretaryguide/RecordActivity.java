package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecordActivity extends Activity implements CameraFragment.RecordCallback {

    final Logger logger = LoggerFactory.getLogger(RecordActivity.class);
    public static final String TAG = "RecordActivity";

    GreetingVideo mRecord;
    int mGreetingType = 1; // 1 , 2 , 3
    int mFaceSide = 1; // 1 , 2

    CameraFragment mCameraFragment;

    @InjectView(R.id.container)
    FrameLayout mContainer;
    @InjectView(R.id.switch_camera_face)
    ImageButton mSwitchCameraFace;
    @InjectView(R.id.switch_face)
    ImageButton mSwitchFace;


    @InjectView(R.id.side)
    TextView mSide;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mCameraFragment = CameraFragment.newInstance();
        mCameraFragment.setRecordCallback(this);
        getFragmentManager().beginTransaction().replace(R.id.container, mCameraFragment).commit();
        ButterKnife.inject(this);

    }

    public void recordVideo(View v) {
        try {
            if (mCameraFragment.isRecording())
                mCameraFragment.stopRecording();
            else
                mCameraFragment.record();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchCamera(View v) {
        mCameraFragment.switchCamera();
    }

    public void switchFace(View view) {
        if(mFaceSide == 1)
            mFaceSide = 2;
        else
            mFaceSide = 1;

        if(mFaceSide == 1)
            mSide.setText("정면을 촬영합니다.");
        else if(mFaceSide == 2)
            mSide.setText("측면을 촬영합니다.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void onRecordStart(File videoPath) {

        mSwitchCameraFace.setEnabled(false);
        mRecord = new GreetingVideo();
        mRecord.path = videoPath.getAbsolutePath();
        mRecord.type = mGreetingType;
        mRecord.side = mFaceSide;
        mRecord.date = DateFormat.format("yyyy-MM-dd-hh-mm-ss", Calendar.getInstance()).toString();
        mRecord.log();
        mRecord.save();
    }

    @Override
    public void onRecordStop() {
        mSwitchCameraFace.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),CompareActivity.class);
        intent.putExtra("record", mRecord);
        startActivity(intent);
    }
}
