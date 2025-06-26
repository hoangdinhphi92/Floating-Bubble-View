package com.torrydo.floating.bubble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.torrydo.floating.AnimHelper;
import com.torrydo.floating.DispatchKeyEventCallback;
import com.torrydo.floating.FloatingBubbleListener;
import com.torrydo.floating.MyBubbleLayout;
import com.torrydo.floating.Utils;
import com.torrydo.floating.ViewUtils;

public class FloatingBubble extends Bubble {

    private final Context context;
    private final boolean forceDragging;
    private final FloatingBubbleListener listener;
    private final float triggerClickableAreaPx;

    /**
     * store previous point for later usage, reset after finger down
     */
    private final Point prevPoint = new Point(0, 0);
    private final PointF rawPointOnDown = new PointF(0f, 0f);
    private final Point newPoint = new Point(0, 0);

    private final int halfScreenWidth = Utils.sez.getFullWidth() / 2;

    public FloatingBubbleListener mListener = null;
    public boolean isDraggable = true;

    public FloatingBubble(Context context) {
        this(context, false, false, null, null, 1f);
    }

    public FloatingBubble(Context context, boolean forceDragging, boolean containCompose, 
                          FloatingBubbleListener listener, DispatchKeyEventCallback onDispatchKeyEvent,
                          float triggerClickableAreaPx) {
        super(context, createRootView(context, onDispatchKeyEvent), containCompose);
        
        this.context = context;
        this.forceDragging = forceDragging;
        this.listener = listener;
        this.triggerClickableAreaPx = triggerClickableAreaPx;
        
        customTouch();
    }

    private static View createRootView(Context context, DispatchKeyEventCallback onDispatchKeyEvent) {
        View root = LayoutInflater.from(context).inflate(com.torrydo.floating.R.layout.bubble, null);
        if (onDispatchKeyEvent != null && root instanceof MyBubbleLayout) {
            ((MyBubbleLayout) root).setOnDispatchKeyEvent(onDispatchKeyEvent::onDispatchKeyEvent);
        }
        return root;
    }

    private SpringAnimation springAnim = null;

    public void animateIconToEdge() {
        if (springAnim != null) {
            springAnim.cancel();
            springAnim = null;
        }

        int bubbleWidth = getRoot().getWidth();

        Pair<Integer, Integer> location = ViewUtils.getXYOnScreen(getRoot());
        int iconX = location.first;

        boolean isOnTheLeftSide = iconX + bubbleWidth / 2 < halfScreenWidth;
        int startX = iconX;
        int endX = isOnTheLeftSide ? 0 : Utils.sez.getSafeWidth() - bubbleWidth;

        springAnim = AnimHelper.startSpringX(
                (float) startX,
                (float) endX,
                new AnimHelper.Event() {
                    @Override
                    public void onUpdate(float value) {
                        try {
                            getLayoutParams().x = (int) value;
                            update();
                        } catch (Exception e) {
                            // Log error if needed
                        }
                    }

                    @Override
                    public void onEnd() {
                        springAnim = null;
                    }
                }
        );
    }

    public void updateLocationUI(float x, float y) {
        float mIconDeltaX = x - rawPointOnDown.x;
        float mIconDeltaY = y - rawPointOnDown.y;

        newPoint.x = prevPoint.x + (int) mIconDeltaX;
        newPoint.y = prevPoint.y + (int) mIconDeltaY;

        // region prevent bubble Y point move outside the screen
        int safeTopY = 0;
        int safeBottomY = Utils.sez.getSafeHeight() - getRoot().getHeight();

        boolean isAboveStatusBar = newPoint.y < safeTopY;
        boolean isUnderSoftNavBar = newPoint.y > safeBottomY;
        if (isAboveStatusBar) {
            newPoint.y = safeTopY;
        } else if (isUnderSoftNavBar) {
            newPoint.y = safeBottomY;
        }
        // endregion

        getLayoutParams().x = newPoint.x;
        getLayoutParams().y = newPoint.y;

        update();
    }

    /**
     * set location without updating UI
     */
    public void setLocation(float x, float y) {
        newPoint.x = (int) x;
        newPoint.y = (int) y;
    }

    /**
     * without status bar
     */
    public Pair<Float, Float> rawLocationOnScreen() {
        return new Pair<>((float) newPoint.x, (float) newPoint.y);
    }

    /**
     * pass close bubble point
     */
    public void animateTo(float x, float y) {
        animateTo(x, y, SpringForce.STIFFNESS_MEDIUM);
    }

    public void animateTo(float x, float y, float stiffness) {
        AnimHelper.animateSpringPath(
                (float) newPoint.x,
                (float) newPoint.y,
                x,
                y,
                new AnimHelper.Event() {
                    @Override
                    public void onUpdatePoint(float x, float y) {
                        getLayoutParams().x = (int) x;
                        getLayoutParams().y = (int) y;
                        update();
                    }

                    @Override
                    public void onEnd() {
                        springAnim = null;
                    }
                },
                stiffness,
                SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        );
    }

    public void safeCancelAnimation() {
        if (springAnim != null) {
            springAnim.cancel();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void customTouch() {
        MyBubbleLayout bubbleLayout = (MyBubbleLayout) getRoot();
        
        bubbleLayout.setIgnoreChildEvent((motionEvent) -> {
            if (forceDragging) return true;
            
            if (!isDraggable) return false;
            
            // Simplified logic - in full implementation would check clickable area
            return Math.abs(motionEvent.getRawX() - rawPointOnDown.x) > triggerClickableAreaPx ||
                   Math.abs(motionEvent.getRawY() - rawPointOnDown.y) > triggerClickableAreaPx;
        });

        bubbleLayout.setDoOnTouchEvent((motionEvent) -> {
            if (!isDraggable) return;

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onFingerDown(motionEvent);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onFingerMove(motionEvent);
                    break;
                case MotionEvent.ACTION_UP:
                    onFingerUp(motionEvent);
                    break;
            }
        });
    }

    private void onFingerDown(MotionEvent event) {
        // Update gesture exclusion
        ViewUtils.updateGestureExclusion(getRoot());

        if (springAnim != null) {
            springAnim.cancel();
            springAnim = null;
        }

        rawPointOnDown.x = event.getRawX();
        rawPointOnDown.y = event.getRawY();

        prevPoint.x = getLayoutParams().x;
        prevPoint.y = getLayoutParams().y;

        if (mListener != null) {
            mListener.onFingerDown(event.getRawX(), event.getRawY());
        }
        if (listener != null) {
            listener.onFingerDown(event.getRawX(), event.getRawY());
        }
    }

    private void onFingerMove(MotionEvent event) {
        updateLocationUI(event.getRawX(), event.getRawY());

        if (mListener != null) {
            mListener.onFingerMove(event.getRawX(), event.getRawY());
        }
        if (listener != null) {
            listener.onFingerMove(event.getRawX(), event.getRawY());
        }
    }

    private void onFingerUp(MotionEvent event) {
        if (mListener != null) {
            mListener.onFingerUp(event.getRawX(), event.getRawY());
        }
        if (listener != null) {
            listener.onFingerUp(event.getRawX(), event.getRawY());
        }
    }
}