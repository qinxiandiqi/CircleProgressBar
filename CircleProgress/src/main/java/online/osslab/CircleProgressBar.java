package online.osslab;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * http://circleprogress.osslab.online
 */
public class CircleProgressBar extends TextView {

    // Properties
    private float progress = 0;
    private float strokeWidth = getResources().getDimension(R.dimen.default_stroke_width);
    private float backgroundStrokeWidth = getResources().getDimension(R.dimen.default_background_stroke_width);
    private int progressColor = Color.BLACK;
    private int progressBgColor = Color.GRAY;
    private int backgroundColor = Color.WHITE;

    // Object used to draw
    private int startAngle = -90;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint progressBgPaint;
    private Paint progressPaint;

    private IProgressListener progressListener;

    //region Constructor & Init Method
    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0, 0);
        //Reading values from the XML layout
        try {
            // Value
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress_value, progress);
            // StrokeWidth
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progress_width, strokeWidth);
            backgroundStrokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progress_background_width, backgroundStrokeWidth);
            // Color
            progressColor = typedArray.getInt(R.styleable.CircleProgressBar_progress_color, progressColor);
            progressBgColor = typedArray.getInt(R.styleable.CircleProgressBar_progress_background_color, progressBgColor);
            backgroundColor = typedArray.getInt(R.styleable.CircleProgressBar_background_color, backgroundColor);
        } finally {
            typedArray.recycle();
        }

        // Init progress Background
        progressBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBgPaint.setColor(progressBgColor);
        progressBgPaint.setStyle(Paint.Style.STROKE);
        progressBgPaint.setStrokeWidth(backgroundStrokeWidth);

        // Init progress Foreground
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);

        // Init background color
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setGravity(Gravity.CENTER);
        setBackgroundResource(0);
    }
    //endregion

    //region Draw Method
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawOval(rectF, backgroundPaint);
        canvas.drawOval(rectF, progressBgPaint);
        float angle = 360 * progress / 100;
        canvas.drawArc(rectF, startAngle, angle, false, progressPaint);
        super.onDraw(canvas);
    }
    //endregion

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = (strokeWidth > backgroundStrokeWidth) ? strokeWidth : backgroundStrokeWidth;
        rectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }
    //endregion

    public IProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(IProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    //region Method Get/Set
    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = (progress<=100) ? progress : 100;
        invalidate();
        if (progressListener != null){
            progressListener.onProgress(progress);
        }
    }

    public float getProgressBarWidth() {
        return strokeWidth;
    }

    public void setProgressBarWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        progressPaint.setStrokeWidth(strokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public float getBackgroundProgressBarWidth() {
        return backgroundStrokeWidth;
    }

    public void setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        this.backgroundStrokeWidth = backgroundStrokeWidth;
        progressBgPaint.setStrokeWidth(backgroundStrokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
    }

    public int getProgressBgColor() {
        return progressBgColor;
    }

    public void setProgressBgColor(int progressBgColor) {
        this.progressBgColor = progressBgColor;
        progressBgPaint.setColor(progressBgColor);
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor){
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
    }
    //endregion

    //region Other Method
    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link CircleProgressBar#setProgress(float)} directly within this method.
     *
     * @param progress The progress it should animate to it.
     */
    public void setProgressWithAnimation(float progress) {
        setProgressWithAnimation(progress, 1500);
    }

    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link CircleProgressBar#setProgress(float)} directly within this method.
     *
     * @param progress The progress it should animate to it.
     * @param duration The length of the animation, in milliseconds.
     */
    public void setProgressWithAnimation(float progress, long duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
    //endregion

    public interface IProgressListener{
        void onProgress(float progress);
    }
}
