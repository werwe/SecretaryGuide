package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by starmark on 2014. 10. 10..
 */
public class ClippingVideoViewTest extends Activity {


    @InjectView(R.id.first_videoview)
    kr.co.starmark.secretaryguide.VideoView mFirstVideoview;
    @InjectView(R.id.second_videoview)
    VideoView mSecondVideoview;
    @InjectView(R.id.cliplayout)
    HalfClippingLayout mCliplayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_custom_video);
        ButterKnife.inject(this);

        mFirstVideoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/doiwannaknow"));
        mFirstVideoview.start();
        mSecondVideoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/midnight_city"));
        mSecondVideoview.start();
    }

    @OnClick(R.id.clip)
    public void clip() {
        Log.d("ClippingVideoViewTest", "Clipping toggle");
        mCliplayout.toggleHalf();
    }
}
