package com.example.pc.mybeziercurveview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 三阶
 * Created by pc on 2017/5/24.
 */
public class MyBezierCurveCubic extends View{

    private Paint mPaint;
    private Path mPath;
    private int centerX,centerY;
    private PointF start,end,control1,control2;
    public static final int CONTROL_ONE = 0;
    public static final int CONTROL_TWO = 1;
    private int control = CONTROL_ONE;

    public MyBezierCurveCubic(Context context) {
        super(context);
        init();
    }

    public MyBezierCurveCubic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBezierCurveCubic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        start = new PointF(0,0);
        end = new PointF(0,0);
        control1 = new PointF(0,0);
        control2 = new PointF(0,0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w/2;
        centerY = h/2;

        //初始化数据点和控制点
        start.x = centerX - 200;
        start.y = centerY;
        end.x = centerX + 200;
        end.y = centerY;
        control1.x = centerX - 200;
        control1.y = centerY - 200;
        control2.x = centerX + 200;
        control2.y = centerY + 200;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制数据点和控制点
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(20);
        canvas.drawPoint(start.x, start.y, mPaint);
        canvas.drawPoint(end.x,end.y,mPaint);
        canvas.drawPoint(control1.x,control1.y,mPaint);
        canvas.drawPoint(control2.x, control2.y, mPaint);
        //绘制辅助线
        mPaint.setStrokeWidth(4);
        canvas.drawLine(start.x, start.y, control1.x, control1.y, mPaint);
        canvas.drawLine(control1.x, control1.y, control2.x, control2.y, mPaint);
        canvas.drawLine(control2.x, control2.y, end.x, end.y, mPaint);
        //绘制三阶贝塞尔曲线
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8);
        mPath.reset();
        mPath.moveTo(start.x,start.y);
        mPath.cubicTo(control1.x,control1.y,control2.x,control2.y,end.x,end.y);
        canvas.drawPath(mPath,mPaint);
    }

    public void setControl(int control){
        this.control = control;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(control == CONTROL_ONE){
                    control1.x = event.getX();
                    control1.y = event.getY();
                }else{
                    control2.x = event.getX();
                    control2.y = event.getY();
                }
                invalidate();
            break;
        }
        return true;
    }
}
