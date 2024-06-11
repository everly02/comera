package com.example.comera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ShutterButton extends View {

    private Paint outerCirclePaint;
    private Paint innerCirclePaint;
    private boolean isRecording = false;

    public ShutterButton(Context context) {
        super(context);
        init();
    }

    public ShutterButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShutterButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(10);
        outerCirclePaint.setColor(Color.WHITE);

        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setStyle(Paint.Style.FILL);
        innerCirclePaint.setColor(Color.RED);
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        // Draw outer circle (ring)
        canvas.drawCircle(width / 2, height / 2, radius - 10, outerCirclePaint);

        // Draw inner circle
        if (isRecording) {
            innerCirclePaint.setColor(Color.RED);
        } else {
            innerCirclePaint.setColor(Color.WHITE);
        }
        canvas.drawCircle(width / 2, height / 2, radius - 20, innerCirclePaint);
    }
}