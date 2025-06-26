package com.torrydo.floating.bubble;

import android.app.Service;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
// Note: ComposeLifecycleOwner removed as it's Compose-specific

public class Bubble {

    private WindowManager windowManager;
    private WindowManager.LayoutParams rootParams;
    private View root;

    // Note: Compose-related fields removed for Java-only implementation
    // private ComposeLifecycleOwner composeOwner;
    // private boolean isComposeOwnerInitialized;

    public Bubble(Context context, View root) {
        this(context, root, false);
    }

    public Bubble(Context context, View root, boolean containCompose) {
        this.windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        this.rootParams = new WindowManager.LayoutParams();
        this.root = root;

        // Note: Compose integration removed for Java-only implementation
        // if (containCompose) {
        //     composeOwner = new ComposeLifecycleOwner();
        //     composeOwner.attachToDecorView(root);
        // }
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public View getRoot() {
        return root;
    }

    public void setRoot(View root) {
        this.root = root;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return rootParams;
    }

    public void setLayoutParams(WindowManager.LayoutParams layoutParams) {
        this.rootParams = layoutParams;
    }

    public ViewGroup getRootGroup() {
        return (ViewGroup) root;
    }

    // public methods ------------------------------------------------------------------------------

    public void show() {
        try {
            // Note: Compose lifecycle management removed for Java-only implementation
            // if (containCompose) {
            //     if (!isComposeOwnerInitialized) {
            //         composeOwner.onCreate(); // only call this once
            //         isComposeOwnerInitialized = true;
            //     }
            //     composeOwner.onStart();
            //     composeOwner.onResume();
            // }

            windowManager.addView(root, rootParams);
        } catch (Exception e) {
            // e.printStackTrace(); // xxx has already added to WindowManager
        }
    }

    /**
     * - don't call remove if the view did not call show() previously
     * - call windowManager.removeViewImmediate() will make the view can't change when added again
     * - add this line 'if (root.windowToken == null) return' will prevent the view from being removed in some cases
     */
    public void remove() {
        // if (root.getWindowToken() == null) return;
        try {
            windowManager.removeView(root);

            // Note: Compose lifecycle management removed for Java-only implementation
            // if (containCompose) {
            //     composeOwner.onPause();
            //     composeOwner.onStop();
            //     composeOwner.onDestroy();
            // }
        } catch (Exception ignored) {}
    }

    public void update() {
        // if (root.getWindowToken() == null) return;
        try {
            windowManager.updateViewLayout(root, rootParams);
        } catch (Exception ignored) {}
    }
}