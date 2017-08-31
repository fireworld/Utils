package cc.colorcat.toolbox.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import cc.colorcat.toolbox.L;
import cc.colorcat.toolbox.R;
import cc.colorcat.toolbox.Utils;

/**
 * Created by cxx on 2017/8/30.
 * xx.ch@outlook.com
 */
public class TernaryTextView extends View {
    private Drawable[] mDrawables = new Drawable[3];
    private Rect[] mBounds = {new Rect(), new Rect(), new Rect()};
    private CharSequence mText;
    private Paint mTextPaint;

    public TernaryTextView(Context context) {
        super(context);
    }

    public TernaryTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
    }

    public TernaryTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TernaryTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttrs(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14F, context.getResources().getDisplayMetrics());
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TernaryTextView);
        mDrawables[0] = ta.getDrawable(R.styleable.TernaryTextView_drawableStart);
        mDrawables[1] = ta.getDrawable(R.styleable.TernaryTextView_drawableCenter);
        mDrawables[2] = ta.getDrawable(R.styleable.TernaryTextView_drawableEnd);
        mText = ta.getText(R.styleable.TernaryTextView_text);
        if (mText == null) mText = "";
        int textSize = ta.getDimensionPixelSize(R.styleable.TernaryTextView_textSize, defaultSize);
        int textColor = ta.getColor(R.styleable.TernaryTextView_textColor, Color.BLACK);
        ta.recycle();
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
    }

    public void setText(CharSequence text) {
        CharSequence cs = (text != null ? text : "");
        if (!cs.equals(mText)) {
            mText = cs;
            requestLayout();
            invalidate();
        }
    }

    public void setTextColor(@ColorInt int color) {
        if (color != mTextPaint.getColor()) {
            mTextPaint.setColor(color);
            invalidate();
        }
    }

    public void setTextSize(float sizePx) {
        setRawTextSize(sizePx);
    }

    /**
     * Set the default text size to a given unit and value.  See {@link
     * TypedValue} for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    public void setTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()));
    }

    public float getTextSize() {
        return mTextPaint.getTextSize();
    }

    private void setRawTextSize(float size) {
        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(size);
            requestLayout();
            invalidate();
        }
    }

    public void setPaintFlags(int flags) {
        if (flags != mTextPaint.getFlags()) {
            mTextPaint.setFlags(flags);
            requestLayout();
            invalidate();
        }
    }

    public void addPaintFlag(int flag) {
        int oldFlags = mTextPaint.getFlags();
        int newFlags = oldFlags | flag;
        if (newFlags != oldFlags) {
            mTextPaint.setFlags(newFlags);
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        L.d("onMeasure");
        Size reqP = computeRequireSize(getPaddingStart(), getPaddingTop(), getPaddingEnd(), getPaddingBottom());
        setMeasuredDimension(measureSize(reqP.width, widthMeasureSpec), measureSize(reqP.height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        L.i(Utils.format("changed = %b, left = %d, top = %d, right = %d, bottom = %d",
                changed, left, top, right, bottom));
        if (changed) {
            int width = right - left, height = bottom - top;
            computeBounds(0, width, height);
            computeBounds(2, width, height);
            computeBounds(1, width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        L.e("onDraw");
        for (int size = mDrawables.length, index = 0; index < size; ++index) {
            Drawable drawable = mDrawables[index];
            if (drawable != null) {
                drawable.setBounds(mBounds[index]);
                drawable.draw(canvas);
            }
        }
        PointF point = computeTextStart();
        canvas.drawText(mText, 0, mText.length(), point.x, point.y, mTextPaint);
    }

    private int measureSize(int requireSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return Math.min(specSize, requireSize);
            case MeasureSpec.UNSPECIFIED:
                return requireSize;
            default:
                throw new IllegalArgumentException("wrong specMode = " + specMode);
        }
    }

    private void computeBounds(int index, int viewWidth, int viewHeight) {
        Size size = computeDrawSize(viewHeight, mDrawables[index]);
        Rect rect = mBounds[index];
        rect.top = 0;
        rect.bottom = size.height;
        switch (index) {
            case 0:
                rect.left = 0;
                rect.right = rect.left + size.width;
                break;
            case 1:
                rect.left = mBounds[0].right;
                rect.right = mBounds[2].left;
                break;
            case 2:
                rect.right = viewWidth;
                rect.left = rect.right - size.width;
                break;
            default:
                break;
        }
    }

    private static Size computeDrawSize(int viewHeight, Drawable drawable) {
        Size size = new Size();
        // 获取 drawable 原始大小
        if (drawable != null) {
            size.width = drawable.getMinimumWidth();
            size.height = drawable.getMinimumHeight();
        }
        // 将 drawable 的高缩放至 viewHeight 大小，同时drawable 的宽等比缩放
        if (size.height != 0) {
            size.width = viewHeight * size.width / size.height;
        }
        size.height = viewHeight;
        return size;
    }

    private Size computeRequireSize(int paddingStart, int paddingTop, int paddingEnd, int paddingBottom) {
        Size result = new Size();
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        int textHeight = Math.round(0.5F + fm.bottom - fm.top);
        int textWidth = Math.round(0.5F + mTextPaint.measureText(mText, 0, mText.length()));
        for (int size = mDrawables.length, index = 0; index < size; index++) {
            Drawable drawable = mDrawables[index];
            if (drawable != null) {
                int height = drawable.getMinimumHeight();
                if (height > result.height) {
                    result.height = height;
                }
                if (index != 1) {
                    result.width += drawable.getMinimumWidth();
                } else {
                    result.width += Math.max(drawable.getMinimumWidth(), textWidth);
                }
            }
        }
        int textHeightWithPadding = textHeight + paddingTop + paddingBottom;
        int textWidthWithPadding = textWidth + paddingStart + paddingEnd;
        result.width = Math.max(result.width, textWidthWithPadding);
        result.height = Math.max(result.height, textHeightWithPadding);
        return result;
    }

    private PointF computeTextStart() {
        float width = mTextPaint.measureText(mText, 0, mText.length());
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float y = getHeight() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        return new PointF((getWidth() - width) / 2, y);
    }

    private static class Size {
        private int width = 0;
        private int height = 0;
    }
}