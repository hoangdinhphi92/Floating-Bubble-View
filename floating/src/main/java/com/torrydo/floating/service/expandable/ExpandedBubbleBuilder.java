package com.torrydo.floating.service.expandable;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.StyleRes;
import com.torrydo.floating.AndroidVersions;
import com.torrydo.floating.DispatchKeyEventCallback;

public class ExpandedBubbleBuilder {

    private final Context context;

    // expanded bubble
    View expandedView = null;
    // Note: Compose support removed for Java-only implementation
    // ComposeView expandedCompose = null;
    Integer expandedBubbleStyle = null;

    // config
    boolean isDraggable = true;
    boolean isAnimateToEdgeEnabled = true;
    boolean fillMaxWidth = false;
    float dimAmount = 0f;
    int startX = 0;
    int startY = 0;
    DispatchKeyEventCallback onDispatchKeyEvent = null;

    public ExpandedBubbleBuilder(Context context) {
        this.context = context;
    }

    public WindowManager.LayoutParams defaultLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        if (fillMaxWidth) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.x = startX;
        params.y = startY;

        if (dimAmount > 0) {
            params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = dimAmount;
        }

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.format = PixelFormat.TRANSLUCENT;

        params.type = Build.VERSION.SDK_INT >= AndroidVersions.VERSION_8 
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        return params;
    }

    /**
     * set view to expanded bubble
     */
    public ExpandedBubbleBuilder expandedView(View view) {
        expandedView = view;
        return this;
    }

    /**
     * set open and exit animation to expanded bubble
     */
    public ExpandedBubbleBuilder style(@StyleRes Integer style) {
        expandedBubbleStyle = style;
        return this;
    }

    /**
     * set whether expanded bubble is draggable
     */
    public ExpandedBubbleBuilder draggable(boolean draggable) {
        isDraggable = draggable;
        return this;
    }

    /**
     * set whether to animate to edge when released
     */
    public ExpandedBubbleBuilder enableAnimateToEdge(boolean enabled) {
        isAnimateToEdgeEnabled = enabled;
        return this;
    }

    /**
     * set whether to fill max width
     */
    public ExpandedBubbleBuilder fillMaxWidth(boolean fillMaxWidth) {
        this.fillMaxWidth = fillMaxWidth;
        return this;
    }

    /**
     * set dim amount for background dimming
     */
    public ExpandedBubbleBuilder dimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    /**
     * set start location
     */
    public ExpandedBubbleBuilder startLocation(int x, int y) {
        this.startX = x;
        this.startY = y;
        return this;
    }

    /**
     * set callback for dispatch key event
     */
    public ExpandedBubbleBuilder setDispatchKeyEvent(DispatchKeyEventCallback callback) {
        this.onDispatchKeyEvent = callback;
        return this;
    }
}