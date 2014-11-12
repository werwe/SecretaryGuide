package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.VideoView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ReplayActivity extends Activity {

    GreetingVideo mVideoRecord;

    android.widget.VideoView mVideo;
    @InjectView(R.id.btn_replay)
    ImageButton mReplay;
    @InjectView(R.id.btn_compare)
    ImageButton mCompare;
    @InjectView(R.id.btn_retake)
    ImageButton mRetake;
    @InjectView(R.id.menu_container)
    RelativeLayout mMenuContainer;
    @InjectView(R.id.video_frame)
    FrameLayout mVideoFrame;
    private int mGreetingType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        ButterKnife.inject(this);
        addVideoView();
        mGreetingType = getIntent().getIntExtra("type", 1);
        mVideoRecord = getIntent().getParcelableExtra("record");
        mMenuContainer.setVisibility(View.GONE);
        replay();
    }

    private void addVideoView() {
        mVideo = new VideoView(this);
        mVideoFrame.addView(mVideo);
    }

    private void removeVideoView(){
        mVideoFrame.removeView(mVideo);
    }

    @OnClick(R.id.btn_replay)
    public void replay() {
        Log.d("ReplayActivity", mVideoRecord.path);
        mVideo.setVideoPath(mVideoRecord.path);
        mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMenuContainer.setVisibility(View.VISIBLE);
            }
        });
        mVideo.start();
        mMenuContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_compare)
    public void compare() {

        removeVideoView();
        Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
        intent.putExtra("record", mVideoRecord);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_retake)
    public void retake() {
        GreetingVideo.delete(GreetingVideo.class, mVideoRecord.storedId);
        File f = new File(mVideoRecord.path);
        if (f.exists()) {
            f.delete();
        }
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra("type", mGreetingType);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
