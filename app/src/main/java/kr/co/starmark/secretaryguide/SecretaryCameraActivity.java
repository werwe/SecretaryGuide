package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class SecretaryCameraActivity extends Activity implements SecrataryCameraFragment.Contract {
    public static final String TAG = SecretaryCameraActivity.class.getName();
    SecrataryCameraFragment frag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_main);
        frag = SecrataryCameraFragment.newInstance(true);
        getFragmentManager().beginTransaction().replace(R.id.container, frag).commit();
    }

    @Override
    public boolean isSingleShotMode() {
        return (false);
    }

    @Override
    public void setSingleShotMode(boolean mode) {
        // hardcoded, unused
    }

    public void recordVideo(View v) {
        Log.d(TAG, "record video");
        try {
            frag.record();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchCamera(View view) {
        Log.d(TAG, "switch camera");
    }
}