package com.example.comera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.face.Face;

import java.util.List;

public class FaceDetectionBoxView extends View {

    private List<Face> faces;
    private Paint paint;

    public FaceDetectionBoxView(Context context) {
        super(context);
        init();
    }

    public FaceDetectionBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceDetectionBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8.0f);
    }

    public void setFaces(List<Face> faces) {
        this.faces = faces;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faces != null) {
            for (Face face : faces) {
                Rect bounds = face.getBoundingBox();
                canvas.drawRect(bounds, paint);
            }
        }
    }
}