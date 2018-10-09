package com.acsoftware.android.varilightcalibrationwheelconcept;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class SuplaRangeCalibrationWheel extends View {

    private final int TOUCHED_NONE = 0;
    private final int TOUCHED_LEFT = 1;
    private final int TOUCHED_RIGHT = 2;

    private Paint paint;
    private RectF rectF;
    private float borderLineWidth;
    private float wheelCenterX;
    private float wheelCenterY;
    private float wheelRadius;
    private float wheelWidth;
    private int touched = TOUCHED_NONE;
    private PointF btnLeftCenter;
    private PointF btnRightCenter;
    private float halfBtnSize;
    private double btnRad;
    private double lastTouchedDegree;

    private double range = 1000;
    private double numerOfTurns = 4;
    private double leftEdge = 0;
    private double rightEdge = 0;

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        rectF = new RectF();
        btnLeftCenter = null;
        btnRightCenter = null;
        btnRad = 0;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        borderLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1.5F, metrics);
    }

    public SuplaRangeCalibrationWheel(Context context) {
        super(context);
        init();
    }

    public SuplaRangeCalibrationWheel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuplaRangeCalibrationWheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SuplaRangeCalibrationWheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setRange(double range) {
        this.range = range;
        invalidate();
    }

    public double getRange() {
        return range;
    }

    public void setNumerOfTurns(double numerOfTurns) {
        this.numerOfTurns = numerOfTurns;
        invalidate();
    }

    public double getNumerOfTurns() {
        return numerOfTurns;
    }

    public void setLeftEdge(double leftEdge) {
        this.leftEdge = leftEdge;
        invalidate();
    }

    public double getLeftEdge() {
        return leftEdge;
    }

    public void setRightEdge(double rightEdge) {
        this.rightEdge = rightEdge;
        invalidate();
    }

    public double getRightEdge() {
        return rightEdge;
    }

    protected PointF drawButton(Canvas canvas, double rad) {

        float btnSize = wheelWidth+4*borderLineWidth;
        halfBtnSize = btnSize/2;
        float x = wheelCenterX+wheelRadius - borderLineWidth;

        PointF result = new PointF();
        result.x = (float)(Math.cos(rad)*wheelRadius)+wheelCenterX;
        result.y = (float)(Math.sin(rad)*wheelRadius)+wheelCenterY;

        canvas.save();
        canvas.rotate((float)Math.toDegrees(rad), wheelCenterX, wheelCenterY);

        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        rectF.set(x-halfBtnSize, wheelCenterY-halfBtnSize, x+halfBtnSize, wheelCenterY+halfBtnSize);

        canvas.drawRoundRect(
                rectF, // rect
                15, // rx
                15, // ry
                paint // Paint
        );

        //for(int a=0;a<3;a++) {
        //    rectF.set(x-halfBtnSize, wheelCenterY-halfBtnSize, x+halfBtnSize, wheelCenterY+halfBtnSize);
        //}

        canvas.restore();

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        wheelCenterX = this.getWidth() / 2;
        wheelCenterY = this.getHeight() / 2;

        wheelRadius = wheelCenterX > wheelCenterY ? wheelCenterY : wheelCenterX;
        wheelRadius *= 0.7;
        wheelWidth = wheelRadius * 0.25F;

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(wheelWidth);
        rectF.set(wheelCenterX-wheelRadius, wheelCenterY-wheelRadius, wheelCenterX+wheelRadius, wheelCenterY+wheelRadius);
        canvas.drawOval(rectF, paint);

        paint.setStrokeWidth(wheelWidth-borderLineWidth*2);
        paint.setColor(Color.RED);
        canvas.drawOval(rectF, paint);

        if (touched == TOUCHED_NONE) {
            btnRightCenter = drawButton(canvas, 0);
            btnLeftCenter = drawButton(canvas, (float)Math.toRadians(180));
        } else {
            if (touched == TOUCHED_RIGHT) {
                btnRightCenter = drawButton(canvas, btnRad);
            } else if (touched == TOUCHED_LEFT) {
                btnLeftCenter = drawButton(canvas, btnRad);
            }
        }

    }

    private boolean btnTouched(PointF btnCenter, PointF touchPoint) {
        if (btnCenter != null) {
            float touchRadius = (float)Math.sqrt(Math.pow(touchPoint.x - btnCenter.x, 2)
                    + Math.pow(touchPoint.y - btnCenter.y, 2));
            return touchRadius <= halfBtnSize*1.1;
        }
        return false;
    }

    private double touchPointToRadian(PointF touchPoint) {
        return Math.atan2(touchPoint.y-wheelCenterY,
                touchPoint.x-wheelCenterX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        PointF touchPoint = new PointF();

        touchPoint.x = event.getX();
        touchPoint.y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                touched = TOUCHED_NONE;
                btnRad = 0;
                invalidate();
                break;

            case MotionEvent.ACTION_DOWN:
                if (touched==TOUCHED_NONE) {
                    if (btnTouched(btnLeftCenter, touchPoint)) {
                        touched = TOUCHED_LEFT;
                    } else if (btnTouched(btnRightCenter, touchPoint)) {
                        touched = TOUCHED_RIGHT;
                    }

                    if (touched!=TOUCHED_NONE) {
                        lastTouchedDegree = Math.toDegrees(touchPointToRadian(touchPoint));
                        invalidate();
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touched!=TOUCHED_NONE) {
                    btnRad = (float) Math.atan2(touchPoint.y-wheelCenterY,
                            touchPoint.x-wheelCenterX);

                    btnRad = touchPointToRadian(touchPoint);
                    double touchedDegree = Math.toDegrees(btnRad);

                    double diff = touchedDegree-lastTouchedDegree;
                    if (Math.abs(diff) > 100) {
                        diff = 360 - Math.abs(lastTouchedDegree) - Math.abs(touchedDegree);
                        if (touchedDegree > 0) {
                            diff*=-1;
                        }
                    }

                    if (Math.abs(diff) <= 20) {
                        diff = (diff*100.0/360.0)*range/100/numerOfTurns;
                        if (touched==TOUCHED_LEFT) {
                            leftEdge+=diff;
                            if (leftEdge > rightEdge) {
                                leftEdge = rightEdge;
                            } else if (leftEdge > range) {
                                leftEdge = range;
                            } else if (leftEdge < 0) {
                                leftEdge = 0;
                            }
                        } else {
                            rightEdge+=diff;
                            if (rightEdge < leftEdge) {
                                rightEdge = leftEdge;
                            } else if (rightEdge > range) {
                                rightEdge = range;
                            } else if (rightEdge < 0) {
                                rightEdge = 0;
                            }
                        }

                        Log.d("Value", "Left: "+Double.toString(leftEdge)+" Right: " + Double.toString(rightEdge));
                    }


                    lastTouchedDegree = touchedDegree;
                    invalidate();
                    return true;
                }
                break;
        }


        return super.onTouchEvent(event);
    }
}
