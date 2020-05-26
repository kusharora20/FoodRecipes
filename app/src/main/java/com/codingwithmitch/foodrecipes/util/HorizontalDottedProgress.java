package com.codingwithmitch.foodrecipes.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.codingwithmitch.foodrecipes.R;

public final class HorizontalDottedProgress extends View {

    private static final String TAG = "HorizontalDottedProgres";
    private float dotRadius = 8;
    private float dotDiameter = 2.5f * dotRadius;
    private float bounceDotRadius = dotRadius * 2f;
    private final int startPosition = (int) bounceDotRadius;
    private int bounceDotPosition = 1;
    private float dotAmount = 10;
    private Paint mPaint;
    private int height, width;
    public static final int animationDuration = 80;

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see {@link View}(Context, AttributeSet, int)
     */
    public HorizontalDottedProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setStyle(Paint.Style.FILL);

    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < dotAmount; i++) {
            if (i == bounceDotPosition)
                canvas.drawCircle(
                        startPosition + dotDiameter * 2 * i,
                        bounceDotRadius,
                        bounceDotRadius,
                        mPaint);
            else
                canvas.drawCircle(
                        startPosition + dotDiameter * 2 * i,
                        bounceDotRadius,
                        dotRadius,
                        mPaint);
        }
    }

    /**
     * <p>
     * Measure the view and its content to determine the measured width and the
     * measured height. This method is invoked by {@link #measure(int, int)} and
     * should be overridden by subclasses to provide accurate and efficient
     * measurement of their contents.
     * </p>
     *
     * <p>
     * <strong>CONTRACT:</strong> When overriding this method, you
     * <em>must</em> call {@link #setMeasuredDimension(int, int)} to store the
     * measured width and height of this view. Failure to do so will trigger an
     * <code>IllegalStateException</code>, thrown by
     * {@link #measure(int, int)}. Calling the superclass'
     * {@link #onMeasure(int, int)} is a valid use.
     * </p>
     *
     * <p>
     * The base class implementation of measure defaults to the background size,
     * unless a larger size is allowed by the MeasureSpec. Subclasses should
     * override {@link #onMeasure(int, int)} to provide better measurements of
     * their content.
     * </p>
     *
     * <p>
     * If this method is overridden, it is the subclass's responsibility to make
     * sure the measured height and width are at least the view's minimum height
     * and width ({@link #getSuggestedMinimumHeight()} and
     * {@link #getSuggestedMinimumWidth()}).
     * </p>
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link MeasureSpec}.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link MeasureSpec}.
     * @see #getMeasuredWidth()
     * @see #getMeasuredHeight()
     * @see #setMeasuredDimension(int, int)
     * @see #getSuggestedMinimumHeight()
     * @see #getSuggestedMinimumWidth()
     * @see MeasureSpec#getMode(int)
     * @see MeasureSpec#getSize(int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = (int) bounceDotRadius * 2;
        float extraEdgeOnEachSide = 2 * (bounceDotRadius - dotRadius);
        width = (int) ((2 * extraEdgeOnEachSide) + (2 * dotAmount - 1) * dotDiameter);

        setMeasuredDimension(width, height);

    }

    // called by the ObjectAnimator in order to setPosition during frame change in Animation
    // call by the ObjectAnimator is hidden and uses Reflection.
    // This method is needed for the ObjectAnimator to work properly
    public void setBounceDotPosition(int bounceDotPosition) {
        this.bounceDotPosition = bounceDotPosition;
        invalidate();
    }


    // setter methods for customization...
    public void setDotRadius(float dotRadius) {
        this.dotRadius = dotRadius;
    }

    public void setBounceDotRadius(float bounceDotRadius) {
        this.bounceDotRadius = bounceDotRadius;
    }

    public void setDotAmount(float dotAmount) {
        this.dotAmount = dotAmount;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }


    /**
     * This is called when the view is attached to a window.  At this point it
     * has a Surface and will start drawing.  Note that this function is
     * guaranteed to be called before {@link #onDraw(Canvas)},
     * however it may be called any time before the first onDraw -- including
     * before or after {@link #onMeasure(int, int)}.
     *
     * @see #onDetachedFromWindow()
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        runAnimation();

    }

    private void runAnimation() {

        ObjectAnimator animator = ObjectAnimator.ofInt(
                this, "bounceDotPosition", 10)
                .setDuration(animationDuration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.start();

    }


}
