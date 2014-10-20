package kr.co.starmark.secretaryguide;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecordActivity extends Activity implements CameraFragment.RecordCallback, RadioGroup.OnCheckedChangeListener {

    final Logger logger = LoggerFactory.getLogger(RecordActivity.class);
    public static final String TAG = "RecordActivity";

    GreetingVideo record;
    int greetingType = 1; // 1 , 2 , 3
    int faceSide = 1;

    CameraFragment mCameraFragment;
    @InjectView(R.id.menu)
    RadioGroup mMenu;
    @InjectView(R.id.container)
    FrameLayout mContainer;
    @InjectView(R.id.switch_camera_face)
    ImageButton mSwitchCameraFace;
    @InjectView(R.id.switch_face)
    ImageButton mSwitchFace;

    int mCheckedID = R.id.greeting1;

    @InjectView(R.id.greeting1)
    RadioButton mGreeting1;
    @InjectView(R.id.greeting2)
    RadioButton mGreeting2;
    @InjectView(R.id.greeting3)
    RadioButton mGreeting3;
    @InjectView(R.id.side)
    TextView mSide;

    @InjectView(R.id.left)
    Button mLeft;
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.right)
    ImageButton mRight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setActionBar();
        mCameraFragment = CameraFragment.newInstance();
        mCameraFragment.setRecordCallback(this);
        getFragmentManager().beginTransaction().replace(R.id.container, mCameraFragment).commit();
        ButterKnife.inject(this);

        mMenu.check(mCheckedID);
        mMenu.setOnCheckedChangeListener(this);
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar);
        ButterKnife.inject(actionBar.getCustomView());
//        mTitle.setText("Secretary Guide");
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
        if(faceSide == 1)
            faceSide = 2;
        else
            faceSide = 1;

        if(faceSide == 1)
            mSide.setText("앞 모습");
        else if(faceSide == 2)
            mSide.setText("옆 모습");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.left)
    public void onActionBarLeft() {
        logger.debug("actionbar left button");
    }

    @OnClick(R.id.right)
    public void onActionBarRight() {
        MenuDialogFragment dialog = MenuDialogFragment.newInstance(0);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "dialog");
    }

    @Override
    public void onRecordStart(File videoPath) {
        mSwitchCameraFace.setEnabled(false);
        record = new GreetingVideo();
        record.path = videoPath.getAbsolutePath();
        record.type = greetingType;
        record.side = faceSide;
        record.date = DateFormat.format("yyyy-MM-dd-hh-mm-ss", Calendar.getInstance()).toString();
        record.log();
        record.save();
    }

    @Override
    public void onRecordStop() {
        mSwitchCameraFace.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),CompareActivity.class);
        intent.putExtra("record", record);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        mCheckedID = i;
        if (mCheckedID == R.id.greeting1)
            greetingType = 1;
        else if (mCheckedID == R.id.greeting2)
            greetingType = 2;
        else if (mCheckedID == R.id.greeting3)
            greetingType = 3;
    }
}
