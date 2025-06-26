package com.torrydo.floating;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import java.util.ArrayList;
import java.util.List;

final class ViewUtils {
    
    private ViewUtils() {
        // Utility class
    }

    // exclude view gesture on home screen
    private static List<Rect> exclusionRects = new ArrayList<>();

    static void updateGestureExclusion(View view) {
        if (Build.VERSION.SDK_INT < AndroidVersions.VERSION_10) return;

        Point screenSize = Utils.sez.getFullSize();

        exclusionRects.clear();
        Rect rect = new Rect(0, 0, view.getWidth(), screenSize.y);
        exclusionRects.add(rect);

        view.setSystemGestureExclusionRects(exclusionRects);
    }

    /**
     * by default, display over other app permission will be granted automatically if android's version smaller than android M
     * - some MIUI devices may not work properly
     */
    static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < AndroidVersions.VERSION_6) {
            return true;
        }
        return Settings.canDrawOverlays(context);
    }

    static void afterMeasured(View view, Runnable afterMeasuredWork) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (view.getMeasuredWidth() > 0 && view.getMeasuredHeight() > 0) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    afterMeasuredWork.run();
                }
            }
        });
    }

    /**
     * @return Pair( 0 .. x, 0 .. y )
     */
    static Pair<Integer, Integer> getXYOnScreen(View view) {
        int[] arr = new int[2];
        view.getLocationOnScreen(arr);
        return new Pair<>(arr[0], arr[1]);
    }
}