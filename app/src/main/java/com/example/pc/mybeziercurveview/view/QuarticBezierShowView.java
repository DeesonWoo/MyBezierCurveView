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
 * 四阶
 * Created by Deeson on 2017/5/18.
 */

public class QuarticBezierShowView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //分别对应贝塞尔曲线、点、数据点和控制点之间的线、第一层辅助线、第一层辅助点、第二层辅助线、第二层辅助点、第三层辅助线、第三层辅助点
    private Paint mPaint, mPointPaint, mLinePaint, mAssistLine1Paint, mAssistPoint1Paint,mAssistLine2Paint,mAssistPoint2Paint, mAssistLine3Paint,mAssistPoint3Paint;
    //绘制贝塞尔曲线的path
    private Path mPath;
    //布局的中心点
    private int centerX, centerY;
    //分别对应四阶贝塞尔曲线的起点、终点、三个控制点
    private PointF start, end, control1, control2, control3;
    //第一层辅助线的4个端点（相当于动态的三阶贝塞尔曲线的起点，两个控制点，终点）
    private PointF process1, process2, process3, process4;
    //第二层辅助线的3个端点（相当于动态的二阶贝塞尔曲线的起点，控制点，终点）
    private PointF secondProcess1, secondProcess2, secondProcess3;
    //第三层辅助线的起点、终点
    private PointF thirdProcess1, thirdProcess2;

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    //子线程标志位
    private boolean mIsDrawing;

    float x = 0;//贝塞尔曲线的实时点x坐标
    float y = 0;//贝塞尔曲线的实时点y坐标
    float t = 0;//实施进度，0<=t<=1


    public QuarticBezierShowView(Context context) {
        super(context);
        init();
    }

    public QuarticBezierShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuarticBezierShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //贝塞尔曲线
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //点
        mPointPaint = new Paint();
        mPointPaint.setColor(Color.BLACK);
        mPointPaint.setStrokeWidth(10);
        mPointPaint.setStyle(Paint.Style.STROKE);

        //数据点和控制点的连线
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStyle(Paint.Style.STROKE);

        //第一层辅助线
        mAssistLine1Paint = new Paint();
        mAssistLine1Paint.setColor(Color.GREEN);
        mAssistLine1Paint.setStrokeWidth(4);
        mAssistLine1Paint.setStyle(Paint.Style.STROKE);
        //第一层辅助点
        mAssistPoint1Paint = new Paint();
        mAssistPoint1Paint.setColor(Color.GREEN);
        mAssistPoint1Paint.setStrokeWidth(10);
        mAssistPoint1Paint.setStyle(Paint.Style.FILL);

        //第二层辅助线
        mAssistLine2Paint = new Paint();
        mAssistLine2Paint.setColor(Color.BLUE);
        mAssistLine2Paint.setStrokeWidth(4);
        mAssistLine2Paint.setStyle(Paint.Style.STROKE);
        //第二层辅助点
        mAssistPoint2Paint = new Paint();
        mAssistPoint2Paint.setColor(Color.BLUE);
        mAssistPoint2Paint.setStrokeWidth(10);
        mAssistPoint2Paint.setStyle(Paint.Style.FILL);

        //第三层辅助线
        mAssistLine3Paint = new Paint();
        mAssistLine3Paint.setColor(Color.MAGENTA);
        mAssistLine3Paint.setStrokeWidth(4);
        mAssistLine3Paint.setStyle(Paint.Style.STROKE);
        //第三层辅助点
        mAssistPoint3Paint = new Paint();
        mAssistPoint3Paint.setColor(Color.MAGENTA);
        mAssistPoint3Paint.setStrokeWidth(10);
        mAssistPoint3Paint.setStyle(Paint.Style.FILL);

        //四阶贝塞尔曲线的起点终点
        start = new PointF(0, 0);
        end = new PointF(0, 0);
        //四阶贝塞尔曲线的三个控制点
        control1 = new PointF(0, 0);
        control2 = new PointF(0, 0);
        control3 = new PointF(0, 0);
        //第一层辅助线的四个端点
        process1 = new PointF(0, 0);
        process2 = new PointF(0, 0);
        process3 = new PointF(0, 0);
        process4 = new PointF(0, 0);
        //第二层辅助线的三个端点
        secondProcess1 = new PointF(0, 0);
        secondProcess2 = new PointF(0, 0);
        secondProcess3 = new PointF(0, 0);
        //第三层辅助线的两个端点
        thirdProcess1 = new PointF(0, 0);
        thirdProcess2 = new PointF(0, 0);

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
        start.x = centerX - 300;
        start.y = centerY;
        end.x = centerX + 400;
        end.y = centerY - 200;
        control1.x = centerX - 150;
        control1.y = centerY - 300;
        control2.x = centerX + 170;
        control2.y = centerY - 340;
        control3.x = centerX + 200;
        control3.y = centerY + 100;

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

                //重点在这里

                //第一层辅助线坐标点
                process1.x = (1 - t) * start.x + t * control1.x;
                process1.y = (1 - t) * start.y + t * control1.y;
                process2.x = (1 - t) * control1.x + t * control2.x;
                process2.y = (1 - t) * control1.y + t * control2.y;
                process3.x = (1 - t) * control2.x + t * control3.x;
                process3.y = (1 - t) * control2.y + t * control3.y;
                process4.x = (1 - t) * control3.x + t * end.x;
                process4.y = (1 - t) * control3.y + t * end.y;
                //第二层辅助线坐标点
                secondProcess1.x = (1 - t) * process1.x + t * process2.x;
                secondProcess1.y = (1 - t) * process1.y + t * process2.y;
                secondProcess2.x = (1 - t) * process2.x + t * process3.x;
                secondProcess2.y = (1 - t) * process2.y + t * process3.y;
                secondProcess3.x = (1 - t) * process3.x + t * process4.x;
                secondProcess3.y = (1 - t) * process3.y + t * process4.y;
                //第三层辅助线坐标点
                thirdProcess1.x = (1 - t) * secondProcess1.x + t * secondProcess2.x;
                thirdProcess1.y = (1 - t) * secondProcess1.y + t * secondProcess2.y;
                thirdProcess2.x = (1 - t) * secondProcess2.x + t * secondProcess3.x;
                thirdProcess2.y = (1 - t) * secondProcess2.y + t * secondProcess3.y;


                //贝塞尔曲线通用公式
                x = (1 - t) * thirdProcess1.x + t * thirdProcess2.x;
                y = (1 - t) * thirdProcess1.y + t * thirdProcess2.y;

                //四阶贝塞尔曲线函数
//                x = (float) (Math.pow((1 - t), 4) * start.x + 4 * t * Math.pow((1 - t), 3) * control1.x +
//                        6 * Math.pow(t, 2) * Math.pow((1 - t), 2) * control2.x + 4 * Math.pow(t, 3) * (1 - t) * control3.x +
//                        Math.pow(t, 4) * end.x);
//
//                y = (float) (Math.pow((1 - t), 4) * start.y + 4 * t * Math.pow((1 - t), 3) * control1.y +
//                        6 * Math.pow(t, 2) * Math.pow((1 - t), 2) * control2.y + 4 * Math.pow(t, 3) * (1 - t) * control3.y +
//                        Math.pow(t, 4) * end.y);


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
            mCanvas.drawPoint(control1.x, control1.y, mPointPaint);
            mCanvas.drawPoint(control2.x, control2.y, mPointPaint);
            mCanvas.drawPoint(control3.x, control3.y, mPointPaint);
            mCanvas.drawPoint(end.x, end.y, mPointPaint);
            //绘制数据点和控制点的连线
            mCanvas.drawLine(start.x, start.y, control1.x, control1.y, mLinePaint);
            mCanvas.drawLine(control1.x, control1.y, control2.x, control2.y, mLinePaint);
            mCanvas.drawLine(control2.x, control2.y, control3.x, control3.y, mLinePaint);
            mCanvas.drawLine(control3.x, control3.y, end.x, end.y, mLinePaint);
            //绘制第一层辅助线和点
            mCanvas.drawLine(process1.x, process1.y, process2.x, process2.y, mAssistLine1Paint);
            mCanvas.drawLine(process2.x, process2.y, process3.x, process3.y, mAssistLine1Paint);
            mCanvas.drawLine(process3.x, process3.y, process4.x, process4.y, mAssistLine1Paint);
            mCanvas.drawPoint(process1.x,process1.y,mAssistPoint1Paint);
            mCanvas.drawPoint(process2.x,process2.y,mAssistPoint1Paint);
            mCanvas.drawPoint(process3.x,process3.y,mAssistPoint1Paint);
            mCanvas.drawPoint(process4.x,process4.y,mAssistPoint1Paint);
            //绘制第二层辅助线和点
            mCanvas.drawLine(secondProcess1.x, secondProcess1.y, secondProcess2.x, secondProcess2.y, mAssistLine2Paint);
            mCanvas.drawLine(secondProcess2.x, secondProcess2.y, secondProcess3.x, secondProcess3.y, mAssistLine2Paint);
            mCanvas.drawPoint(secondProcess1.x,secondProcess1.y,mAssistPoint2Paint);
            mCanvas.drawPoint(secondProcess2.x,secondProcess2.y,mAssistPoint2Paint);
            mCanvas.drawPoint(secondProcess3.x,secondProcess3.y,mAssistPoint2Paint);
            //绘制第三层辅助线和点
            mCanvas.drawLine(thirdProcess1.x, thirdProcess1.y, thirdProcess2.x, thirdProcess2.y, mAssistLine3Paint);
            mCanvas.drawPoint(thirdProcess1.x,thirdProcess1.y,mAssistPoint3Paint);
            mCanvas.drawPoint(thirdProcess2.x,thirdProcess2.y,mAssistPoint3Paint);
            //绘制四阶贝塞尔曲线的当前点
            mCanvas.drawPoint(x, y, mPointPaint);
            //绘制四阶贝塞尔曲线
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
