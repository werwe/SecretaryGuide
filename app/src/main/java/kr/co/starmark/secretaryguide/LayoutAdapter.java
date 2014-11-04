package kr.co.starmark.secretaryguide;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.VideoRecordItem> {
    private final Context mContext;
    private final TwoWayView mRecyclerView;
    private List<GreetingVideo> mRecords;
    private int mCurrentItemId = 0;

    public static class VideoRecordItem extends RecyclerView.ViewHolder {

        @InjectView(R.id.thumbnail)
        ImageView mThumbnail;
        @InjectView(R.id.title)
        TextView mTitle;
        @InjectView(R.id.date)
        TextView mDate;

        @InjectView(R.id.delete_check)
        ImageView mDeleteIcon;

        VideoRecordItem(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void checkDelete() {
            mDeleteIcon.setVisibility(View.VISIBLE);
        }

        public void uncheckDelete() {
            mDeleteIcon.setVisibility(View.GONE);
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
            mTitle.setText(getGreetingType(item.type) + " - " + getfaceSide(item.side));
            mDate.setText(item.date);
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... path) {
                    return ThumbnailUtils.createVideoThumbnail(path[0], MediaStore.Images.Thumbnails.MINI_KIND);
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

    public LayoutAdapter(Context context, TwoWayView recyclerView, List<GreetingVideo> datas) {
        mContext = context;
        mRecords = datas;
        mRecyclerView = recyclerView;
    }

    public void addItem(int position, GreetingVideo item) {
        mRecords.add(position, item);
        notifyItemInserted(position);
    }

    public GreetingVideo removeItem(int position) {
        GreetingVideo video = mRecords.remove(position);
        notifyItemRemoved(position);
        return video;
    }

    public void removeItem(GreetingVideo item) {
        int index = mRecords.indexOf(item);
        mRecords.remove(index);
        notifyItemRemoved(index);
    }

    public GreetingVideo getItem(int position) {
        return mRecords.get(position);
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    @Override
    public VideoRecordItem onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.album_listitem, parent, false);
        return new VideoRecordItem(view);
    }

    @Override
    public void onBindViewHolder(VideoRecordItem holder, int position) {
        holder.setItem(getItem(position));
    }
}