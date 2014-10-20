package kr.co.starmark.secretaryguide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class PreviewFrameLayout extends RelativeLayout {

    public static final String TAG = "PreviewFrameLayout";

    public interface OnSizeChangedListener {
        public void onSizeChanged(int width, int height);
    }

    private double mAspectRatio;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private OnSizeChangedListener mListener;


    public PreviewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAspectRatio(4f / 3f);
    }

    public void setAspectRatio(double ratio) {
        if (ratio <= 0.0) {
            throw new IllegalArgumentException();
        }

        ratio = 1 / ratio;

        if (mAspectRatio != ratio) {
            mAspectRatio = ratio;
            requestLayout();
        }
    }

    public void setPreviewSize(int width, int height) {
        mPreviewWidth = height;
        mPreviewHeight = width;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        float width = MeasureSpec.getSize(widthSpec);
        float height = MeasureSpec.getSize(heightSpec);

        setMeasuredDimension((int)width, (int)height);

        super.onMeasure(MeasureSpec.makeMeasureSpec((int) width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY));
    }
    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(getChildCount() > 0) {

            final View child = getChildAt(0);
            int height = b - t;
            int width = 0;

            if(mPreviewHeight == 0) {
                child.layout(l, t, r, b);
                return;
            }
            width = mPreviewWidth * height / mPreviewHeight;
//            Log.d(TAG, "mPreviewWidth:" + mPreviewWidth);
//            Log.d(TAG, "mPreviewHeight:" + mPreviewHeight);
//            Log.d(TAG, "width:" + width);
//            Log.d(TAG, "height:" + height);
//            Log.d(TAG, String.format("l:%d,t:%d,r:%d,b:%d",l,t,r,b));
            int parentWidth = r-l;
            int offset = (parentWidth - width) / 2;
            child.layout(l+offset,t,r-offset,b);
        }
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mListener != null) mListener.onSizeChanged(w, h);
    }
}