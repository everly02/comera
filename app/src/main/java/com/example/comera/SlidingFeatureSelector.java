package com.example.comera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class SlidingFeatureSelector extends View {

    private static final int FEATURE_COUNT = 5;  // 增加 Filter 功能
    private static final String[] FEATURES = {"Camera", "Video", "Portrait", "Night", "Filter"};
    private int selectedFeatureIndex = 0;
    private Paint paint;
    private Rect rect;
    private float initialX;
    private float offsetX;

    private OnFeatureSelectedListener listener;

    public SlidingFeatureSelector(Context context) {
        super(context);
        init();
    }

    public SlidingFeatureSelector(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingFeatureSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        paint.setColor(Color.DKGRAY);
        canvas.drawRect(0, 0, width, height, paint);

        for (int i = 0; i < FEATURE_COUNT; i++) {
            float x = width / 2 + (i - selectedFeatureIndex) * width + offsetX;
            float y = height / 2;

            String feature = FEATURES[i];
            paint.getTextBounds(feature, 0, feature.length(), rect);

            float scale = 1.0f - 0.5f * Math.abs((x - width / 2) / width);
            float alpha = 255 * (1.0f - Math.abs((x - width / 2) / width));

            paint.setTextSize(60 * scale);
            paint.setAlpha((int) alpha);
            paint.setColor(Color.WHITE);

            canvas.drawText(feature, x, y - rect.centerY(), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                offsetX = event.getX() - initialX;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                float deltaX = event.getX() - initialX;
                if (Math.abs(deltaX) > getWidth() / 3) {
                    if (deltaX > 0) {
                        selectedFeatureIndex = Math.max(selectedFeatureIndex - 1, 0);
                    } else {
                        selectedFeatureIndex = Math.min(selectedFeatureIndex + 1, FEATURE_COUNT - 1);
                    }
                    if (listener != null) {
                        listener.onFeatureSelected(FEATURES[selectedFeatureIndex]);
                    }
                }
                offsetX = 0;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnFeatureSelectedListener(OnFeatureSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnFeatureSelectedListener {
        void onFeatureSelected(String feature);
    }
}
