package com.naruto.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import com.naruto.view.R;

import java.lang.reflect.Field;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2018/9/14 0014
 * @Note
 */
public class CustomViewHelper {
    private final static int DEFAULT_STROKE_COLOR = 0x00000000;
    private final static int DEFAULT_STROKE_WIDTH = 1;//单位：dp
    private final static int DEFAULT_STROKE_DASH_SIZE = 3;//默认虚线间隔，单位：dp
    private final static int STROKE_TYPE_SOLID = 0;//实线
    private final static int STROKE_TYPE_DASH = 1;//虚线
    private float strokeWidth;//描边画笔宽度//单位：px
    private int strokeColor;//描边颜色
    private int radius;//圆角半径
    private int strokeType;//描边类型
    private int strokeDashLength;//虚线线段长度
    private int strokeDashInterval;//虚线线段间隔
    private float constraintRadiusWithWidth_percent;//圆角半径相对于控件宽度的比例
    private float constraintRadiusWithHeight_percent;//圆角半径相对于控件高度的比例
    private Context context;
    private String customViewName;
    private View view;


    public CustomViewHelper(Context context, String customViewName, View view) {
        this.context = context;
        this.customViewName = customViewName;
        this.view = view;
    }

    public void init(TypedArray ta) {
        radius = ta.getDimensionPixelSize(getAttrsId("radius"), 0);
        strokeWidth = ta.getDimensionPixelSize(getAttrsId("strokeWidth"), dip2px(DEFAULT_STROKE_WIDTH));
        strokeColor = ta.getColor(getAttrsId("strokeColor"), DEFAULT_STROKE_COLOR);
        strokeType = ta.getInt(getAttrsId("strokeType"), STROKE_TYPE_SOLID);
        strokeDashLength = ta.getDimensionPixelSize(getAttrsId("strokeDashLength"), dip2px(DEFAULT_STROKE_DASH_SIZE));
        strokeDashInterval = ta.getDimensionPixelSize(getAttrsId("strokeDashInterval"), dip2px(DEFAULT_STROKE_DASH_SIZE));
        constraintRadiusWithWidth_percent = ta.getFloat(getAttrsId("constraintRadiusWithWidth_percent"), 0);
        constraintRadiusWithHeight_percent = ta.getFloat(getAttrsId("constraintRadiusWithHeight_percent"), 0);
    }

    private int getAttrsId(String attrs) {
        int id = 999999999;
        try {
            Field field = R.styleable.class.getDeclaredField(customViewName + "_" + attrs);
            id = (int) field.get(R.styleable.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    private int dip2px(float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setRadiusByPercent() {
        if (radius == 0) {
            if (constraintRadiusWithWidth_percent > 0) {
                setRadiusByPercent(true);
            } else if (constraintRadiusWithHeight_percent > 0) {
                setRadiusByPercent(false);
            }
        }
    }

    /**
     * 根据比例设置圆角半径
     *
     * @param isByWidth
     */
    private void setRadiusByPercent(boolean isByWidth) {
        int base = 0;
        float percent = 0;
        if (isByWidth) {
            base = view.getWidth();
            percent = constraintRadiusWithWidth_percent;
        } else {
            base = view.getHeight();
            percent = constraintRadiusWithHeight_percent;
        }
        if (percent > 0) {
            if (percent > 1) {
                percent = 1;
            }
            radius = (int) (base * percent);
        }
    }

    /**
     * 绘制描边
     *
     * @param paint
     * @param canvas
     */
    public void drawStroke(Paint paint, Canvas canvas) {
        if (strokeColor == DEFAULT_STROKE_COLOR) {
            return;
        }
        paint.reset();
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);

        RectF rf = new RectF(canvas.getClipBounds());
        float padding = strokeWidth / 2;
        rf.left += padding;
        rf.top += padding;
        rf.right -= padding;
        rf.bottom -= padding;

        if (strokeType == STROKE_TYPE_DASH) {
            paint.setPathEffect(new DashPathEffect(new float[]{strokeDashLength, strokeDashInterval}, 0));
        }
        if (radius > 0) {//圆角矩形
            paint.setAntiAlias(true);
            canvas.drawRoundRect(rf, radius, radius, paint);
        } else {//矩形
            canvas.drawRect(canvas.getClipBounds(), paint);
        }
    }

    /**
     * 设置view为圆角
     *
     * @param canvas
     */
    public void makeToRoundrect(Canvas canvas) {
        int paddingTop = view.getPaddingTop();
        int paddingBottom = view.getPaddingBottom();
        int paddingLeft = view.getPaddingLeft();
        int paddingRight = view.getPaddingRight();
        if (radius > 0 && view.getWidth() >= 2 * radius && view.getHeight() >= 2 * radius) {
            Path path = new Path();
            RectF rf = new RectF(canvas.getClipBounds());
            rf.left += paddingLeft;
            rf.top += paddingTop;
            rf.right -= paddingRight;
            rf.bottom -= paddingBottom;
            path.addRoundRect(rf, radius - paddingLeft, radius - paddingBottom, Path.Direction.CW);
            canvas.clipPath(path);
        }
    }
}
