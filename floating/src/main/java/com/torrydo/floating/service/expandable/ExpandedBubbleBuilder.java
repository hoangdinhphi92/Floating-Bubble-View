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

public class ExpandedBubbleBuilder {

    public interface DispatchKeyEventCallback {
        Boolean onDispatchKeyEvent(KeyEvent event);
    }

    private final Context context;

    // expanded bubble
    View expandedView = null;
    // Note: Compose support removed for Java-only implementation
    // ComposeView expandedCompose = null;
    Integer expandedBubbleStyle = null;

    // config
    boolean isDraggable = true;
    DispatchKeyEventCallback onDispatchKeyEvent = null;

    public ExpandedBubbleBuilder(Context context) {
        this.context = context;
    }

    public WindowManager.LayoutParams defaultLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.x = 0;
        params.y = 0;

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
    public ExpandedBubbleBuilder expandedBubbleStyle(@StyleRes Integer style) {
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
     * set callback for dispatch key event
     */
    public ExpandedBubbleBuilder setDispatchKeyEvent(DispatchKeyEventCallback callback) {
        this.onDispatchKeyEvent = callback;
        return this;
    }
}