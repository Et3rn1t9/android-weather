package com.example.weather.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;


import androidx.annotation.Nullable;

import com.example.weather.util.DensityUtils;
import com.example.weather.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class WeatherView extends View {
    ObjectAnimator animator = ObjectAnimator.ofInt(this, "dd", 0, 450);

    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("Recycle")
    public WeatherView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WeatherView);
        resourceId = ta.getResourceId(R.styleable.WeatherView_msrc, 0);
        bitmapBG = BitmapFactory.decodeResource(getResources(), resourceId);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setColor(color);
        paintLine.setStrokeWidth(DensityUtils.dipTopx(context, 1.6f));
        int lineLong = DensityUtils.dipTopx(context, 10);
        longY = (float) (lineLong * 0.94);
        longX = (float) (longY * 0.364);
    }

    private String cityName = "余杭";
    private String temperature = "25";


    public enum Type {
        sunday, night, raining, snow, rainingNight, snowNight;
    }

    int measuredHeight, measuredWidth;
    int resourceId;//默认显示图片
    Bitmap bitmapBG = null;//背景图片
    Bitmap bitmapPre = null;//前景图片
    Path path;
    Type myType = null;

    int color;
    /**
     * 下落时Y轴向量的值
     */
    float spe;
    boolean addAble = true;
    float longX, longY;
    /**
     * 间隔增速
     */
    float interval = 2;

    Rect rectBG, rectPre;
    Paint paint;
    Paint paintLine;
    List<WeatherBean> pointList;
    List<WeatherBean> liuxingList;
    int dd = 0;
    int lx = 0;
    float sun = 0;//太阳位置
    int sunX = 0;
    int sunPath = 0;
    float sunSize = 0;//太阳Bitmap的大小（边长）//可在设置数据时初始化


    {
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        pointList = new ArrayList<>();
        liuxingList = new ArrayList<>();
        color = Color.parseColor("#40ffffff");
    }

    boolean isAttached = false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
        if (animator != null) {
            animator.start();

        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.end();
        }

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
        spe = measuredHeight / 50f;//高度分为50份  用作下雨 下雪 下冰雹
        rectBG = new Rect(0, 0, measuredWidth, measuredHeight);//绘制背景的rect
        rectPre = new Rect(0, (int) (measuredHeight * 0.6f), measuredWidth, measuredHeight);//绘制前景的rect
        path = new Path();
        if (measuredWidth > 0 && measuredHeight > 0) {//圆角
            path.moveTo(18, 0);
            path.lineTo(measuredWidth - 18, 0);
            path.quadTo(measuredWidth, 0, measuredWidth, 18);
            path.lineTo(measuredWidth, measuredHeight - 18);
            path.quadTo(measuredWidth, measuredHeight, measuredWidth - 18, measuredHeight);
            path.lineTo(18, measuredHeight);
            path.quadTo(0, measuredHeight, 0, measuredHeight - 18);
            path.lineTo(0, 18);
            path.quadTo(0, 0, 18, 0);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.clipPath(path);
        if (myType == null) {
            return;
        }
        super.draw(canvas);
        switch (myType) {
            case raining://！完成
                initPoint();
                drawBg(canvas);
                drawRain(canvas);
                break;
            case rainingNight://！完成
                initPoint();
                drawBg(canvas);
                drawPre(canvas);
                drawRain(canvas);
                break;
            case snow://！完成
                initSnowPoint();
                drawBg(canvas);
                drawSnow(canvas);
                break;
            case snowNight://！完成
                initSnowPoint();
                drawBg(canvas);
                drawPre(canvas);
                drawSnow(canvas);
                break;
            case night://！完成
                drawBg(canvas);
                drawLX(canvas);
                drawPre(canvas);
                break;
            case sunday:
                if (sunPath <= 0) {
                    meaSunDate();
                }
                drawBg(canvas);
                drawSun(canvas);
                drawPre(canvas);
                if (!hasPlaySun) {
                    hasPlaySun = true;
                    startSunAnimation();
                }
                break;
        }
        // 绘制城市名字（左边）
        paint.setColor(Color.WHITE);
        paint.setTextSize(DensityUtils.dipTopx(getContext(), 22));
        canvas.drawText(cityName, 0, measuredHeight * 0.6f, paint);

        // 绘制今日温度（右边）
        paint.setTextSize(DensityUtils.dipTopx(getContext(), 22));
        float temperatureWidth = paint.measureText(temperature); // 获取温度文本的宽度
        float temperatureX = measuredWidth - temperatureWidth; // 计算温度文本的绘制位置
        canvas.drawText(temperature, temperatureX, measuredHeight * 0.6f, paint);

    }

    private void drawSun(Canvas canvas) {
        float half = sunSize / 2;//太阳图片的一半长度
        float lastheight = measuredHeight - half;//总高度减去图片一半，即太阳中心最终所在的位置
        //左边距
        float left = sunX + sunPath * sun;//sunX太阳初始位置，这里是控件长度的1/3，sunPath太阳起点到终点的总长度
        //顶部边距
        float top = lastheight * (1 - sun);

        //计算绘制太阳的矩形
        RectF rectF = new RectF(left, top, left + sunSize, top + sunSize);
        //在计算和的矩形绘制太阳
        canvas.drawBitmap(taiyangBitmap, null, rectF, paint);
    }

    //绘制流星
    private void drawLX(Canvas canvas) {
        if (lxBitmap != null) {
            for (int i = 0; i < liuxingList.size(); i++) {
                WeatherBean weatherBean = liuxingList.get(i);
                Rect rect = new Rect(weatherBean.x - lx - 48, lx - 55, weatherBean.x - lx, lx);
                canvas.drawBitmap(lxBitmap, null, rect, paint);
            }

        }

    }

    private void drawBg(Canvas canvas) {
        if (bitmapBG != null) {
            canvas.drawBitmap(bitmapBG, null, rectBG, paint);
        }
    }

    private void drawPre(Canvas canvas) {
        if (bitmapPre != null) {
            canvas.drawBitmap(bitmapPre, null, rectPre, paint);
        }

    }

    private void drawRain(Canvas canvas) {
        for (int i = 0; i < pointList.size(); i++) {
            WeatherBean weatherBean = pointList.get(i);

            float startY = spe * weatherBean.spex;
            float startX = weatherBean.x - startY * 0.364f;
            //经三角函数计算得出
            canvas.drawLine(startX, startY, startX - longX, startY + longY, paintLine);
        }
    }


    private void drawSnow(Canvas canvas) {
        for (int i = 0; i < pointList.size(); i++) {
            WeatherBean weatherBean = pointList.get(i);

            float startY = spe * weatherBean.spex;
            float startX = weatherBean.x - startY * 0.264f;
            RectF rectF = new RectF(startX, startY, startX + weatherBean.size, startY + weatherBean.size);
            canvas.drawBitmap(snowBitmap, null, rectF, paint);
        }
    }

    Bitmap snowBitmap = null;
    Bitmap lxBitmap = null;
    Bitmap taiyangBitmap = null;
    boolean hasPlaySun = true;

    /**
     * 设置天气的函数
     *
     * @param myType
     */
    public void setMyType(Type myType) {
        if (this.myType != myType) {
            if (animator == null) animator = ObjectAnimator.ofInt(this, "dd", 0, 450);
            if (animator.isRunning()) {
                animator.end();
                animator = null;
            }
        }
        this.myType = myType;
        switch (myType) {
            case raining:
                resourceId = R.mipmap.img_rainbg;
                interval = 2;
                startMyAnimation(false);
                break;
            case rainingNight:
                resourceId = R.mipmap.img_nightbg;
                interval = 2;
                bitmapPre = BitmapFactory.decodeResource(getResources(), R.mipmap.img_nightpre);
                startMyAnimation(false);
                break;
            case snow:
                resourceId = R.mipmap.img_snowbg;
                interval = 0.8f;
                snowBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_xue);
                break;
            case snowNight:
                resourceId = R.mipmap.img_nightbg;
                interval = 0.8f;
                bitmapPre = BitmapFactory.decodeResource(getResources(), R.mipmap.img_nightpre);
                snowBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_xue);
                startMyAnimation(false);
                break;
            case night:
                resourceId = R.mipmap.img_nightbg;
                bitmapPre = BitmapFactory.decodeResource(getResources(), R.mipmap.img_nightpre);
                lxBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_liuxing);
                liuxingList = new ArrayList<>();
                startMyAnimation(true);
                break;
            case sunday:
                resourceId = R.mipmap.img_sunbg;
                bitmapPre = BitmapFactory.decodeResource(getResources(), R.mipmap.img_sunpre);
                taiyangBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_taiyang);
                sunSize = DensityUtils.dipTopx(getContext(), 49);
                hasPlaySun = false;

                break;
        }
        bitmapBG = BitmapFactory.decodeResource(getResources(), resourceId);
        invalidate();
    }

    private void meaSunDate() {
        int i = measuredWidth / 3;
        sunX = (int) (i - 0.5 * sunSize);
        sunPath = (int) (i * 2 - 0.5 * sunSize - 30);
    }

    private void startMyAnimation(boolean isLiuXing) {
        if (!isLiuXing && animator != null && animator.isRunning()) {
            return;
        }
        if (!isLiuXing) {
            animator = ObjectAnimator.ofInt(this, "dd", 0, 450);
            animator.setDuration(5000);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
        } else {
            // 流星
            animator = ObjectAnimator.ofInt(this, "lx", 0, DensityUtils.dipTopx(getContext(), 65) + 55);
            animator.setDuration(1500);
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setStartDelay(2000);

        }
        if (isAttached) {
            animator.start();
        }

    }

    /**
     * 太阳升起动画
     */
    private void startSunAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "sun", 0, 1);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(2500);
        objectAnimator.start();
    }


    private void initPoint() {
        //循环，给所有spex加1
        //移除大于500的
        if (addAble) {
            //循环，给pointList补满30个
            int k = 30 - pointList.size();
            for (int i = 0; (i < k && i < 3); i++) {
                WeatherBean wb = new WeatherBean();
                wb.x = (int) (getRandomPoint() * measuredWidth);
                pointList.add(wb);
            }
        }
    }

    private void initSnowPoint() {
        //循环，给所有spex加1
        //移除大于500的
        if (addAble) {
            //循环，给pointList补满30个
            int k = 30 - pointList.size();
            for (int i = 0; (i < k && i < 1); i++) {
                WeatherBean wb = new WeatherBean();
                wb.x = (int) (getRandomPoint() * measuredWidth);
                wb.size = getRandomSize();
                pointList.add(wb);
            }
        }
    }

    /**
     * 获取一个随机点
     *
     * @return
     */
    private float getRandomPoint() {
        Random random = new Random();
        float v = random.nextFloat();
        return v;
    }

    /**
     * 获取一个随机尺寸 雪花
     *
     * @return
     */
    private int getRandomSize() {
        Random random = new Random();
        int v = random.nextInt(25);
        return v + 20;
    }

    /**
     * 获取一个随机点 流星
     *
     * @return
     */
    private int getRandomX() {
        Random random = new Random();
        int v = random.nextInt(measuredWidth == 0 ? 500 : (measuredWidth - 400));
        return v + 400;
    }

    public void setDd(int dd) {
        this.dd = dd;
        addAble = (dd % 5 == 0);
        int size = 0;
        //每次重新设置DD值 叠加spex的值，达到50移除
        //
        while (size < pointList.size()) {
            WeatherBean weatherBean = pointList.get(size);
            weatherBean.spex += interval;
            if (weatherBean.spex >= 50) {
                pointList.remove(size);
            } else {
                size++;
            }
        }
        invalidate();
    }

    /**
     * 流星动画设置函数，暂定同时只能存在一个流星
     */
    public void setLx(int lx) {
        this.lx = lx;
        if (liuxingList.size() == 0 || lx > measuredHeight + 51) {
            liuxingList.clear();
            if (getRandomPoint() > 0.98f) {//概率模拟不定时产生流星
                liuxingList.add(new WeatherBean(getRandomX()));
            }
        } else {
            invalidate();
        }

    }

    public void setSun(float sun) {
        this.sun = sun;
        invalidate();
    }

    private class WeatherBean {
        int x = 0;
        float spex = -longY;
        int size;

        public WeatherBean() {
        }

        public WeatherBean(int x) {
            this.x = x;
        }
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
        invalidate(); // 通知控件重绘
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature + "℃";
        invalidate(); // 通知控件重绘
    }


}
