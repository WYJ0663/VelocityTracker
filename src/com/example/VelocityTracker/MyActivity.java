package com.example.VelocityTracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.TextView;

import java.security.interfaces.ECKey;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
  //  TextView textView;
    private VelocityTracker mVelocityTracker = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    //    textView = (TextView) findViewById(R.id.textView);
    }

    float tempX = 0f;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                acquireVelocityTracker(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                if (mVelocityTracker.getXVelocity() >tempX){
                    tempX = mVelocityTracker.getXVelocity();
                }
                Log.i("yijun","the x velocity is " + mVelocityTracker.getXVelocity() );
//                textView.setText("the x velocity is " + tempX+ "--");
//                textView.append("the y velocity is " + mVelocityTracker.getYVelocity());

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
                break;
        }
        return true;

    }


    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }else {
            mVelocityTracker.clear();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 释放VelocityTracker
     *
     * @see android.view.VelocityTracker#clear()
     * @see android.view.VelocityTracker#recycle()
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private static final String sFormatStr = "velocityX=%f\nvelocityY=%f";

    /**
     * 记录当前速度
     *
     * @param velocityX x轴速度
     * @param velocityY y轴速度
     */
    private void recodeInfo(final float velocityX, final float velocityY) {
        final String info = String.format(sFormatStr, velocityX, velocityY);
   //     textView.setText(info);
    }
}
