package com.biz.navimate.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.biz.navimate.R;
import com.biz.navimate.objects.Statics;

/**
 * Created by Jagannath on 07-11-2017.
 */

public class VwPhotoDraw extends View
{

    private static final float STROKE_WIDTH         = 10f;
    private static final float HALF_STROKE_WIDTH    = STROKE_WIDTH / 2;
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint;
    //initial color
    private int paintColor = 0xFF660000;
    Rect mSrcRectF;
    Rect mDestRectF;
    Matrix mMatrix;
    Bitmap image;
    Drawable d;

    RectF dirtyRect = new RectF();

    float lastTouchX;
    float lastTouchY;


    public VwPhotoDraw(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    //setup drawing
    private void setupDrawing(){

        //prepare for drawing and setup paint stroke properties
        drawPath = new Path();
        drawPaint = new Paint();

        mMatrix = new Matrix();
        mSrcRectF = new Rect();
        mDestRectF = new Rect();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(15.0f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        image = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
        // Setting size of Source Rect
        //mSrcRectF.set(0, 0,image.getWidth(),image.getHeight());
        System.out.println("WIDTH: "+image.getWidth());
        System.out.println("HEIGHT: "+image.getHeight());
        System.out.println(Statics.SCREEN_SIZE.toString());

        d = getResources().getDrawable(R.drawable.test2);
        d.setBounds(0, 0,image.getWidth(), image.getHeight());
    }

    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("OnDraw Called");

        //canvas.drawBitmap(image, mSrcRectF,mDestRectF, null);
        d.draw(canvas);
        canvas.drawPath(drawPath, drawPaint);
    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    drawPath.lineTo(historicalX, historicalY);
                }
                drawPath.lineTo(eventX, eventY);
                break;
        }

        invalidate( (int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    public void startNew(){
        drawPath.reset();
        invalidate();
    }

    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }
}
