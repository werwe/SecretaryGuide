package kr.co.starmark.secretaryguide;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
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

    private int ICONS[] = {
            R.drawable.title_icon_00,
            R.drawable.title_icon_01,
            R.drawable.title_icon_02,
            R.drawable.title_icon_04,
            R.drawable.title_icon_03
    };

    private static final String[] TITLES = {"  가벼운 인사", "  보통 인사", "  정중한 인사", "  전화 예절", "  내방객 응대"};

    GreetingVideo mRecord;
    int mGreetingType = 1; // 1 , 2 , 3
    int mFaceSide = 1; // 1 , 2
    @InjectView(R.id.pager_header)
    PagerTabStrip mPagerHeader;
    @InjectView(R.id.pager)
    ViewPager mPager;

    //ActionBar
    @InjectView(R.id.left)
    ImageButton mLeft;
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
        mPagerHeader.setTabIndicatorColor(getResources().getColor(R.color.base));
        mPagerHeader.setTextSpacing(10);
        mPagerHeader.setNonPrimaryAlpha(0.3f);
        mPagerHeader.setTextColor(Color.argb(255,10,89,152));
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
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
    }

    @OnClick(R.id.start_video_recorder)
    public void onVideoRecoderStart() {
        if(mPager.getCurrentItem() > 2)
            return;

        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra("type", mPager.getCurrentItem() + 1);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_open_up_translate,R.anim.activity_close_scale);
    }

    private Uri getGuideVideoUri(int i){
        return Uri.parse("android.resource://" + getPackageName() + "/raw/greeting_guide_"+i);
    }

    private Uri getVideoUri(int type, int side) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/greeting_sample_" + type + "_" + side);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) { }

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

        if(i < 3)
            mTitle.setText("인사 예절");
        else if(i == 3 )
            mTitle.setText("전화 예절");
        else if(i == 4)
            mTitle.setText("내방객 응대");
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
                return PageFragment.create(
                        new Uri[]{
                                getGuideVideoUri(0)
                        }
                );
            }
            if(position == 3)
                return PageFragment.create(new Uri[]{getGuideVideoUri(1)});
            if(position == 4)
                return PageFragment.create(new Uri[]{getGuideVideoUri(2)});
            return  null;
        }

        public CharSequence getPageTitle(int position) {
            Drawable myDrawable = getResources().getDrawable(ICONS[position]);
            SpannableStringBuilder sb = new SpannableStringBuilder(TITLES[position]);

            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return sb;
        }
    }

    public static class PageFragment extends Fragment {

        public static final String VIDEOS = "ARG_URIES";
        private Uri[] mVideoUris;

        @InjectView(R.id.preview)
        ImageView mPreview;

        @InjectView(R.id.videoContainer)
        RelativeLayout mVideoContainer;

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
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mPreview.getWidth(), mPreview.getHeight());
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mVideoContainer.addView(mVideo, params);
        }

        @OnClick(R.id.btn_play)
        public void play(final View view) {
            if (mVideo == null) {
                addVideoView();
            }
            mVideo.setVideoURI(mVideoUris[0]);
            mVideo.start();
            mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
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