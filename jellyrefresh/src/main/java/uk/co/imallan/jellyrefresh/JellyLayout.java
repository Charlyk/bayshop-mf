package uk.co.imallan.jellyrefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

class JellyLayout extends FrameLayout {

    private Paint mPaint;
    private Path mPath;
    private Path mShadowPath;
    private Paint mShadowPaint;
    @ColorInt
    private int mColor = Color.GRAY;
    private ViewOutlineProvider mViewOutlineProvider;
    private float mPointX;
    float mHeaderHeight = 0;
    float mPullHeight = 0;
    private Context mContext;
    private static final int SHADOW_OFFSET = 7;

    public JellyLayout(Context context) {
        this(context, null);
        init(context);
    }

    public JellyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public JellyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        mContext = context;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mShadowPath = new Path();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mViewOutlineProvider = new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    if (mPath.isConvex()) outline.setConvexPath(mPath);
                    if (mShadowPath.isConvex()) outline.setConvexPath(mShadowPath);
                }
            };

        }
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setPointX(float pointX) {
        boolean needInvalidate = pointX != mPointX;
        mPointX = pointX;
        if (needInvalidate) invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPulling(canvas);
    }

    private void drawPulling(Canvas canvas) {

        final int width = canvas.getWidth();
        final float mDisplayX = (mPointX - width / 2f) * 0.5f + width / 2f;
        mPaint.setColor(mColor);

        int color = Color.parseColor("#7F909090");
        int transparent = mContext.getResources().getColor(android.R.color.transparent);

        int headerHeight = (int) mHeaderHeight;
        int pullHeight = (int) mPullHeight;

        mShadowPaint.setShader(new LinearGradient(0, 0, 0, headerHeight + SHADOW_OFFSET, color, transparent, Shader.TileMode.MIRROR));

        mPath.rewind();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, headerHeight);
        mPath.quadTo(mDisplayX, pullHeight, width, headerHeight);
        mPath.lineTo(width, 0);
        mPath.close();

        mShadowPath.rewind();
        mShadowPath.moveTo(0, 0);
        mShadowPath.lineTo(0, headerHeight + SHADOW_OFFSET);
        mShadowPath.quadTo(mDisplayX, pullHeight + SHADOW_OFFSET, width, headerHeight + SHADOW_OFFSET);
        mShadowPath.lineTo(width, 0);
        mShadowPath.close();

        canvas.drawPath(mShadowPath, mShadowPaint);
        canvas.drawPath(mPath, mPaint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(mViewOutlineProvider);
        }

    }

    public void setHeaderHeight(float headerHeight) {
        mHeaderHeight = headerHeight;
    }
}
