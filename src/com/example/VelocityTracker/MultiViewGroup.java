package com.example.VelocityTracker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Scroller;


public class MultiViewGroup extends ViewGroup {
    private Context mContext;

    private static String TAG = "MultiViewGroup";
    private int curScreen = 0;

    private Scroller mScroller = null;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;

    public static int SNAP_VELOCITY = 600;
    private int mTouchSlop = 0;
    private float mLastionMotionX = 0;
    private float mLastMotionY = 0;

    private VelocityTracker mVelocityTracker = null;


    public MultiViewGroup(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MultiViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mScroller = new Scroller(mContext);

        LinearLayout oneLL = new LinearLayout(mContext);
        oneLL.setBackgroundColor(Color.RED);

        addView(oneLL);

        LinearLayout twoLL = new LinearLayout(mContext);
        twoLL.setBackgroundColor(Color.WHITE);
        addView(twoLL);

        LinearLayout threeLL = new LinearLayout(mContext);
        threeLL.setBackgroundColor(Color.YELLOW);

        addView(threeLL);

        LinearLayout fourLL = new LinearLayout(mContext);
        fourLL.setBackgroundColor(Color.BLUE);
        addView(fourLL);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        Log.e(TAG, "computeScroll");
        if (mScroller.computeScrollOffset()) {
            Log.e(TAG, mScroller.getCurrX() + "======" + mScroller.getCurrY());
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            Log.e(TAG, "### getleft is " + getLeft() + " ### getRight is " + getRight());
            postInvalidate();
        } else
            Log.i(TAG, "have done the scoller -----");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);

        final int action = ev.getAction();

        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onInterceptTouchEvent move");
                final int xDiff = (int) Math.abs(mLastionMotionX - x);
                if (xDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onInterceptTouchEvent down");
                mLastionMotionX = x;
                mLastMotionY = y;
                Log.e(TAG, "scroller is finished" + mScroller.isFinished() + "");
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onInterceptTouchEvent up or cancel");
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        Log.e(TAG, mTouchState + "====" + TOUCH_STATE_REST);

        return mTouchState != TOUCH_STATE_REST;
    }


    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "--- onTouchEvent--> ");

        // TODO Auto-generated method stub
        Log.e(TAG, "onTouchEvent start");
        if (mVelocityTracker == null) {

            Log.e(TAG, "onTouchEvent start-------** VelocityTracker.obtain");

            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
        super.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                }

                mLastionMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastionMotionX - x);
                int deltaY = (int) (mLastMotionY - y);

                if (Math.abs(deltaX) < Math.abs(deltaY)) {
                    break;
                }
                scrollBy(deltaX, 0);

                Log.e(TAG, "--- MotionEvent.ACTION_MOVE--> detaX is " + deltaX);
                mLastionMotionX = x;
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();

                Log.e(TAG, "---velocityX---" + velocityX);

                if (velocityX > SNAP_VELOCITY && curScreen > 0) {
                    // Fling enough to move left
                    Log.e(TAG, "snap left");
                    snapToScreen(curScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY && curScreen < (getChildCount() - 1)) {
                    Log.e(TAG, "snap right");
                    snapToScreen(curScreen + 1);
                } else {
                    snapToDestination();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    private void snapToDestination() {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        Log.e(TAG, "### onTouchEvent snapToDestination ### scrollX is " + scrollX);
        int destScreen = (getScrollX() + getWidth() / 2) / getWidth();
        Log.e(TAG, "### onTouchEvent  ACTION_UP### dx destScreen " + destScreen);
        snapToScreen(destScreen);
    }

    //滑动到相应的View
    private void snapToScreen(int whichScreen) {
        curScreen = whichScreen;
        if (curScreen > getChildCount() - 1)
            curScreen = getChildCount() - 1;

        int dx = curScreen * getWidth() - getScrollX();

        Log.e(TAG, "### onTouchEvent  ACTION_UP### dx is " + dx);

        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx) * 2);
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.i(TAG, "--- start onMeasure --");

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        int childCount = getChildCount();
        Log.i(TAG, "--- onMeasure childCount is -->" + childCount);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(getWidth(), 500);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        Log.i(TAG, "--- start onLayout --");
        int startLeft = 0;
        int startTop = 10;
        int childCount = getChildCount();
        Log.i(TAG, "--- onLayout childCount is -->" + childCount);

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != View.GONE)
                child.layout(startLeft, startTop,
                        startLeft + getWidth(),
                        startTop + 500);

            startLeft = startLeft + getWidth();
        }
    }
}
