package com.torrydo.floating.bubble;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import com.torrydo.floating.AndroidVersions;
import com.torrydo.floating.Utils;
import com.torrydo.floating.ViewUtils;
import com.torrydo.floating.XMath;

public class FloatingCloseBubble extends Bubble {

    private final int distanceToClosePx;
    private final int bottomPaddingPx;

    private int LIMIT_FLY_HEIGHT = 0;
    private int halfWidthPx = 0;
    private int halfHeightPx = 0;

    /**
     * able to interact with bubble, because when the close bubble initialized.
     * 
     * the close-bubble appears on the top left and I can't get it's size, therefore this attribute shows
     * that is the bubble ready to interact (attract, follow, close) bubble or not
     */
    public boolean ableToInteract = false;

    public int width = 0;
    public int height = 0;

    private int halfSafeScreenWidth = 0;
    private int baseX = 0;
    private int baseY = 0;

    private int centerCloseBubbleX = 0;
    private int centerCloseBubbleY = 0;

    public FloatingCloseBubble(Context context, View root, int distanceToClosePx, int bottomPaddingPx) {
        super(context, root);
        this.distanceToClosePx = distanceToClosePx;
        this.bottomPaddingPx = bottomPaddingPx;
        
        setupLayoutParams();
        getRoot().setVisibility(View.INVISIBLE);

        ViewUtils.afterMeasured(getRoot(), () -> {
            width = getRoot().getWidth();
            height = getRoot().getHeight();

            LIMIT_FLY_HEIGHT = Utils.sez.getFullHeight() / 10;

            halfSafeScreenWidth = Utils.sez.getSafeWidth() / 2;
            halfWidthPx = width / 2;
            halfHeightPx = height / 2;
            baseX = halfSafeScreenWidth - halfWidthPx;
            baseY = Utils.sez.getSafeHeight() - height - bottomPaddingPx;

            centerCloseBubbleX = halfSafeScreenWidth;
            centerCloseBubbleY = baseY + halfHeightPx;

            WindowManager.LayoutParams layoutParams = getLayoutParams();
            layoutParams.x = baseX;
            layoutParams.y = baseY;

            update();
            ableToInteract = true;
            getRoot().setVisibility(View.VISIBLE);
        });
    }

    private void setupLayoutParams() {
        WindowManager.LayoutParams layoutParams = getLayoutParams();
        
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.format = PixelFormat.TRANSLUCENT;

        layoutParams.type = Build.VERSION.SDK_INT >= AndroidVersions.VERSION_8 
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
    }

    //region Public methods ------------------------------------------------------------------------

    private boolean isBubbleAnimated = false;

    /**
     * x, y: point on screen
     * @return TRUE: if the bubble is attracted to the center of the close-bubble
     *         FALSE: if the bubble is too far from the close-bubble
     */
    public boolean tryAttractBubble(FloatingBubble floatingBubble, float x, float y) {
        if (isFingerInsideClosableArea(x, y) && ableToInteract) {
            if (!isBubbleAnimated) {
                int bWidth = floatingBubble.getRoot().getWidth();
                int bHeight = floatingBubble.getRoot().getHeight();

                int xOffset = (width - bWidth) / 2;
                int yOffset = (height - bHeight) / 2;

                float xUpdated = (float) (baseX + xOffset);
                float yUpdated = (float) (baseY + yOffset);

                floatingBubble.animateTo(xUpdated, yUpdated);
                floatingBubble.setLocation(xUpdated, yUpdated);

                isBubbleAnimated = true;
            }
            return true;
        } else {
            isBubbleAnimated = false;
            return false;
        }
    }

    /**
     * pass bubble location
     */
    public boolean isBubbleInsideClosableArea(Bubble bubble) {
        return distanceRatioFromBubbleToClosableArea(bubble) == 0.0f;
    }

    public boolean isFingerInsideClosableArea(float x, float y) {
        // because x and y of the finger which we got from MotionEvent included the cutout and the nav-bar, so we must exclude them
        float mX = x - Utils.sez.getSafePaddingLeft();
        float mY = y - Utils.sez.getSafePaddingTop();
        return distanceRatioFromLocationToClosableArea(mX, mY) == 0.0f;
    }

    /**
     * @param bubble the bubble
     * @return x=0.0 means the bubble is inside the closable area, 0.0 < x < 1.0 means outside
     */
    private float distanceRatioFromBubbleToClosableArea(Bubble bubble) {
        int bWidth = bubble.getRoot().getWidth();
        int bHeight = bubble.getRoot().getHeight();
        Pair<Integer, Integer> location = ViewUtils.getXYOnScreen(bubble.getRoot());
        int x = location.first;
        int y = location.second;

        int centerBubbleX = x + bWidth / 2;
        int centerBubbleY = y + bHeight / 2;

        double distanceToBubble = XMath.distance(
                centerCloseBubbleX, centerCloseBubbleY,
                centerBubbleX, centerBubbleY
        );
        
        double distanceRatio = (double) distanceToClosePx / distanceToBubble;
        if (distanceRatio > 1) {
            return 0f;
        }
        return (float) (1 - distanceRatio);
    }

    /**
     * Important: the x and y is the location after exclude the nav bar and cutout
     */
    private float distanceRatioFromLocationToClosableArea(float x, float y) {
        double distanceToLocation = XMath.distance(
                centerCloseBubbleX, centerCloseBubbleY,
                x, y
        );
        
        double distanceRatio = (double) distanceToClosePx / distanceToLocation;
        if (distanceRatio > 1) {
            return 0f;
        }
        return (float) (1 - distanceRatio);
    }

    /**
     * x and y are point which exclude status bar
     */
    public void followBubble(int x, int y, Bubble bubble) {
        int bWidth = bubble.getRoot().getWidth();
        int bHeight = bubble.getRoot().getHeight();

        float distanceRatio = distanceRatioFromBubbleToClosableArea(bubble);

        if (distanceRatio == 0.0f) {
            stickToBubble(x, y, bWidth, bHeight);
        } else {
            int centerBubbleX = x + bWidth / 2;
            boolean isXOnTheLeft = centerBubbleX < halfSafeScreenWidth;

            WindowManager.LayoutParams layoutParams = getLayoutParams();
            
            if (isXOnTheLeft) {
                layoutParams.x = (int) (baseX - ((halfSafeScreenWidth - centerBubbleX) * distanceRatio) / 5);
            } else {
                layoutParams.x = (int) (baseX + ((centerBubbleX - halfSafeScreenWidth) * distanceRatio) / 5);
            }

            int flyHeight = (int) (((Utils.sez.getFullHeight() - y) * distanceRatio) / 10);
            if (flyHeight > LIMIT_FLY_HEIGHT) {
                flyHeight = LIMIT_FLY_HEIGHT;
            }
            layoutParams.y = baseY - flyHeight;

            update();
        }
    }

    //endregion ------------------------------------------------------------------------------------

    private void stickToBubble(int x, int y, int bWidth, int bHeight) {
        int midBubbleX = x + bWidth / 2;
        int midBubbleY = y + bHeight / 2;

        WindowManager.LayoutParams layoutParams = getLayoutParams();
        layoutParams.x = midBubbleX - halfWidthPx;
        layoutParams.y = midBubbleY - halfHeightPx;

        update();
    }
}