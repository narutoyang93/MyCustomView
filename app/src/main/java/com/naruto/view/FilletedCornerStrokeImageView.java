package com.naruto.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.naruto.helper.CustomViewHelper;

/**
 * @Purpose 支持圆角和描边，无需自定义shape
 * @Author Naruto Yang
 * @CreateDate 2018/9/8 0008
 * @Note
 */
public class FilletedCornerStrokeImageView extends android.support.v7.widget.AppCompatImageView {
    private Context context;
    private CustomViewHelper customViewHelper;

    public FilletedCornerStrokeImageView(Context context) {
        super(context);
        this.context = context;
    }

    public FilletedCornerStrokeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public FilletedCornerStrokeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void init(AttributeSet attrs) {
        customViewHelper = new CustomViewHelper(context, "FilletedCornerStrokeImageView", this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FilletedCornerStrokeImageView);
        //从TypedArray中取出对应的值来为要设置的属性赋值
        customViewHelper.getAttrs(ta);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        customViewHelper.setRadiusByPercent();
        customViewHelper.drawStroke(paint, canvas);
        customViewHelper.makeToRoundRect(canvas);
        super.onDraw(canvas);
    }

}
