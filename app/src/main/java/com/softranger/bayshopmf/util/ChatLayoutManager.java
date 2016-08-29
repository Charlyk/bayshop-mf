package com.softranger.bayshopmf.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Eduard Albu on 8/26/16, 08, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */
public class ChatLayoutManager extends RecyclerView.LayoutManager {

    private static final float VIEW_HEIGHT_PERCENT = 0.75f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        fillDown(recycler);
    }

    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        Rect decorRect = new Rect();
        calculateItemDecorationsForChild(child, decorRect);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        widthUsed = updateSpecWithExtra(widthUsed, lp.leftMargin + decorRect.left, lp.rightMargin + decorRect.right);
        heightUsed = updateSpecWithExtra(heightUsed, lp.topMargin + decorRect.top, lp.bottomMargin + decorRect.bottom);
        child.measure(widthUsed, heightUsed);
    }

    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset == 0 && endInset == 0) {
            return spec;
        }

        final int mode = View.MeasureSpec.getMode(spec);

        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(spec) - startInset - endInset, mode);
        }
        return spec;
    }

    private void fillDown(RecyclerView.Recycler recycler) {
        int pos = 0;
        boolean fillDown = true;
        int height = getHeight();
        int viewTop = 0;
        int itemCount = getItemCount();
        int viewHeight = (int) (getHeight() * VIEW_HEIGHT_PERCENT);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY);

        while (fillDown && pos < itemCount) {
            View view = recycler.getViewForPosition(pos);
            addView(view);
            measureChildWithMargins(view, widthSpec, heightSpec);
            int decorationMeasureWidth = getDecoratedMeasuredWidth(view);
            layoutDecorated(view, 0, viewTop, decorationMeasureWidth, viewTop + viewHeight);
            viewTop = getDecoratedBottom(view);
            fillDown = viewTop <= height;
            pos++;
        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        offsetChildrenVertical(-dy);
        return dy;
    }
}
