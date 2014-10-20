package kr.co.starmark.secretaryguide;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import kr.co.starmark.secretaryguide.blur.Blur;

/**
 * Created by starmark on 2014. 10. 16..
 */
public class MenuDialogFragment extends DialogFragment {

    int mNum;

    @InjectView(R.id.blured_back)
    ImageView mBluredBack;

    @InjectView(R.id.close)
    ImageButton mClose;
    @InjectView(R.id.menu_00)
    RadioButton mMenu00;
    @InjectView(R.id.menu_01)
    RadioButton mMenu01;
    @InjectView(R.id.menu_02)
    RadioButton mMenu02;
    @InjectView(R.id.menu_03)
    RadioButton mMenu03;

    @InjectViews({R.id.menu_00,R.id.menu_01,R.id.menu_02,R.id.menu_03})
    List<RadioButton> mMenus;
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static MenuDialogFragment newInstance(int selectedMenu) {
        MenuDialogFragment f = new MenuDialogFragment();
        Bundle args = new Bundle();
        args.putInt("num", selectedMenu);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Blurry background( texture view의 드로잉 캐시가 생성이 안된다.)
        //View decorView = getActivity().getWindow().getDecorView();

        //Bitmap b = decorView.getDrawingCache();
        //if(b == null) {
        //    decorView.buildDrawingCache();
        //    b = decorView.getDrawingCache();
        //}

        //Bitmap bluredBitmap = Blur.fastblur(getActivity().getApplicationContext(), b, 12);
        //mBluredBack.setImageBitmap(bluredBitmap);

        return null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setDimAmount(0.90f);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);

        View v = View.inflate(dialog.getContext(),R.layout.menu_dialog, null);
        ButterKnife.inject(this, v);
        dialog.setContentView(v);

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

    @OnClick(R.id.close)
    public void close() {
        dismiss();
    }

    public void show() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.menu_00, R.id.menu_01, R.id.menu_02, R.id.menu_03})
    public void onMenuClick(final RadioButton v){
        ButterKnife.apply(mMenus,new ButterKnife.Action<RadioButton>() {
            @Override
            public void apply(RadioButton view, int index) {
                if (v.getId() != view.getId()) {
                    view.setChecked(false);
                }
            }
        });
    }
}
