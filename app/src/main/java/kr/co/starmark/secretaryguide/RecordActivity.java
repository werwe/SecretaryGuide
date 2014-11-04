package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecordActivity extends Activity implements CameraFragment.RecordCallback {

    final Logger logger = LoggerFactory.getLogger(RecordActivity.class);
    public static final String TAG = "RecordActivity";

    final int NUMBERS[] = {
            R.drawable.number_00,
            R.drawable.number_01,
            R.drawable.number_02,
            R.drawable.number_03,
            R.drawable.number_04,
            R.drawable.number_05,
            R.drawable.number_06,
            R.drawable.number_07,
            R.drawable.number_08,
            R.drawable.number_09,
            R.drawable.number_10
    };

    @InjectView(R.id.timer_text)
    ImageView mTimerText;

    GreetingVideo mRecord;
    int mGreetingType = 1;  // 1 , 2 , 3
    int mFaceSide = 1;      // 1 , 2

    CameraFragment mCameraFragment;

    @InjectView(R.id.container)
    FrameLayout mContainer;
    @InjectView(R.id.switch_camera_face)
    ImageButton mSwitchCameraFace;
    @InjectView(R.id.switch_face)
    ImageButton mSwitchFace;

    @InjectView(R.id.menu_container)
    LinearLayout mMenuContainer;

    @InjectView(R.id.side)
    TextView mSide;


    TimerDialogFragment mTimerSelectDialog;
    TimerDialogFragment.TimeAmountSelectCallback mTimerCallback = new TimerDialogFragment.TimeAmountSelectCallback() {
        @Override
        public void onTimeAmount(int seconds) {
            if (seconds == 0)
                mRecordSequenceTimer.startTimer(10);
            else
                startTimer(seconds);
        }

    };

    Timer mCountDownTimer;
    Timer mRecordSequenceTimer;
    File mCurrentVideoPath;
    SoundManager mSoundManager;

    TextToSpeech.OnInitListener mSpeechInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.KOREAN);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }
        }
    };

    private TextToSpeech tts;
    private Sequence mGreetingSequence;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mGreetingType = getIntent().getIntExtra("type", 1);
        mCameraFragment = CameraFragment.newInstance();
        mCameraFragment.setRecordCallback(this);
        getFragmentManager().beginTransaction().replace(R.id.container, mCameraFragment).commit();
        ButterKnife.inject(this);
        mCountDownTimer = new Timer(Timer.ORDER.DECREASE);
        mCountDownTimer.setTimerCallback(mCountDownCallback);
        mRecordSequenceTimer = new Timer(Timer.ORDER.INCREASE);
        mRecordSequenceTimer.setTimerCallback(mRecordingTimeCallback);
        mSoundManager = SoundManager.createInstance(getApplicationContext());
        mSoundManager.load(R.raw.your_turn);
        tts = new TextToSpeech(getApplicationContext(), mSpeechInitListener);
        initSequence();
    }

    public void recordVideo() {
        try {
            if (mCameraFragment.isRecording())
                mCameraFragment.stopRecording();
            else
                mCameraFragment.record();
        } catch (IOException e) {
            Log.d(TAG, "recordVideo", e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "recordVideo", e);
            e.printStackTrace();
        }
    }

    public void selectTimer(View v) {
        if (mTimerSelectDialog == null)
            mTimerSelectDialog = TimerDialogFragment.newInstance();
        mTimerSelectDialog.setTimerCallback(mTimerCallback);
        mTimerSelectDialog.show(getFragmentManager(), "timerdialog");
    }

    public void switchCamera(View v) {
        mCameraFragment.switchCamera();
    }

    public void switchFace(View view) {
        if (mFaceSide == 1)
            mFaceSide = 2;
        else
            mFaceSide = 1;

        if (mFaceSide == 1)
            mSide.setText("정면을 촬영합니다.");
        else if (mFaceSide == 2)
            mSide.setText("측면을 촬영합니다.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountDownTimer.setTimerCallback(null);
        mCountDownTimer.stopTimer();
        ButterKnife.reset(this);
    }

    @Override
    public void onRecordStart(File videoPath) {
        mCurrentVideoPath = videoPath;
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
        mCurrentVideoPath = null;
        mSwitchCameraFace.setEnabled(true);
//        Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
//        intent.putExtra("record", mRecord);
//        startActivity(intent);
    }

    private void startTimer(int seconds) {
        mTimerText.setImageResource(NUMBERS[seconds]);
        mTimerText.setVisibility(View.VISIBLE);
        mCountDownTimer.startTimer(seconds + 1);
    }

    public void onRecordVideo(View v) {
        recordVideo();
    }

    @Override
    public void onBackPressed() {
        try {
            if (mCameraFragment.isRecording()) {
                mCameraFragment.setRecordCallback(null);
                mCameraFragment.stopRecording();
            }
            if (mCurrentVideoPath.exists())
                mCurrentVideoPath.delete();

        } catch (IOException e) {
            Log.d(TAG, "recordVideo", e);
        } catch (Exception e) {
            Log.d(TAG, "recordVideo", e);
        }
        super.onBackPressed();

    }

    Timer.TimerCallback mCountDownCallback = new Timer.TimerCallback() {
        @Override
        public void onTime(final int seconds) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTimerText.setImageResource(NUMBERS[seconds]);
                }
            });
        }

        @Override
        public void onTimeEnd() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTimerText.setVisibility(View.INVISIBLE);
                    mRecordSequenceTimer.startTimer(10);
                }
            });
        }
    };

    Timer.TimerCallback mRecordingTimeCallback = new Timer.TimerCallback() {
        @Override
        public void onTime(final int seconds) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Action action = mGreetingSequence.head();
                    if(action == null)
                        return;
                    if (seconds == action.getTiming()) {
                        action.action();
                        mGreetingSequence.next();
                    }
                }
            });
        }

        @Override
        public void onTimeEnd() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCameraFragment.stopRecording();
                        showAfterRecordMenu();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void showAfterRecordMenu() {
        mMenuContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_replay)
    public void replay() {
        Intent intent = new Intent(getApplicationContext(), ReplayActivity.class);
        intent.putExtra("record", mRecord);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_compare)
    public void compare() {
        Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
        intent.putExtra("record", mRecord);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_retake)
    public void retake() {
        mMenuContainer.setVisibility(View.GONE);
    }


    private void initSequence() {
        mGreetingSequence = new Sequence();
        mGreetingSequence.add(new TextReadAction(0, "인사를 시작합니다"));
        mGreetingSequence.add(new RecordAction(3));
        mGreetingSequence.add(new BeepAction(3, mSoundManager));
        mGreetingSequence.add(new TextReadAction(10, "녹화를 종료합니다"));
    }

    abstract class Action {
        int mTime = -1;

        private Action() {
        }

        public Action(int time) {
            mTime = time;
        }

        public int getTiming() {
            return mTime;
        }

        abstract public void action();
    }

    class Sequence extends LinkedList<Action> {
        int mHead = 0;

        public void next() {
            int size = size();
            if (mHead < size) mHead++;
        }

        public Action head() {
            int size = size();
            if (mHead >= size)
                return null;
            return get(mHead);
        }
    }

    class BeepAction extends Action {
        SoundManager mManager;

        public BeepAction(int time, SoundManager manager) {
            super(time);
            mManager = manager;
        }

        @Override
        public void action() {
            mManager.play(R.raw.your_turn);
            Log.d(TAG, "beep action");
        }
    }

    class TextReadAction extends Action {
        String mText;

        public TextReadAction(int timing, String text) {
            super(timing);
            mText = text;
        }

        @Override
        public void action() {
            tts.speak(mText, TextToSpeech.QUEUE_FLUSH, null);
            Log.d(TAG, "speak action");
        }
    }

    class RecordAction extends Action {
        public RecordAction(int timing) {
            super(timing);
        }

        @Override
        public void action() {
            Log.d(TAG, "recording action");
            recordVideo();
        }
    }
}