package kr.co.starmark.secretaryguide;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class GuideActivity extends FragmentActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        setActionBar();
        ButterKnife.inject(this);
        mPager.setAdapter(new SampleFragmentPagerAdapter());
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
//        MenuDialogFragment dialog = MenuDialogFragment.newInstance(0);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        dialog.show(ft, "dialog");
        Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
        startActivity(intent);
    }

    private Uri getVideoUri(int type, int side) {
        return Uri.parse("android.resource://" + getPackageName() + "/raw/greeting_sample_" + type + "_" + side);
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
            return PageFragment.create(
                    getVideoUri(mPager.getCurrentItem() + 1, 1) ,
                    getVideoUri(mPager.getCurrentItem() + 1, 2)
            );
        }

        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    public static class PageFragment extends Fragment {
        public static final String FRONT = "ARG_FRONT";
        public static final String SIDE = "ARG_SIDE";
        @InjectView(R.id.video)
        android.widget.VideoView mVideo;
        @InjectView(R.id.btn_play)
        ImageView mBtnPlay;

        private Uri mFrontVideo;
        private Uri mSidevideo;

        public static PageFragment create(Uri front, Uri side) {
            Bundle args = new Bundle();
            args.putParcelable(FRONT, front);
            args.putParcelable(SIDE, side);
            PageFragment fragment = new PageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mFrontVideo = getArguments().getParcelable(FRONT);
            mSidevideo = getArguments().getParcelable(SIDE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_page, container, false);
            ButterKnife.inject(this, view);

            mVideo.setVideoURI(mFrontVideo);
            mVideo.seekTo(100);
            return view;
        }

        @OnClick(R.id.btn_play)
        public void play(final View view)
        {
            mVideo.start();
            view.setVisibility(View.GONE);
            mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    view.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
