package kr.co.starmark.secretaryguide;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.VideoView;

import com.melnykov.fab.FloatingActionButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GuideActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    final Logger logger = LoggerFactory.getLogger(GuideActivity.class);
    public static final String TAG = "RecordActivity";

    private static final String[] TITLES = {"가벼운 인사", "보통 인사", "정중한 인사", "menu4", "menu5"};

    GreetingVideo mRecord;
    int mGreetingType = 1; // 1 , 2 , 3
    int mFaceSide = 1; // 1 , 2
    @InjectView(R.id.pager_header)
    PagerTabStrip mPagerHeader;
    @InjectView(R.id.pager)
    ViewPager mPager;

    //ActionBar
    @InjectView(R.id.left)
    Button mLeft;
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.right)
    ImageButton mRight;

    @InjectView(R.id.start_video_recorder)
    FloatingActionButton mStartVideoRecorder;
    private SampleFragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        setActionBar();
        ButterKnife.inject(this);
        mPagerAdapter = new SampleFragmentPagerAdapter();
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
        mPagerHeader.setGravity(Gravity.CENTER);
        mPagerHeader.setTabIndicatorColor(Color.CYAN);
        mPagerHeader.setTextSpacing(10);
        mPagerHeader.setTextColor(Color.BLUE);
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar);
        ButterKnife.inject(actionBar.getCustomView());
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

        Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.start_video_recorder)
    public void onVideoRecoderStart() {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(intent);
    }

    private Uri getVideoUri(int type, int side) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/greeting_sample_" + type + "_" + side);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if (i == 1) {
            Log.d(TAG, String.format("i:%d,v:%f,i2:%d", i, v, i2));
        }

    }

    @Override
    public void onPageSelected(int i) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment f : fragmentList) {
            if (f instanceof PageFragment) {
                PageFragment fragment = (PageFragment) f;
                fragment.removeVideoView();
            }
        }
        if (i > 2)
            mStartVideoRecorder.hide(true);
        else
            mStartVideoRecorder.show(true);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 5;

        public SampleFragmentPagerAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 3) {
                Log.d(TAG, "Position:" + (position + 1));
                return PageFragment.create(
                        new Uri[]{
                                getVideoUri(position + 1, 1),
                                getVideoUri(position + 1, 1),
                                getVideoUri(position + 1, 2)
                        }
                );
            }
            return PageFragment.create(new Uri[]{getVideoUri(2, 1)});
        }

        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    public static class PageFragment extends Fragment {

        public static final String VIDEOS = "ARG_URIES";
        private Uri[] mVideoUris;

        @InjectView(R.id.preview)
        ImageView mPreview;

        @InjectView(R.id.videoContainer)
        FrameLayout mVideoContainer;

        @InjectView(R.id.btn_play)
        ImageView mBtnPlay;

        VideoView mVideo;

        private int mCnt = 0;

        public static PageFragment create(Uri[] videos) {
            Bundle args = new Bundle();
            args.putParcelableArray(VIDEOS, videos);
            PageFragment fragment = new PageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mVideoUris = (Uri[]) getArguments().getParcelableArray(VIDEOS);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_page, container, false);
            ButterKnife.inject(this, view);
            loadPreview();
            return view;
        }

        private void loadPreview() {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getActivity().getApplicationContext(), mVideoUris[0]);
            Bitmap b = retriever.getFrameAtTime();
            mPreview.setImageBitmap(b);
        }

        private void addVideoView() {
            mVideo = new VideoView(getActivity().getApplicationContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mPreview.getWidth(), mPreview.getHeight());
            params.gravity = Gravity.CENTER;
            mVideoContainer.addView(mVideo, params);
        }

        @OnClick(R.id.btn_play)
        public void play(final View view) {
            Log.d(TAG, "play");
            if (mVideo == null) {
                addVideoView();
            }
            mVideo.setVideoURI(mVideoUris[0]);
            mVideo.start();
            mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.d(TAG, "onPrepared");
                    mediaPlayer.seekTo(0);
                    mVideo.start();
                    view.setVisibility(View.GONE);
                    mPreview.setVisibility(View.GONE);
                    mVideo.setOnPreparedListener(null);
                }
            });
            mVideo.setVideoURI(mVideoUris[mCnt]);
            mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mVideoUris.length > 1 && mCnt < mVideoUris.length - 1) {
                        mVideo.setVideoURI(mVideoUris[++mCnt]);
                        mVideo.start();
                    } else {
                        mCnt = 0;
                        mBtnPlay.setVisibility(View.VISIBLE);
                        mPreview.setVisibility(View.VISIBLE);
                    }
                }
            });
            mVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    return true;
                }
            });
        }

        @Override
        public void onPause() {
            super.onPause();
            stopPlay();
        }

        @Override
        public void onResume() {
            super.onResume();
            mBtnPlay.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.VISIBLE);
        }

        public void stopPlay() {
            if (mVideo != null) {
                mVideo.stopPlayback();
                mCnt = 0;
                mVideo.setVideoURI(mVideoUris[mCnt]);
                mVideo.seekTo(100);
            }

            mBtnPlay.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.VISIBLE);
        }

        public void removeVideoView() {
            Log.d(TAG, "removeVideoView");
            if (mVideo != null)
                mVideo.stopPlayback();

            mBtnPlay.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.VISIBLE);
            mVideoContainer.removeAllViews();

            mVideo = null;
            mCnt = 0;
        }
    }
}