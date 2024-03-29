package com.capitalnowapp.mobile.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class FreezeCursorEditText extends CNEditText {

    /**
     * @param context
     */
    public FreezeCursorEditText(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public FreezeCursorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public FreezeCursorEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int start, int stop) {
        // TODO Auto-generated method stub
        super.setSelection(start, stop);
    }


    @Override
    public void setSelection(int index) {
        // TODO Auto-generated method stub
        super.setSelection(index);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        setSelection(getText().length());
        super.onDraw(canvas);
    }
}
