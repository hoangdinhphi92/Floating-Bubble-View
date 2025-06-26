package com.torrydo.floating;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyBubbleLayout extends LinearLayout {

    public interface IgnoreChildEventCallback {
        boolean shouldIgnore(MotionEvent event);
    }

    public interface TouchEventCallback {
        void onTouchEvent(MotionEvent event);
    }

    private IgnoreChildEventCallback ignoreChildEvent = event -> false;
    private TouchEventCallback doOnTouchEvent = event -> {};
    private DispatchKeyEventCallback onDispatchKeyEvent = null;

    public MyBubbleLayout(Context context) {
        this(context, null);
    }

    public MyBubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIgnoreChildEvent(IgnoreChildEventCallback callback) {
        this.ignoreChildEvent = callback != null ? callback : event -> false;
    }

    public void setDoOnTouchEvent(TouchEventCallback callback) {
        this.doOnTouchEvent = callback != null ? callback : event -> {};
    }

    public void setOnDispatchKeyEvent(DispatchKeyEventCallback callback) {
        this.onDispatchKeyEvent = callback;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (onDispatchKeyEvent != null && event != null) {
            Boolean result = onDispatchKeyEvent.onDispatchKeyEvent(event);
            if (result != null) {
                return result;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev != null) {
            doOnTouchEvent.onTouchEvent(ev);
            return ignoreChildEvent.shouldIgnore(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            doOnTouchEvent.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }
}