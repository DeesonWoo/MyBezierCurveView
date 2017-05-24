package com.example.pc.mybeziercurveview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 二阶
 * Created by Deeson on 2017/5/24.
 */
public class QuadraticBezierShowView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //分别对应贝塞尔曲线、点、数据点和控制点之间的线、辅助线、辅助点
    private Paint mPaint, mPointPaint, mLinePaint, mAssistLinePaint,mAssistPointPaint;
    //绘制贝塞尔曲线的path
    private Path mPath;
    //布局的中心点
    private int centerX, centerY;
    //分别对应贝塞尔曲线的起点、终点、控制点、辅助线的起点、终点
    private PointF start, end, control, process1, process2;

    private SurfaceHolder mHolder;
    //用于绘图的canvas
    private Canvas mCanvas;
    //子线程标志位
    private boolean mIsDrawing;

    float x = 0;//贝塞尔曲线的实时点x坐标
    float y = 0;//贝塞尔曲线的实时点y坐标
    float t = 0;//实施进度，0<=t<=1

    public QuadraticBezierShowView(Context context) {
        super(context);
        init();
    }

    public QuadraticBezierShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuadraticBezierShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(60);

        mPointPaint = new Paint();
        mPointPaint.setColor(Color.BLACK);
        mPointPaint.setStrokeWidth(10);
        mPointPaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mAssistLinePaint = new Paint();
        mAssistLinePaint.setColor(Color.GREEN);
        mAssistLinePaint.setStrokeWidth(4);
        mAssistLinePaint.setStyle(Paint.Style.STROKE);

        mAssistPointPaint = new Paint();
        mAssistPointPaint.setColor(Color.GREEN);
        mAssistPointPaint.setStrokeWidth(10);
        mAssistPointPaint.setStyle(Paint.Style.FILL);

        start = new PointF(0, 0);
        end = new PointF(0, 0);
        control = new PointF(0, 0);
        process1 = new PointF(0, 0);
        process2 = new PointF(0, 0);

        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        //初始化数据点和控制点的位置
        start.x = centerX - 200;
        start.y = centerY;
        end.x = centerX + 200;
        end.y = centerY;
        control.x = centerX - 50;
        control.y = centerY - 300;
        x = start.x;
        y = start.y;
        mPath.moveTo(x, y);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {
        while (mIsDrawing) {
            draw();
            if (t <= 1) {
                t += 0.003;

                //辅助线坐标点
                process1.x = (1 - t) * start.x + t * control.x;
                process1.y = (1 - t) * start.y + t * control.y;
                process2.x = (1 - t) * control.x + t * end.x;
                process2.y = (1 - t) * control.y + t * end.y;

                //贝塞尔曲线通用函数
                x = (1 - t) * process1.x + t * process2.x;
                y = (1 - t) * process1.y + t * process2.y;

                //二阶贝塞尔曲线函数
//                x = (float) (Math.pow((1 - t), 2) * start.x + 2 * t * (1 - t) * control.x + Math.pow(t, 2) * end.x);
//                y = (float) (Math.pow((1 - t), 2) * start.y + 2 * t * (1 - t) * control.y + Math.pow(t, 2) * end.y);

                mPath.lineTo(x, y);
            } else {
                mIsDrawing = false;
            }

        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            //绘制数据点和控制点
            mCanvas.drawPoint(start.x, start.y, mPointPaint);
            mCanvas.drawPoint(control.x, control.y, mPointPaint);
            mCanvas.drawPoint(end.x, end.y, mPointPaint);
            //绘制数据点和控制点的连线
            mCanvas.drawLine(start.x, start.y, control.x, control.y, mLinePaint);
            mCanvas.drawLine(control.x, control.y, end.x, end.y, mLinePaint);
            //绘制辅助线和辅助点
            mCanvas.drawLine(process1.x, process1.y, process2.x, process2.y, mAssistLinePaint);
            mCanvas.drawPoint(process1.x,process1.y,mAssistPointPaint);
            mCanvas.drawPoint(process2.x,process2.y,mAssistPointPaint);
            //绘制二阶贝塞尔曲线的当前点
            mCanvas.drawPoint(x, y, mPointPaint);
            //绘制二阶贝塞尔曲线
            mCanvas.drawPath(mPath, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mCanvas) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
