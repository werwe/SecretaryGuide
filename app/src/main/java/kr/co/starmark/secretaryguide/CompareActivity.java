package kr.co.starmark.secretaryguide;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class CompareActivity extends Activity implements MediaPlayer.OnPreparedListener{

    @InjectView(R.id.left)
    Button mLeft;
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.right)
    ImageButton mRight;

    @InjectView(R.id.greeting1)
    TextView mGreeting1;
    @InjectView(R.id.leftVideo)
    android.widget.VideoView mLeftVideo;
    @InjectView(R.id.rightVideo)
    VideoView mRightVideo;

    @InjectView(R.id.btn_play)
    ImageView mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        setActionBar();
        ButterKnife.inject(this);

        GreetingVideo video = getIntent().getParcelableExtra("record");

        video.log();

        String title = getGreetingType(video.type) + " - " + getfaceSide(video.side);
        mTitle.setText("Secretary Guide");
        mGreeting1.setText(title);
        mRight.setEnabled(true);
        mLeftVideo.setVideoURI(getVideoUri(video.type, video.side));
        mLeftVideo.requestFocus();

        mRightVideo.setVideoPath(video.path);
        mRightVideo.requestFocus();

        mLeftVideo.seekTo(100);
        mRightVideo.seekTo(100);

        mRightVideo.setOnCompletionListener(mV2Completion);
        mLeftVideo.setOnCompletionListener(mV1Completion);
        mRightVideo.setOnPreparedListener(this);
        mLeftVideo.setOnPreparedListener(this);
    }

    private String getfaceSide(int side) {
        switch (side) {
            case 1:
                return "앞 모습";
            case 2:
                return "옆 모습";
        }
        return "";
    }

    private String getGreetingType(int type) {
        switch (type) {
            case 1:
                return "가벼운 인사";
            case 2:
                return "보통 인사";
            case 3:
                return "정중한 인사";
        }
        return "";
    }

    private Uri getVideoUri(int type, int side) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/greeting_sample_"+type+"_"+side);
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar);
        ButterKnife.inject(actionBar.getCustomView());
        //mTitle.setText("Secretary Guide");
    }

    @OnClick(R.id.left)
    public void left() {
        finish();
    }

    @OnClick(R.id.right)
    public void right()
    {
        MenuDialogFragment dialog = MenuDialogFragment.newInstance(0);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "dialog");
    }

    boolean mV1IsPlaying = false;
    boolean mV2IsPlaying = false;

    MediaPlayer.OnCompletionListener mV1Completion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mV1IsPlaying = false;
            if(!mV2IsPlaying)
                mPlay.setVisibility(View.VISIBLE);
        }
    };

    MediaPlayer.OnCompletionListener mV2Completion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mV2IsPlaying = false;
            if(!mV1IsPlaying)
                mPlay.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.seekTo(100);
    }

    @OnClick(R.id.btn_play)
    public void onPlay(View v) {
        mLeftVideo.start();
        mRightVideo.start();
        mV1IsPlaying = true;
        mV2IsPlaying = true;
        v.setVisibility(View.INVISIBLE);
    }
}