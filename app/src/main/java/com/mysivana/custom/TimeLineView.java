/**
 * Copyright MySivana LLC
 *
 * (C) Copyright MySivana LLC   All rights reserved.
 *
 * NOTICE:  All information contained herein or attendant hereto is,
 *          and remains, the property of MySivana LLC.  Many of the
 *          intellectual and technical concepts contained herein are
 *          proprietary to MySivana LLC. Any dissemination of this
 *          information or reproduction of this material is strictly
 *          forbidden unless prior written permission is obtained
 *          from MySivana LLC.
 *
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 * Revision History
 * ========================================================================
 * DATE             : PROGRAMMER  : DESCRIPTION
 * ========================================================================
 * JUNE 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.mysivana.R;
import com.mysivana.util.UIUtils;
/**
 * Timeline view used in transaction pages
 */
public class TimeLineView extends View {

    private Drawable mMarker;
    private Drawable mStartLine;
    private Drawable mEndLine;
    private int mMarkerSize;
    private int mLineSize;
    private int mLineOrientation;
    private int mLinePadding;
    private boolean mMarkerInCenter;
    private boolean mShowTopLine;
    private boolean mShowBottomLine;

    private Rect mBounds;
    private Context mContext;

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.timeline_style);
        mMarker = typedArray.getDrawable(R.styleable.timeline_style_marker);
        mStartLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mEndLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mMarkerSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_markerSize, UIUtils.dpToPx(25, mContext));
        mLineSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_lineSize, UIUtils.dpToPx(2, mContext));
        mLineOrientation = typedArray.getInt(R.styleable.timeline_style_lineOrientation, 1);
        mLinePadding = typedArray.getDimensionPixelSize(R.styleable.timeline_style_linePadding, 0);
        mMarkerInCenter = typedArray.getBoolean(R.styleable.timeline_style_markerInCenter, true);
        mShowTopLine = typedArray.getBoolean(R.styleable.timeline_style_showTopLine, true);
        mShowBottomLine = typedArray.getBoolean(R.styleable.timeline_style_showBottomLine, true);
        typedArray.recycle();

        if (mMarker == null) {
            mMarker = mContext.getResources().getDrawable(R.drawable.marker);
        }

        if (mStartLine == null && mEndLine == null) {

            if (mShowTopLine)
                mStartLine = new ColorDrawable(mContext.getResources().getColor(android.R.color.darker_gray));
            if (mShowBottomLine)
                mEndLine = new ColorDrawable(mContext.getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Width measurements of the width and height and the inside view of child controls
        int w = mMarkerSize + getPaddingLeft() + getPaddingRight();
        int h = mMarkerSize + getPaddingTop() + getPaddingBottom();

        // Width and height to determine the final view through a systematic approach to decision-making
        int widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
        int heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
        initDrawable();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // When the view is displayed when the callback
        // Positioning Drawable coordinates, then draw
        initDrawable();
    }

    private void initDrawable() {
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();

        int width = getWidth();// Width of current custom view
        int height = getHeight();

        int cWidth = width - pLeft - pRight;// Circle width
        int cHeight = height - pTop - pBottom;

        int markSize = Math.min(mMarkerSize, Math.min(cWidth, cHeight));

        if (mMarkerInCenter) { //Marker in center is true

            if (mMarker != null) {
                mMarker.setBounds((width / 2) - (markSize / 2), (height / 2) - (markSize / 2), (width / 2) + (markSize / 2), (height / 2) + (markSize / 2));
                mBounds = mMarker.getBounds();
            }

        } else { //Marker in center is false

            if (mMarker != null) {
                mMarker.setBounds(pLeft, pTop, pLeft + markSize, pTop + markSize);
                mBounds = mMarker.getBounds();
            }
        }

        int centerX = mBounds.centerX();
        int lineLeft = centerX - (mLineSize >> 1);

        if (mLineOrientation == 0) {

            //Horizontal Line
            if (mStartLine != null && mShowTopLine) {
                mStartLine.setBounds(0, pTop + (mBounds.height() / 2), mBounds.left - mLinePadding, (mBounds.height() / 2) + pTop + mLineSize);
            }

            if (mEndLine != null && mShowBottomLine) {
                mEndLine.setBounds(mBounds.right + mLinePadding, pTop + (mBounds.height() / 2), width, (mBounds.height() / 2) + pTop + mLineSize);
            }
        } else {

            //Vertical Line
            if (mStartLine != null && mShowTopLine) {
                mStartLine.setBounds(lineLeft, 0, mLineSize + lineLeft, mBounds.top - mLinePadding);
            }

            if (mEndLine != null && mShowBottomLine) {
                mEndLine.setBounds(lineLeft, mBounds.bottom + mLinePadding, mLineSize + lineLeft, height);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMarker != null) {
            mMarker.draw(canvas);
        }

        if (mStartLine != null && mShowTopLine) {
            mStartLine.draw(canvas);
        }

        if (mEndLine != null && mShowBottomLine) {
            mEndLine.draw(canvas);
        }
    }

    /**
     * Sets marker.
     *
     * @param marker will set marker drawable to timeline
     */
    public void setMarker(Drawable marker) {
        mMarker = marker;
        initDrawable();
    }

    /**
     * Sets marker.
     *
     * @param marker will set marker drawable to timeline
     * @param color  with a color
     */
    public void setMarker(Drawable marker, int color) {
        mMarker = marker;
        mMarker.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        initDrawable();
    }

    /**
     * Sets marker color.
     *
     * @param color the color
     */
    public void setMarkerColor(int color) {
        mMarker.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        initDrawable();
    }

    /**
     * Sets start line.
     *
     * @param color    the color
     * @param viewType the view type
     */
    public void setStartLine(int color, int viewType) {
        mStartLine = new ColorDrawable(color);
        initLine(viewType);
    }

    /**
     * Sets end line.
     *
     * @param color    the color
     * @param viewType the view type
     */
    public void setEndLine(int color, int viewType) {
        mEndLine = new ColorDrawable(color);
        initLine(viewType);
    }

    /**
     * Shows end line.
     *
     * @param showBottomLine true to show the line at bottom
     */
    public void showBottomLine(boolean showBottomLine) {
        mShowBottomLine = showBottomLine;
        initDrawable();
    }

    /**
     * Sets marker size.
     *
     * @param markerSize the marker size
     */
    public void setMarkerSize(int markerSize) {
        mMarkerSize = markerSize;
        initDrawable();
    }

    /**
     * Sets line size.
     *
     * @param lineSize the line size
     */
    public void setLineSize(int lineSize) {
        mLineSize = lineSize;
        initDrawable();
    }

    /**
     * Sets line padding
     *
     * @param padding the line padding
     */
    public void setLinePadding(int padding) {
        mLinePadding = padding;
        initDrawable();
    }

    private void setStartLine(Drawable startLine) {
        mStartLine = startLine;
        initDrawable();
    }

    private void setEndLine(Drawable endLine) {
        mEndLine = endLine;
        initDrawable();
    }

    /**
     * Init line.
     *
     * @param viewType the view type
     */
    public void initLine(int viewType) {
        if (viewType == LineType.BEGIN) {
            setStartLine(null);
        } else if (viewType == LineType.END) {
            setEndLine(null);
        } else if (viewType == LineType.ONLYONE) {
            setStartLine(null);
            setEndLine(null);
        }
        initDrawable();
    }

    /**
     * Gets timeline view type.
     *
     * @param position   the position
     * @param total_size the total size
     * @return the time line view type
     */
    public static int getTimeLineViewType(int position, int total_size) {
        if (total_size == 1) {
            return LineType.ONLYONE;
        } else if (position == 0) {
            return LineType.BEGIN;
        } else if (position == total_size - 1) {
            return LineType.END;
        } else {
            return LineType.NORMAL;
        }
    }

    public static class LineType {
        public static final int NORMAL = 0;
        public static final int BEGIN = 1;
        public static final int END = 2;
        public static final int ONLYONE = 3;
    }
}
