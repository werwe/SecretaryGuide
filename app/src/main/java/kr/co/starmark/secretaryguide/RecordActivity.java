package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
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
    RelativeLayout mMenuContainer;

//    @InjectView(R.id.side)
//    TextView mSide;


    @InjectView(R.id.guide_image)
    ImageView mGuide;

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


    @InjectView(R.id.cancel_record)
    ImageButton mCancelRecord;

    @InjectView(R.id.no_delay)
    ImageButton mNoDelay;
    @InjectView(R.id.delay_5)
    ImageButton mDelay5;
    @InjectView(R.id.delay_10)
    ImageButton mDelay10;
    @InjectView(R.id.timerContainer)
    LinearLayout mTimerContainer;
    @InjectView(R.id.text_timer_choose)
    ImageView mTimerChooseText;

//    @InjectView(R.id.btn_replay)
//    Button mBtnReplay;
//    @InjectView(R.id.btn_compare)
//    Button mBtnCompare;
//    @InjectView(R.id.btn_retake)
//    Button mBtnRetake;

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
        mSoundManager.load(R.raw.audio_count);
        mSoundManager.load(R.raw.audio_greeing_start);
        mSoundManager.load(R.raw.audio_greeting_end);

        mMenuContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {}
        });
//        tts = new TextToSpeech(getApplicationContext(), mSpeechInitListener);
        initSequence();
    }

    @OnClick(R.id.cancel_record)
    public void cancelRecord()
    {
        terminateRecord();
        finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_down_translate);
    }

    @OnClick(R.id.menu_close)
    public void afterMemuClose()
    {
        finish();
    }


    //Timer click callback
    @OnClick(R.id.no_delay)
    public void recordStartImmediatly()
    {
        resetSequeceHead();
        mRecordSequenceTimer.startTimer(8);
        hideTimerLayout();
    }

    @OnClick(R.id.delay_5)
    public void recordAfter5Seconds()
    {
        resetSequeceHead();
        startTimer(5);
        hideTimerLayout();
    }

    @OnClick(R.id.delay_10)
    public void recordAfter10Seconds()
    {
        resetSequeceHead();
        startTimer(10);
        hideTimerLayout();
    }

    private void hideTimerLayout() {
        mTimerContainer.setVisibility(View.GONE);
        mTimerChooseText.setVisibility(View.GONE);
    }

    private void showTimerLayout() {
        mTimerChooseText.setImageResource(R.drawable.text_choose_time);
        mTimerContainer.setVisibility(View.VISIBLE);
        mTimerChooseText.setVisibility(View.VISIBLE);
    }
    //timer click


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


    public void switchCamera(View v) {
        mCameraFragment.switchCamera();
    }

    public void switchFace(View view) {
        if (mFaceSide == 1)
            mFaceSide = 2;
        else
            mFaceSide = 1;

        if (mFaceSide == 1)
            mGuide.setImageResource(R.drawable.record_guide_front);
//            mSide.setText("정면을 촬영합니다.");
        else if (mFaceSide == 2)
            mGuide.setImageResource(R.drawable.record_guide_side);
//            mSide.setText("측면을 촬영합니다.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onRecordStart(File videoPath) {
        mCurrentVideoPath = videoPath;
        mSwitchCameraFace.setEnabled(false);
        mRecord = new GreetingVideo();
        mRecord.path = videoPath.getAbsolutePath();
        mRecord.type = mGreetingType;
        mRecord.side = mFaceSide;
        mRecord.date = DateFormat.format("yyyy.MM.dd hh:mm:ss", Calendar.getInstance()).toString();
        mRecord.log();
        mRecord.storedId = mRecord.save();
    }

    @Override
    public void onRecordStop() {
        mCurrentVideoPath = null;
        mSwitchCameraFace.setEnabled(true);
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
        terminateRecord();
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_down_translate);
    }

    public void terminateRecord() {

        mCountDownTimer.setTimerCallback(null);
        mCountDownTimer.stopTimer();
        mRecordSequenceTimer.setTimerCallback(null);
        mRecordSequenceTimer.stopTimer();
        mSoundManager.stopAll();
        if(mRecord != null)
            mRecord.delete();
        try {
            if (mCameraFragment.isRecording()) {
                mCameraFragment.setRecordCallback(null);
                mCameraFragment.stopRecording();
            }
            if (mCurrentVideoPath != null && mCurrentVideoPath.exists())
                mCurrentVideoPath.delete();

        } catch (IOException e) {
            Log.d(TAG, "terminateRecord", e);
        } catch (Exception e) {
            Log.d(TAG, "terminateRecord", e);
        }
    }

    Timer.TimerCallback mCountDownCallback = new Timer.TimerCallback() {
        int mSecond = -1;
        @Override
        public void onTime(final int seconds) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTimerText.setImageResource(NUMBERS[seconds]);
                    if(mSecond != seconds)
                        mSoundManager.play(R.raw.audio_count);
                    mSecond = seconds;
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
                    if (action == null)
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
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        mCameraFragment.stopRecording();
//                        showAfterRecordMenu();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }
    };

    private void showAfterRecordMenu() {
        mMenuContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_replay)
    public void replay() {
        Intent intent = new Intent(getApplicationContext(), ReplayActivity.class);
        intent.putExtra("type", mGreetingType);
        intent.putExtra("record", mRecord);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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

        showTimerLayout();
        mRecord.delete();
        File f = new File(mRecord.path);
        if (f.exists()) {
            f.delete();
        }
        mRecord = null;
    }


    private void initSequence() {
        mGreetingSequence = new Sequence();
        mGreetingSequence.add(new RecordAction(0));
        mGreetingSequence.add(new ShowNotiTextAction(1));
        mGreetingSequence.add(new SoundAction(2, mSoundManager,R.raw.audio_greeing_start));
        mGreetingSequence.add(new RemoveNotiTextAction(4));
        mGreetingSequence.add(new RecordStopAction(7));
        mGreetingSequence.add(new SoundAction(8, mSoundManager,R.raw.audio_greeting_end));
    }

    private void resetSequeceHead()
    {
        mGreetingSequence.resetHead();
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

    class ShowNotiTextAction extends Action{

        public ShowNotiTextAction(int timing) {
            super(timing);
        }

        @Override
        public void action() {
            mTimerChooseText.setImageResource(R.drawable.text_start_greeting);
            mTimerChooseText.setVisibility(View.VISIBLE);
        }
    }

    class RemoveNotiTextAction extends Action{

        public RemoveNotiTextAction(int timing) {
            super(timing);
        }

        @Override
        public void action() {
            mTimerChooseText.setVisibility(View.GONE);
        }
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

        public void resetHead() {
            mHead = 0;
        }
    }

    class SoundAction extends Action {
        SoundManager mManager;
        int mSoundRes = -1;
        public SoundAction(int time, SoundManager manager, int soundres) {
            super(time);
            mManager = manager;
            mSoundRes = soundres;
        }

        @Override
        public void action() {
            mManager.play(mSoundRes);
        }
    }

    class TextReadAction extends Action {
        String mText;
        String mUtteranceId;
        HashMap<String, String> mParameters = new HashMap<String, String>();

        public TextReadAction(int timing, String text) {
            super(timing);
            mText = text;
            mUtteranceId = "default";
        }

        public TextReadAction(int timing, String text, String utterId) {
            super(timing);
            mText = text;
            mUtteranceId = utterId;
        }

        @Override
        public void action() {


//            final int id = tts.speak(mText, TextToSpeech.QUEUE_FLUSH, mUtteranceId);
//            tts.speak(mText, TextToSpeech.QUEUE_FLUSH, null, mUtteranceId);
            Log.d(TAG, "speak action");
        }
    }

    class NotiAndReadAction extends TextReadAction {

        public NotiAndReadAction(int timing, String text) {
            super(timing, text);
            initUtterProgressListner();
        }

        private void initUtterProgressListner() {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTimerChooseText.setImageResource(R.drawable.text_start_greeting);
                            mTimerChooseText.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.d(TAG, "onDone");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTimerChooseText.setVisibility(View.GONE);
                        }
                    });

                    tts.setOnUtteranceProgressListener(null);
                }

                @Override
                public void onError(String utteranceId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTimerChooseText.setVisibility(View.GONE);
                        }
                    });
                    tts.setOnUtteranceProgressListener(null);
                }
            });
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

    class RecordStopAction extends Action {
        public RecordStopAction(int timing){
            super(timing);
        }

        @Override
        public void action() {
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
    }
}