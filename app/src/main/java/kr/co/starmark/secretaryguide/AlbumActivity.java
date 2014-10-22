package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class AlbumActivity extends Activity {

    private static final String TAG = AlbumActivity.class.getName();
    List<GreetingVideo> mRecords;

    @InjectView(R.id.list)
    ListView mList;
    @InjectView(R.id.progress)
    ProgressBar mProgress;
    @InjectView(R.id.progressContainer)
    LinearLayout mProgressContainer;

    AlbumAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.inject(this);
        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    private void queryData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mRecords = new Select().from(GreetingVideo.class).orderBy("Date DESC").execute();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mProgressContainer.setVisibility(View.GONE);
                if(mRecords == null || mRecords.isEmpty()); //비어있는 화면
                mAdapter = new AlbumAdapter(getApplicationContext());
                mList.setAdapter(mAdapter);
                Log.d(TAG, "Records:" + mRecords.size());
            }
        }.execute();
    }

    class AlbumAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public AlbumAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mRecords.size();
        }

        @Override
        public GreetingVideo getItem(int i) {
            return mRecords.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            VideoRecordItem holder = null;
            if (view == null) {
                view = mInflater.inflate(R.layout.album_listitem, null);
                view.setTag(holder = new VideoRecordItem(view));
            } else
                holder = (VideoRecordItem) view.getTag();

            holder.setItem(getItem(i));
            return view;
        }
    }

    static class VideoRecordItem {

        @InjectView(R.id.thumbnail)
        ImageView mThumbnail;
        @InjectView(R.id.title)
        TextView mTitle;
        @InjectView(R.id.date)
        TextView mDate;

        VideoRecordItem(View view) {
            ButterKnife.inject(this, view);
        }

        public ImageView getThumbnail() {
            return mThumbnail;
        }

        public void setThumbnail(ImageView thumbnail) {
            mThumbnail = thumbnail;
        }

        public TextView getTitle() {
            return mTitle;
        }

        public void setTitle(TextView title) {
            mTitle = title;
        }

        public TextView getDate() {
            return mDate;
        }

        public void setDate(TextView date) {
            mDate = date;
        }

        public void setItem(GreetingVideo item) {
            mTitle.setText(getGreetingType(item.type)+" - "+getfaceSide(item.side));
            mDate.setText(item.date);
            new AsyncTask<String,Void,Bitmap>(){
                @Override
                protected Bitmap doInBackground(String... path) {
                    return ThumbnailUtils.createVideoThumbnail(path[0],MediaStore.Images.Thumbnails.MICRO_KIND);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    mThumbnail.setImageBitmap(bitmap);
                }
            }.execute(item.path);
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
    }
}
