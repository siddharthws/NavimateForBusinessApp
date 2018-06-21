package com.biz.navimate.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawableImageView extends AppCompatImageView implements View.OnTouchListener
{
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    Canvas canvas;
    Paint paint;
    Matrix matrix;

    public DrawableImageView(Context context)
    {
        super(context);
        System.out.println("const 1 Called");
        setOnTouchListener(this);
    }

    public DrawableImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        System.out.println("const 2 Called");
        setOnTouchListener(this);
    }

    public DrawableImageView(Context context, AttributeSet attrs,
                             int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        System.out.println("const 3 Called");
        setOnTouchListener(this);
    }

    public void setNewImage(Bitmap bmp)
    {
        System.out.println("SetNewImage Called");
        canvas = new Canvas(bmp);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        matrix = new Matrix();
        canvas.drawBitmap(bmp, matrix, paint);
        setImageBitmap(bmp);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        System.out.println("OnTouch Called");
        int action = event.getAction();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                downx = getPointerCoords(event)[0];//event.getX();
                downy = getPointerCoords(event)[1];//event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    final float[] getPointerCoords(MotionEvent e)
    {
        System.out.println("getPointercoord Called");
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        getImageMatrix().invert(matrix);
        matrix.postTranslate(getScrollX(), getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }
}
