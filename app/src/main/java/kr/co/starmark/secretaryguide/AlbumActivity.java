package kr.co.starmark.secretaryguide;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemSelectionSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AlbumActivity extends Activity {

    private static final String TAG = AlbumActivity.class.getName();
    public static final int NORMAL = 0;
    public static final int DELETE = 1;

    List<GreetingVideo> mRecords;

    @InjectView(R.id.list)
    TwoWayView mRecyclerView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;
    @InjectView(R.id.progressContainer)
    LinearLayout mProgressContainer;

    LayoutAdapter mAdapter;
    @InjectView(R.id.left)
    ImageButton mLeft;
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.right)
    ImageButton mRight;

    private int mode = NORMAL;

    private ItemSelectionSupport selection;
    private ItemClickSupport itemclick;

    private ActionMode mActionMode = null;
    private TextView mDeleteCount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        setActionBar();
        ButterKnife.inject(this);
        mLeft.setVisibility(View.VISIBLE);
        mLeft.setImageResource(R.drawable.icon_back);
        mRight.setImageResource(R.drawable.icon_trash);
        mTitle.setText("앨범");
        setRecyclerView();
        mRecyclerView.setHasFixedSize(true);
        queryData();

    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar);
        ButterKnife.inject(actionBar.getCustomView());
    }

    @OnClick(R.id.left)
    public void left()
    {
        finish();
    }

    @OnClick(R.id.right)
    public void right() {
        selection.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
        switchMode(DELETE);
        switchActionBar();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    private void setRecyclerView() {
        selection = ItemSelectionSupport.addTo(mRecyclerView);

        itemclick = ItemClickSupport.addTo(mRecyclerView);
        itemclick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (mode == NORMAL) {
                    GreetingVideo video = mAdapter.getItem(position);
                    Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
                    intent.putExtra("record", video);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
                } else {
                    boolean check = selection.isItemChecked(position);
                    selection.setViewChecked(ButterKnife.findById(view, R.id.delete_check), check);
                    selection.setItemChecked(position, check);
                    mDeleteCount.setText(selection.getCheckedItemCount()+" 선택됨");
                    if (selection.getCheckedItemCount() <= 0)
                        mActionMode.finish();
                }
            }
        });

        itemclick.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
                if (mode == NORMAL) {
                    selection.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                    switchMode(DELETE);
                    switchActionBar();
                    selection.setViewChecked(ButterKnife.findById(view, R.id.delete_check), true);
                    selection.setItemChecked(position, true);
                }
                return false;
            }
        });


    }

    private void switchMode(int mode) {
        this.mode = mode;
        if (mode == NORMAL) {
            if(selection.getCheckedItemCount() > 0)
                selection.clearChoices();
        }
    }

    private void switchActionBar() {
        mActionMode = startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                switchMode(NORMAL);
                selection.setChoiceMode(ItemSelectionSupport.ChoiceMode.NONE);
            }
        });
        mActionMode.setCustomView(View.inflate(getApplicationContext(), R.layout.actionmode_delete, null));
        mDeleteCount = ButterKnife.findById(mActionMode.getCustomView(), R.id.delete_count);
        ButterKnife.findById(mActionMode.getCustomView(), R.id.action_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
            }
        });
    }

    private void deleteAll() {
        if(selection.getCheckedItemCount() == 0) {
            mActionMode.finish();
            switchMode(NORMAL);
            return;
        }

        //2개 이상을 한꺼번에 못 지움.
        ActiveAndroid.beginTransaction();
        for(int j = 0 ; j < selection.getCheckedItemCount() ; j++)
        {
            int length = mAdapter.getItemCount();
            for (int i = 0; i < length; i++) {
                if (selection.isItemChecked(i)) {
                    GreetingVideo record = mAdapter.removeItem(i);
                    GreetingVideo.delete(GreetingVideo.class, record.getId());
                    File f = new File(record.path);
                    if (f.exists()) {
                        f.delete();
                    }
                    break;
                }
            }
        }
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        int count = mAdapter.getItemCount();

        mActionMode.finish();
        switchMode(NORMAL);
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
                mAdapter.setSelection(selection);
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        if (mode == NORMAL)
            super.onBackPressed();
        else {
            switchMode(NORMAL);
            selection.clearChoices();
        }
    }
}
