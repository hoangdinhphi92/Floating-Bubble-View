package com.torrydo.floating.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import com.torrydo.floating.Utils;

public final class ViewHelper {
    
    private ViewHelper() {
        // Utility class
    }

    //region load bubble
    public static View fromBitmap(Context context, Bitmap bm) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bm);
        return imageView;
    }

    public static View fromBitmap(Context context, Bitmap bm, int widthDp, int heightDp) {
        View view = fromBitmap(context, bm);
        view.setLayoutParams(new ViewGroup.LayoutParams(Utils.toPx(widthDp), Utils.toPx(heightDp)));
        return view;
    }

    public static View fromDrawable(Context context, @DrawableRes int drawable) {
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, drawable));
        return imageView;
    }

    public static View fromDrawable(Context context, @DrawableRes int drawable, int widthDp, int heightDp) {
        View view = fromDrawable(context, drawable);
        view.setLayoutParams(new ViewGroup.LayoutParams(Utils.toPx(widthDp), Utils.toPx(heightDp)));
        return view;
    }
    //endregion
}