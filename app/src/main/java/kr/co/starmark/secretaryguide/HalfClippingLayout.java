package kr.co.starmark.secretaryguide;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;


public class HalfClippingLayout extends FrameLayout {


    private int halfDivider = 1;

    public HalfClippingLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public HalfClippingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HalfClippingLayout(Context context, AttributeSet attrs, int defStyle) {
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


    public void toggleHalf()
    {
        if(halfDivider == 1)
            halfDivider = 2;
        else
            halfDivider = 1;
        invalidate();
    }

}
