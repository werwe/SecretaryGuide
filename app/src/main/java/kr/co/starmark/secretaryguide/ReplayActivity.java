package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ReplayActivity extends Activity {

    GreetingVideo mVideoRecord;
    @InjectView(R.id.video)
    android.widget.VideoView mVideo;
    @InjectView(R.id.btn_replay)
    Button mReplay;
    @InjectView(R.id.btn_compare)
    Button mCompare;
    @InjectView(R.id.btn_retake)
    Button mRetake;
    @InjectView(R.id.menu_container)
    LinearLayout mMenuContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        ButterKnife.inject(this);
        mVideoRecord = getIntent().getParcelableExtra("record");
        mMenuContainer.setVisibility(View.GONE);
        replay();
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
        Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
        intent.putExtra("record", mVideoRecord);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_retake)
    public void retake() {
        finish();
    }
}