package com.torrydo.floating.service.expandable;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.StyleRes;
import com.torrydo.floating.AndroidVersions;
import com.torrydo.floating.CloseBubbleBehavior;
import com.torrydo.floating.DispatchKeyEventCallback;
import com.torrydo.floating.FloatingBubbleListener;
import com.torrydo.floating.MyBubbleLayout;
import com.torrydo.floating.Utils;
import com.torrydo.floating.bubble.FloatingBubble;

public class BubbleBuilder {



    private final Context context;

    // bubble
    View bubbleView = null;
    // Note: Compose support removed for Java-only implementation
    // ComposeView bubbleCompose = null;
    private Integer bubbleStyle = com.torrydo.floating.R.style.default_bubble_style;

    // close-bubble
    View closeView = null;
    Integer closeBubbleStyle = null;

    // config
    private Point startPoint = new Point(0, 0);
    boolean isAnimateToEdgeEnabled = true;
    boolean isBottomBackgroundEnabled = false;

    int distanceToClosePx = 200;
    int closeBubbleBottomPaddingPx = 80;
    float triggerClickablePerimeterPx = 5f;

    FloatingBubbleListener listener = null;
    CloseBubbleBehavior behavior = CloseBubbleBehavior.FIXED_CLOSE_BUBBLE;

    boolean forceDragging = true;
    boolean isBubbleDraggable = true;

    public DispatchKeyEventCallback onDispatchKeyEvent = null;

    public BubbleBuilder(Context context) {
        this.context = context;
    }

    public WindowManager.LayoutParams defaultLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.x = startPoint.x;
        params.y = startPoint.y;

        if (bubbleStyle != null) {
            params.windowAnimations = bubbleStyle;
        }

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.format = PixelFormat.TRANSLUCENT;

        params.type = Build.VERSION.SDK_INT >= AndroidVersions.VERSION_8 
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        return params;
    }

    /**
     * Set the clickable perimeter of the bubble in pixels (default = 5f).
     *
     * For example, when the bubble is dragged, it will still perform a click if
     * the new location (when the bubble is released) is not further than the last location by the specified pixel amount.
     *
     * @param f The size of the clickable area in pixels.
     * @return This BubbleBuilder instance for method chaining.
     */
    public BubbleBuilder triggerClickablePerimeterPx(float f) {
        this.triggerClickablePerimeterPx = f;
        return this;
    }

    public BubbleBuilder bubbleDraggable(boolean b) {
        isBubbleDraggable = b;
        return this;
    }

    public BubbleBuilder forceDragging(boolean b) {
        this.forceDragging = b;
        return this;
    }

    /**
     * choose behavior for the bubbles
     */
    public BubbleBuilder closeBehavior(CloseBubbleBehavior behavior) {
        this.behavior = behavior;
        return this;
    }

    /**
     * the more value, the larger closeable area
     *
     * @param dp distance between bubble and close-bubble
     */
    public BubbleBuilder distanceToClose(int dp) {
        this.distanceToClosePx = Utils.toPx(dp);
        return this;
    }

    /**
     * @param enabled show gradient dark background on the bottom of the screen
     */
    public BubbleBuilder bottomBackground(boolean enabled) {
        this.isBottomBackgroundEnabled = enabled;
        return this;
    }

    /**
     * @param enabled animate the bubble to the left/right side of the screen when finger is released, true by default
     */
    public BubbleBuilder enableAnimateToEdge(boolean enabled) {
        isAnimateToEdgeEnabled = enabled;
        return this;
    }

    /**
     * set view to bubble
     */
    public BubbleBuilder bubbleView(View view) {
        bubbleView = view;
        return this;
    }

    /**
     * set open and exit animation to bubble
     */
    public BubbleBuilder bubbleStyle(@StyleRes Integer style) {
        bubbleStyle = style;
        return this;
    }

    /**
     * set view to close-bubble
     */
    public BubbleBuilder closeBubbleView(View view) {
        closeView = view;
        return this;
    }

    /**
     * set open and exit animation to close-bubble
     */
    public BubbleBuilder closeBubbleStyle(@StyleRes Integer style) {
        closeBubbleStyle = style;
        return this;
    }

    /**
     * start location of the bubble
     */
    public BubbleBuilder startLocation(float x, float y) {
        startPoint.x = Utils.toPx(x);
        startPoint.y = Utils.toPx(y);
        return this;
    }

    /**
     * start location of the bubble
     */
    public BubbleBuilder startLocationPx(int x, int y) {
        startPoint.x = x;
        startPoint.y = y;
        return this;
    }

    /**
     * bottom padding for close-bubble from the bottom of the screen
     */
    public BubbleBuilder closeBubbleBottomPadding(int dp) {
        closeBubbleBottomPaddingPx = Utils.toPx(dp);
        return this;
    }

    /**
     * add listener to bubble
     */
    public BubbleBuilder addFloatingBubbleListener(FloatingBubbleListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * set callback for dispatch key event
     */
    public BubbleBuilder setDispatchKeyEvent(DispatchKeyEventCallback callback) {
        this.onDispatchKeyEvent = callback;
        return this;
    }
}