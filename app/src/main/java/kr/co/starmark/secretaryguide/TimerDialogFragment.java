package kr.co.starmark.secretaryguide;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by starmark on 2014. 10. 16..
 */
public class TimerDialogFragment extends DialogFragment {

    @InjectView(R.id.no_delay)
    Button mNoDelay;
    @InjectView(R.id.delay_5)
    Button mDelay5;
    @InjectView(R.id.delay_10)
    Button mDelay10;

    public static interface TimeAmountSelectCallback {
        public void onTimeAmount(int seconds);
    }

    public void setTimerCallback(TimeAmountSelectCallback callback) {
        this.mTimerCallback = callback;
    }

    TimeAmountSelectCallback mTimerCallback;

    static TimerDialogFragment newInstance() {
        TimerDialogFragment f = new TimerDialogFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setDimAmount(0.90f);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        View v = View.inflate(dialog.getContext(), R.layout.timer_layout, null);
        dialog.setContentView(v);
        ButterKnife.inject(this, v);

        WindowManager.LayoutParams parmas = window.getAttributes();
        parmas.width = WindowManager.LayoutParams.MATCH_PARENT;
        parmas.height = WindowManager.LayoutParams.MATCH_PARENT;

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.no_delay)
    public void noDelay() {
        if (mTimerCallback != null)
            mTimerCallback.onTimeAmount(0);
        dismiss();
    }

    @OnClick(R.id.delay_5)
    public void delay5() {
        if (mTimerCallback != null)
            mTimerCallback.onTimeAmount(5);
        dismiss();
    }

    @OnClick(R.id.delay_10)
    public void delay10() {
        if (mTimerCallback != null)
            mTimerCallback.onTimeAmount(10);
        dismiss();
    }
}

