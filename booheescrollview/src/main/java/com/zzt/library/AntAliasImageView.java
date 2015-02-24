package com.zzt.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zzt on 2015/2/23.
 */
public class AntAliasImageView extends ImageView {

    public AntAliasImageView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public AntAliasImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public AntAliasImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        super.dispatchDraw(canvas);
    }
}

