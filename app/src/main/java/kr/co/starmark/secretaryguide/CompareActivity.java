package kr.co.starmark.secretaryguide;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class CompareActivity extends Activity implements MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {

    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int STOP = 3;
    private static final String TAG = CompareActivity.class.getName();

    @InjectView(R.id.left)
    ImageButton mLeft;
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.right)
    ImageButton mRight;

    @InjectView(R.id.face_explain)
    TextView mGreeting1;

    @InjectView(R.id.leftVideo)
    VideoView mLeftVideo;
    @InjectView(R.id.rightVideo)
    VideoView mRightVideo;

    @InjectView(R.id.btn_play)
    ImageView mPlay;
    @InjectView(R.id.video_frame)
    LinearLayout mVideoFrame;
    @InjectView(R.id.seek_bar)
    SeekBar mSeekBar;

    @InjectView(R.id.play)
    ImageButton mToolbarPlay;
    @InjectView(R.id.controller)
    LinearLayout mController;

    @InjectView(R.id.current_time)
    TextView mCurrentTime;
    @InjectView(R.id.duration)
    TextView mDuration;

    private int mPlayStatement = STOP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        setActionBar();
        ButterKnife.inject(this);

        mLeft.setVisibility(View.VISIBLE);
        mLeft.setImageResource(R.drawable.icon_back);
        mRight.setVisibility(View.GONE);
        mTitle.setText("모델과 비교");

        GreetingVideo video = getIntent().getParcelableExtra("record");

        video.log();

        String title = getGreetingType(video.type) + " - " + getfaceSide(video.side);
        mTitle.setText("Secretary Guide");
        mRight.setVisibility(View.GONE);
        mGreeting1.setText(title);
        mRight.setEnabled(true);

        mLeftVideo.setVideoURI(getVideoUri(video.type, video.side));
        mLeftVideo.requestFocus();

        mRightVideo.setVideoPath(video.path);
        mRightVideo.requestFocus();

        mLeftVideo.seekTo(0);
        mRightVideo.seekTo(0);

        mRightVideo.setOnCompletionListener(mV2Completion);
        mLeftVideo.setOnCompletionListener(mV1Completion);
        mRightVideo.setOnPreparedListener(this);
        mLeftVideo.setOnPreparedListener(this);

        mSeekBar.setOnSeekBarChangeListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSeekerThread();
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
        return Uri.parse("android.resource://" + getPackageName() + "/raw/greeting_sample_" + type + "_" + side);
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar);
        ButterKnife.inject(actionBar.getCustomView());
    }

    @OnClick(R.id.left)
    public void left() {
        finish();
    }

    boolean mV1IsPlaying = false;
    boolean mV2IsPlaying = false;

    MediaPlayer.OnCompletionListener mV1Completion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mV1IsPlaying = false;
            if (!mV2IsPlaying) {
                mPlay.setVisibility(View.VISIBLE);
                stopSeekerThread();
            }
        }
    };

    MediaPlayer.OnCompletionListener mV2Completion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mV2IsPlaying = false;
            if (!mV1IsPlaying) {
                mPlay.setVisibility(View.VISIBLE);
                stopSeekerThread();
            }
        }
    };

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.seekTo(0);
        int duration = mLeftVideo.getDuration();
        mSeekBar.setMax(duration);
        mDuration.setText(DateFormat.format("m:ss", duration));
    }

    public void play() {
        mPlayStatement = PLAY;
        mLeftVideo.start();
        mRightVideo.start();
        mV1IsPlaying = true;
        mV2IsPlaying = true;
        mPlay.setVisibility(View.INVISIBLE);
        mToolbarPlay.setImageResource(R.drawable.btn_pause);
        stopSeekerThread();
        mThread = new SeekerThread();
        mThread.start();

    }

    private void stopSeekerThread() {
        if (mThread != null && mThread.isAlive()) {
            mThread.stopThread();
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    SeekerThread mThread;

    @OnClick(R.id.btn_play)
    public void onPlay(View v) {
        play();
    }

    @OnClick(R.id.play)
    public void onPlayPause(View v) {
        if (mPlayStatement == STOP || mPlayStatement == PAUSE) {
            play();
        } else if (mPlayStatement == PLAY) {
            mPlayStatement = PAUSE;
            mPlay.setVisibility(View.VISIBLE);
            mToolbarPlay.setImageResource(R.drawable.btn_play);
            mLeftVideo.pause();
            mRightVideo.pause();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            mLeftVideo.seekTo(i);
            mRightVideo.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    class SeekerThread extends Thread {
        boolean run = true;

        public void stopThread() {
            run = false;
        }

        @Override
        public void run() {
            super.run();
            while (run) {
                try {
                    Thread.sleep(50);
                    updateSeekerPosition();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateSeekerPosition() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(mLeftVideo.getCurrentPosition());
                mCurrentTime.setText(DateFormat.format("m:ss", mLeftVideo.getCurrentPosition()));
            }
        });
    }
}