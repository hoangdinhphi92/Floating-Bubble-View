package com.torrydo.floating.bubble;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.torrydo.floating.AndroidVersions;
import com.torrydo.floating.Utils;
import com.torrydo.floating.ViewUtils;

public class FloatingBottomBackground extends Bubble {

    public boolean isShowing = false;

    public FloatingBottomBackground(Context context) {
        super(context, new LinearLayout(context));
        
        setRoot(LayoutInflater.from(context).inflate(com.torrydo.floating.R.layout.bottom_background, null));
        setupLayoutParams();
    }

    @Override
    public void show() {
        if (isShowing) return;
        isShowing = true;
        super.show();
    }

    @Override
    public void remove() {
        isShowing = false;
        super.remove();
    }

    private void setupLayoutParams() {
        WindowManager.LayoutParams layoutParams = getLayoutParams();
        
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = Utils.sez.getFullHeight() / 5;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

        // layoutParams.windowAnimations = R.style.default_close_bubble_style;

        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.type = Build.VERSION.SDK_INT >= AndroidVersions.VERSION_8 
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
    }
}