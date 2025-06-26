package com.torrydo.floating;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.torrydo.screenez.ScreenEasy;
import java.lang.ref.WeakReference;

public class Utils {
    
    private Utils() {
        // Utility class
    }

    public static final ScreenEasy sez = new ScreenEasy();

    public  static Bitmap toBitmap(int drawableRes, Context context) {
        WeakReference<Context> weakContext = new WeakReference<>(context);
        Context ctx = weakContext.get();
        if (ctx != null) {
            // Note: DrawableKt.toBitmap is Kotlin-specific, would need Android Bitmap creation
            // For now, returning null - in full implementation would convert drawable to bitmap
            return null;
        }
        return null;
    }

    public static int toDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public  static int toPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}