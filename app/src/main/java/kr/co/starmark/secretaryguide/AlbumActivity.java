package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.activeandroid.query.Select;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class AlbumActivity extends Activity {

    private static final String TAG = AlbumActivity.class.getName();
    List<GreetingVideo> mRecords;

    @InjectView(R.id.list)
    TwoWayView mRecyclerView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;
    @InjectView(R.id.progressContainer)
    LinearLayout mProgressContainer;

    LayoutAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.inject(this);
        setRecyclerView();
        mRecyclerView.setHasFixedSize(true);
        queryData();
    }

    private void setRecyclerView() {
        final ItemClickSupport itemclick = ItemClickSupport.addTo(mRecyclerView);
        itemclick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                GreetingVideo video = mAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
                intent.putExtra("record", video);
                startActivity(intent);
            }
        });
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
                if (mRecords == null || mRecords.isEmpty()) ; //비어있는 화면
                mAdapter = new LayoutAdapter(AlbumActivity.this, mRecyclerView, mRecords);
                mRecyclerView.setAdapter(mAdapter);
                Log.d(TAG, "Records:" + mRecords.size());
            }
        }.execute();
    }
}
