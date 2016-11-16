package yangxixi.processsuccessviewdemo.ui.views;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yangxixi.processsuccessviewdemo.ui.R;

/**
 * Created by yangxixi on 16/11/14.
 *
 * 高仿小米"安全中心"中垃圾清理成功后的显示界面，动画进度条成功的过度界面。
 */

public class ProcessSuccessView extends View implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "ProcessSuccessView";
    private static final long ARC_CREATE_TIME = 1L;                                 //圆弧边的创建的时间
    private static final long ARC_CREATE_ANGLE = 8;                                 //圆弧边的创建的时间

    private int isShowTriAngle;                                                     //是否展示圆弧旁边转动的三角形
    private float rightStrokeWidth;                                                 //钩子边的宽度
    private float arcStrokeWidth;                                                   //圆弧边的宽度
    private int rightColor;                                                         //钩子的颜色
    private int arcColor;                                                           //圆弧的颜色
    private float arcRoundWidthScale = 0.5f;                                        //圆弧的直径所占屏幕的比例
    private float arcWidth;                                                         //圆弧的直径
    private boolean isDraw = true;                                                  //绘制时候变化的角度
    private boolean isDrawing = false;                                              //是否正在绘制
    private float arcAngle = 0;                                                     //旋转的角度
    private List<Point> valuePoints;                                                //随机产生的所有的坐标点
    private List<Integer> triAngle = new ArrayList<>();                             //三角形的随机宽度

    private Paint mArcPaint;                                                        //画圆弧的画笔
    private Paint mRightPaint;                                                      //画钩钩的画笔
    private Paint mTriAnglePaint;                                                   //画三角形的画笔

    private IAnimotionListener listener;

    public ProcessSuccessView(Context context) {
        this(context, null);
    }

    public ProcessSuccessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProcessSuccessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);  //没有视图绘制的时候，onDraw方法可能不会调用，此项设置，可以保证onDraw方法调用

        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProcessSuccessView);
            arcRoundWidthScale = typedArray.getFloat(R.styleable.ProcessSuccessView_scale, 0.5f);
            if(arcRoundWidthScale < 0) {
                arcRoundWidthScale = 0.0f;
            }
            if(arcRoundWidthScale >= 1) {
                arcRoundWidthScale = 1.0f;
            }
            arcColor = typedArray.getColor(R.styleable.ProcessSuccessView_arcColor, Color.parseColor("#63D3D0"));
            rightColor = typedArray.getColor(R.styleable.ProcessSuccessView_rightColor, Color.parseColor("#B9EBE9"));
            arcStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ProcessSuccessView_arcWidth, 8);
            rightStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ProcessSuccessView_rightWidth, 32);
            isShowTriAngle = typedArray.getInt(R.styleable.ProcessSuccessView_isShowTriAngle, 1);
        } finally {
            if(typedArray != null) {
                typedArray.recycle();
            }
        }

        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initParam();
        //宽度
        int specModeWidth = MeasureSpec.getMode(widthMeasureSpec);//得到模式
        int specSizeWidth = MeasureSpec.getSize(widthMeasureSpec);//得到大小
        //高度
        int specModeHeight = MeasureSpec.getMode(heightMeasureSpec);//得到模式
        int specSizeHeight = MeasureSpec.getSize(heightMeasureSpec);//得到大小
        //如果两个都是确定的高度，由最短的那个长度确定圆的直径
        if (specModeWidth == MeasureSpec.EXACTLY && specModeHeight == MeasureSpec.EXACTLY) {
            int size = ((specSizeWidth >= specSizeHeight) ? specSizeHeight : specSizeWidth);
            arcWidth = size * arcRoundWidthScale;
            setMeasuredDimension(size, size);
        } else {
            setMeasuredDimension(specSizeWidth, specSizeHeight);
        }
    }

    /**
     * 初始化基本参数
     */
    private void initParam() {
        arcWidth = getWidth() * arcRoundWidthScale;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(arcColor);
        mArcPaint.setStrokeWidth(arcStrokeWidth);

        mRightPaint = new Paint();
        mRightPaint.setAntiAlias(true);
        mRightPaint.setStyle(Paint.Style.STROKE);
        mRightPaint.setColor(rightColor);
        mRightPaint.setStrokeWidth(rightStrokeWidth);

        mTriAnglePaint = new Paint();
        mTriAnglePaint.setAntiAlias(true);
        mTriAnglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTriAnglePaint.setColor(Color.parseColor("#14FFFFFF"));
        mTriAnglePaint.setStrokeWidth(rightStrokeWidth);
    }

    /**
     * 画圆弧
     */
    private void drawArc(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.left = (getWidth() - arcWidth) / 2;
        rectF.top = (getHeight() - arcWidth) / 2;
        rectF.right = (getWidth() - arcWidth) / 2 + arcWidth;
        rectF.bottom = (getHeight() - arcWidth) / 2 + arcWidth;
        if (isDraw) {
            //未运行的的时候可以执行一次
            if (!isDrawing) {
                startBezierAnimotion();
            }

            isDrawing = true;
            canvas.drawArc(rectF, -90, arcAngle, false, mArcPaint);
            arcAngle += ARC_CREATE_ANGLE;

            if (arcAngle > 330) {
                drawRight(canvas);
            }

            postInvalidateDelayed(ARC_CREATE_TIME); //每隔一段时间开始绘制

            if (arcAngle >= 360) {
                isDraw = false;
            }

            drawTriAngle(canvas);
        } else {
            canvas.drawArc(rectF, -90, 360, false, mArcPaint);
            drawRight(canvas);
            isDrawing = false;
            if(listener != null) {
                listener.onAnimotionFinished();
            }
        }
    }

    /**
     * 画正确的钩子
     *
     * @param canvas\
     */
    private void drawRight(Canvas canvas) {
        Path path = new Path();
        path.moveTo((getWidth() - arcWidth) / 2 + arcWidth / 4 + arcWidth / 16, (getHeight() - arcWidth) / 2 + arcWidth / 2);
        path.lineTo((getWidth() - arcWidth) / 2 + arcWidth / 4 + arcWidth / 8 + arcWidth / 16, (getHeight() - arcWidth) / 2 + (arcWidth / 4) * 3 - arcWidth / 8);
        path.lineTo((getWidth() - arcWidth) / 2 + arcWidth / 2 + arcWidth / 4 - arcWidth / 8 + arcWidth / 16, (getHeight() - arcWidth) / 2 + arcWidth / 4 + arcWidth / 8);
        canvas.drawPath(path, mRightPaint);
    }

    /**
     * 画各个三角形碎片
     *
     * @param canvas
     */
    private void drawTriAngle(Canvas canvas) {
        if (valuePoints != null && valuePoints.size() > 0) {
            for (int i = 0; i < valuePoints.size(); i++) {
                Point mPoint = valuePoints.get(i);
                int offset = triAngle.get(i);   //获取不规则的三角的区间值
                int x = mPoint.x;
                int circleY = mPoint.y;
                Path path = new Path();
                path.moveTo(x, circleY);
                path.lineTo(x - offset, circleY + offset);
                path.lineTo(x + offset, circleY + offset);
                path.close();
                canvas.drawPath(path, mTriAnglePaint);
            }
        }
    }

    /**
     * 开始运行，展示动画
     */
    public void starRun() {
        if (!isDrawing) {
            arcAngle = 0;
            isDraw = true;
            startBezierAnimotion();
            invalidate();
        } else {
            Log.i(TAG, "loading is going on");
        }
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        List<Point> valuePoints = (List<Point>) animation.getAnimatedValue();
        this.valuePoints = valuePoints;
        invalidate();
    }

    /**
     * 开始运动三角形
     */
    private void startBezierAnimotion() {
        if(isShowTriAngle == 1) {
            float circleX = (getWidth() - arcWidth) / 2 + arcWidth / 2;
            float circleY = (getHeight() - arcWidth) / 2 + arcWidth / 2;
            float x = ((getWidth() - arcWidth) / 2) / 2;
            Point controllPoint = new Point((int) circleX, (int) circleY);
            BezierEvaluators bezierEvaluator = new BezierEvaluators(controllPoint);
            //初始化开始坐标和结束坐标，在这里这个坐标并使用，只是为了能够得属性动画产生的变化值
            List<Point> startPoint = new ArrayList<>();
            List<Point> endPoint = new ArrayList<>();
            triAngle.clear();
            for (int i = 0; i < 10; i++) {
                startPoint.add(new Point(i, i));    //随意生成
                endPoint.add(new Point(i, i));      //随意生成

                //半径之外的范围 150 - 200之间
                double r = Math.random() * 30 + 20;
                triAngle.add((int) r);
            }
            ValueAnimator anim = ValueAnimator.ofObject(bezierEvaluator, startPoint, endPoint);
            anim.addUpdateListener(this);
            anim.setDuration(1500);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());   //动画运动的速率
            anim.start();
        }
    }

    public IAnimotionListener getListener() {
        return listener;
    }

    public void setListener(IAnimotionListener listener) {
        this.listener = listener;
    }

    public class BezierEvaluators implements TypeEvaluator<List<Point>> {

        private Point controllPoint;

        public BezierEvaluators(Point controllPoint) {
            this.controllPoint = controllPoint;
        }

        @Override
        public List<Point> evaluate(float t, List<Point> startValue, List<Point> endValue) {
            List<Point> list = new ArrayList<>();
            if (startValue.size() > 0 && endValue.size() > 0 && startValue.size() == endValue.size()) {
                for (int i = 0; i < startValue.size(); i++) {
                    //增加角度，让每个碎片三角形所处位置不一样
                    double sum = t + ((double) i / (double) 10);
                    if (sum >= 1) {
                        sum = sum - 1;
                    }
                    // 200 - (80 * t) 旋转越来越靠近园
                    double x = controllPoint.x + (arcWidth / 2 + 200 - (80 * t)) * Math.cos(360 * sum * Math.PI / 180);
                    double y = controllPoint.y + (arcWidth / 2 + 200 - (80 * t)) * Math.sin(360 * sum * Math.PI / 180);
                    list.add(new Point((int) x, (int) y));
                }
            }
            return list;
        }
    }

    /**
     * 坐标点的位置
     */
    public class Point implements Serializable {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(Point src) {
            this.x = src.x;
            this.y = src.y;
        }

        public void set(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public interface IAnimotionListener {
        /**
         * 动画加载完毕
         */
        void onAnimotionFinished();
    }
}
