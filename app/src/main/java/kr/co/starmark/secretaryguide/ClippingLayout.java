package kr.co.starmark.secretaryguide;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;


public class ClippingLayout extends FrameLayout {

    public static final int CLIPPING_FULL = 0;
    public static final int CLIPPING_HALF = 2;
    public static final int CLIPPING_NONE = 1;



    private int halfDivider = 1;

    public ClippingLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public ClippingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ClippingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(0, 0, getWidth() / halfDivider, getHeight());
        super.onDraw(canvas);
    }


    public void setClipMode(int clipMode)
    {
        if(clipMode == CLIPPING_FULL)
            halfDivider = CLIPPING_FULL;
        else if(clipMode == CLIPPING_HALF)
            halfDivider = CLIPPING_HALF;
        else if (clipMode == CLIPPING_NONE)
            halfDivider = CLIPPING_NONE;
        invalidate();
    }

}
