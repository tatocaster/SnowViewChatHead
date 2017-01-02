package me.tatocaster.snowview.SnowView;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

/**
 * Created by tatocaster on 1/1/17.
 */

class SnowFlake {
    private static final String TAG = "SnowFlake";
    private static final int FLAKE_MAX_RADIUS = 6;
    // control this, will start from upper frame
    private static final int DIVISOR_CONTROL_FLAKE_START = 8;

    // wind direction and strength
    private static final int WIND = -10;

    private final Random mRandom;
    private double x;
    private double y;
    private Paint mPaint;
    private int width;
    private int height;

    SnowFlake(Point position, Paint paint, int width, int height) {
        this.x = position.x;
        this.y = position.y;
        this.mPaint = paint;
        this.width = width;
        this.height = height;
        mRandom = new Random();
    }

    private void move() {
//        x += WIND;
        y += 15;
        if (needsReset()) {
            reset();
        }
    }

    private void reset() {
        x = mRandom.nextInt(width);
        y = mRandom.nextInt(height / DIVISOR_CONTROL_FLAKE_START);
    }

    private boolean needsReset() {
        return y >= height;
    }

    void draw(Canvas canvas) {
        move();
        canvas.drawCircle((float) x, (float) y, mRandom.nextInt(FLAKE_MAX_RADIUS), mPaint);
    }
}
