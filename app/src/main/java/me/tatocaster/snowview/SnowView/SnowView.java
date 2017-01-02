package me.tatocaster.snowview.SnowView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import java.util.Random;

import me.tatocaster.snowview.R;
import me.tatocaster.snowview.Utils;

/**
 * Created by tatocaster on 1/1/17.
 */

public class SnowView extends View implements View.OnTouchListener {
    private static final String TAG = "SnowView";
    private static final int NUM_SNOWFLAKES = 70;
    private static final long DELAY = 100L;
    private Context mContext;
    private WindowManager mWindowManager;
    private FrameLayout mFrameLayout;
    private SnowFlake[] mSnowFlakes;
    private static final int CANVAS_WIDTH = 200;
    private static final int CANVAS_HEIGHT = 200;

    private Spring mSpring;
    private Spring mSpringForFrameXPosition;
    private Spring mSpringForFrameYPosition;
    // sprint transition
    private static double TENSION = 300;
    private static double DAMPER = 16; //friction


    private WindowManager.LayoutParams mWindowLayoutParams; // Window Manager Params
    private int initX, initY;
    private int initTouchX, initTouchY;
    private int mScreenWidth;
    private int mScreenHeight;

    private Runnable mRunnable = this::invalidate;

    public SnowView(Context context) {
        super(context);
        init(context);
    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        Random random = new Random();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        mSnowFlakes = new SnowFlake[NUM_SNOWFLAKES];
        for (int i = 0; i < NUM_SNOWFLAKES; i++) {
            Point position = new Point(random.nextInt(CANVAS_WIDTH), random.nextInt(CANVAS_HEIGHT));
            mSnowFlakes[i] = new SnowFlake(position, paint, CANVAS_WIDTH, CANVAS_HEIGHT);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        for (SnowFlake snowFlake : mSnowFlakes) {
            snowFlake.draw(canvas);
        }
        getHandler().postDelayed(mRunnable, DELAY);

    }

    public void addToWindowManager() {
        mWindowLayoutParams = new WindowManager.LayoutParams(
                CANVAS_WIDTH,
                CANVAS_HEIGHT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowLayoutParams.gravity = Gravity.LEFT;

        mFrameLayout = new FrameLayout(mContext);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        // set screen width and height variables
        mScreenHeight = Utils.getScreenHeight(mWindowManager);
        mScreenWidth = Utils.getScreenWidth(mWindowManager);


        mWindowManager.addView(mFrameLayout, mWindowLayoutParams);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Here is the place where you can inject whatever layout you want.
        layoutInflater.inflate(R.layout.overlay, mFrameLayout);

        addSpringSystem();
        springForXAxis();
        springForYAxis();

        mFrameLayout.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getRawX();
        int y = (int) motionEvent.getRawY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initX = mWindowLayoutParams.x;
                initY = mWindowLayoutParams.y;
                initTouchX = x;
                initTouchY = y;
                mSpring.setEndValue(1);
                return true;

            case MotionEvent.ACTION_UP:
                mSpring.setEndValue(0);
                return true;

            case MotionEvent.ACTION_MOVE:
                mWindowLayoutParams.x = initX + (x - initTouchX);
                mWindowLayoutParams.y = initY + (y - initTouchY);

                if (x > mScreenWidth / 2) {
                    mWindowLayoutParams.x = mScreenWidth - CANVAS_WIDTH;
                } else {
                    mWindowLayoutParams.x = 0;
                }
                mSpringForFrameXPosition.setEndValue(mWindowLayoutParams.x);
                mSpringForFrameYPosition.setEndValue(mWindowLayoutParams.y);

                // Invalidate layout
//                mWindowManager.updateViewLayout(mFrameLayout, mWindowLayoutParams);
                return true;
        }
        return false;
    }

    private void addSpringSystem() {
        SpringSystem springSystem = SpringSystem.create();
        mSpring = springSystem.createSpring();

        mSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
                mFrameLayout.setScaleX(value);
                mFrameLayout.setScaleY(value);
            }
        });
    }

    private void springForXAxis() {
        SpringSystem springSystem = SpringSystem.create();
        mSpringForFrameXPosition = springSystem.createSpring();

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mSpringForFrameXPosition.setSpringConfig(config);

        mSpringForFrameXPosition.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                mWindowLayoutParams.x = (int) value;
                mWindowManager.updateViewLayout(mFrameLayout, mWindowLayoutParams);
            }
        });
    }

    private void springForYAxis() {
        SpringSystem springSystem = SpringSystem.create();
        mSpringForFrameYPosition = springSystem.createSpring();

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mSpringForFrameYPosition.setSpringConfig(config);

        mSpringForFrameYPosition.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                mWindowLayoutParams.y = (int) value;
                mWindowManager.updateViewLayout(mFrameLayout, mWindowLayoutParams);
            }
        });
    }


    /**
     * Removes the view from window manager.
     */
    public void destroy() {
        mWindowManager.removeView(mFrameLayout);
    }
}
