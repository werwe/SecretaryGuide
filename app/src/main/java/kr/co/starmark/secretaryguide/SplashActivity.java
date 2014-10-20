package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends Activity {

    public static final String TAG = "SplashActivity";

    private Runnable mStartAct = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(), RecordActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    };

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(mStartAct, 1500);
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(mStartAct);
        super.onBackPressed();
    }
}