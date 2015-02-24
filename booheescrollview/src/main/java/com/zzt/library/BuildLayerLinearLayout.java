package com.zzt.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by zzt on 2015/2/16.
 */
public class BuildLayerLinearLayout extends LinearLayout {
    public BuildLayerLinearLayout(Context context) {
        super(context);
    }

    public BuildLayerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BuildLayerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
