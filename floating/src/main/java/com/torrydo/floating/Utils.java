package com.torrydo.floating;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableKt;
import com.torrydo.screenez.ScreenEasy;
import java.lang.ref.WeakReference;

final class Utils {
    
    private Utils() {
        // Utility class
    }

    static final ScreenEasy sez = new ScreenEasy();

    static Bitmap toBitmap(int drawableRes, Context context) {
        WeakReference<Context> weakContext = new WeakReference<>(context);
        Context ctx = weakContext.get();
        if (ctx != null) {
            return DrawableKt.toBitmap(ContextCompat.getDrawable(ctx, drawableRes));
        }
        return null;
    }

    static int toDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    static int toPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}