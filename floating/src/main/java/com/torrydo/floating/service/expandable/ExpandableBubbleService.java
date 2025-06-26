package com.torrydo.floating.service.expandable;

import android.content.Context;
import android.content.res.Configuration;
import androidx.annotation.Nullable;
import com.torrydo.floating.CloseBubbleBehavior;
import com.torrydo.floating.FloatingBubbleListener;
import com.torrydo.floating.ServiceInteractor;
import com.torrydo.floating.Utils;
import com.torrydo.floating.bubble.FloatingBottomBackground;
import com.torrydo.floating.bubble.FloatingBubble;
import com.torrydo.floating.bubble.FloatingCloseBubble;
import com.torrydo.floating.service.FloatingBubbleService;

public abstract class ExpandableBubbleService extends FloatingBubbleService {

    private Context context;

    // 0: nothing
    // 1: bubble
    // 2: expanded-bubble
    private int state = 0;

    private FloatingBubble bubble;
    private FloatingBubble expandedBubble;
    private FloatingCloseBubble closeBubble;
    private FloatingBottomBackground bottomBackground;

    ServiceInteractor serviceInteractor = null;

    private void createBubbles(Context context, BubbleBuilder bubbleBuilder, ExpandedBubbleBuilder expandedBuilder) {
        this.context = context;

        if (bubbleBuilder != null) {
            // setup bubble ------------------------------------------------------------------------
            bubble = new FloatingBubble(
                    context,
                    bubbleBuilder.forceDragging,
                    false, // containCompose removed for Java-only
                    bubbleBuilder.listener,
                    bubbleBuilder.onDispatchKeyEvent,
                    bubbleBuilder.triggerClickablePerimeterPx
            );

            if (bubbleBuilder.bubbleView != null) {
                bubble.getRootGroup().addView(bubbleBuilder.bubbleView);
            }

            bubble.mListener = new CustomBubbleListener(
                    bubble,
                    bubbleBuilder.isAnimateToEdgeEnabled,
                    bubbleBuilder.behavior,
                    true,
                    bubbleBuilder.triggerClickablePerimeterPx
            );
            bubble.setLayoutParams(bubbleBuilder.defaultLayoutParams());
            bubble.isDraggable = bubbleBuilder.isBubbleDraggable;

            // setup close-bubble ------------------------------------------------------------------
            if (bubbleBuilder.closeView != null) {
                closeBubble = new FloatingCloseBubble(
                        context,
                        bubbleBuilder.closeView,
                        bubbleBuilder.distanceToClosePx,
                        bubbleBuilder.closeBubbleBottomPaddingPx
                );
                if (bubbleBuilder.closeBubbleStyle != null) {
                    closeBubble.getLayoutParams().windowAnimations = bubbleBuilder.closeBubbleStyle;
                }
            }

            // setup bottom-background
            if (bubbleBuilder.isBottomBackgroundEnabled) {
                bottomBackground = new FloatingBottomBackground(context);
            }
        }

        // setup expanded-bubble
        if (expandedBuilder != null) {
            expandedBubble = new FloatingBubble(
                    context,
                    false, // forceDragging
                    false, // containCompose removed for Java-only
                    null,
                    expandedBuilder.onDispatchKeyEvent,
                    1f
            );
            
            if (expandedBuilder.expandedView != null) {
                expandedBubble.getRootGroup().addView(expandedBuilder.expandedView);
            }

            expandedBubble.mListener = new CustomBubbleListener(
                    expandedBubble,
                    false, // isAnimateToEdgeEnabled
                    CloseBubbleBehavior.FIXED_CLOSE_BUBBLE,
                    false, // isCloseBubbleEnabled
                    1f
            );
            expandedBubble.setLayoutParams(expandedBuilder.defaultLayoutParams());
            if (expandedBuilder.expandedBubbleStyle != null) {
                expandedBubble.getLayoutParams().windowAnimations = expandedBuilder.expandedBubbleStyle;
            }
            expandedBubble.isDraggable = expandedBuilder.isDraggable;
        }
    }

    // region public methods

    @Override
    public void removeAll() {
        if (bubble != null) {
            bubble.remove();
        }
        if (closeBubble != null) {
            closeBubble.remove();
        }
        if (expandedBubble != null) {
            expandedBubble.remove();
        }
        if (bottomBackground != null) {
            bottomBackground.remove();
        }
    }

    public FloatingBubble getBubble() {
        return bubble;
    }

    public FloatingBubble getExpandedBubble() {
        return expandedBubble;
    }

    /**
     * show expanded bubble, hide bubble
     */
    public void expand() {
        if (expandedBubble == null) return;

        state = 2;
        if (bubble != null) {
            bubble.remove();
        }
        if (closeBubble != null) {
            closeBubble.remove();
        }
        if (bottomBackground != null && bottomBackground.isShowing) {
            bottomBackground.remove();
        }
        expandedBubble.show();
    }

    /**
     * show bubble, hide expanded bubble
     */
    public void minimize() {
        if (bubble == null) return;

        state = 1;
        if (expandedBubble != null) {
            expandedBubble.remove();
        }
        bubble.show();
    }

    @Override
    public void setup() {
        createBubbles(this, configBubble(), configExpandedBubble());
        minimize();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Utils.sez.with(getApplicationContext());
    }

    // endregion

    // region abstract methods

    @Nullable
    public abstract BubbleBuilder configBubble();

    @Nullable
    public abstract ExpandedBubbleBuilder configExpandedBubble();

    // endregion

    // Inner class for bubble listener
    class CustomBubbleListener implements FloatingBubbleListener {
        private final FloatingBubble targetBubble;
        private final boolean isAnimateToEdgeEnabled;
        private final CloseBubbleBehavior closeBehavior;
        private final boolean isCloseBubbleEnabled;
        private final float triggerClickableAreaPx;

        public CustomBubbleListener(FloatingBubble targetBubble, boolean isAnimateToEdgeEnabled, 
                                  CloseBubbleBehavior closeBehavior, boolean isCloseBubbleEnabled, 
                                  float triggerClickableAreaPx) {
            this.targetBubble = targetBubble;
            this.isAnimateToEdgeEnabled = isAnimateToEdgeEnabled;
            this.closeBehavior = closeBehavior;
            this.isCloseBubbleEnabled = isCloseBubbleEnabled;
            this.triggerClickableAreaPx = triggerClickableAreaPx;
        }

        @Override
        public void onFingerDown(float x, float y) {
            if (isCloseBubbleEnabled && closeBubble != null) {
                closeBubble.show();
                if (bottomBackground != null) {
                    bottomBackground.show();
                }
            }
        }

        @Override
        public void onFingerMove(float x, float y) {
            if (isCloseBubbleEnabled && closeBubble != null && closeBubble.ableToInteract) {
                closeBubble.tryAttractBubble(targetBubble, x, y);
                closeBubble.followBubble((int) x, (int) y, targetBubble);
            }
        }

        @Override
        public void onFingerUp(float x, float y) {
            if (isCloseBubbleEnabled && closeBubble != null) {
                if (closeBubble.isBubbleInsideClosableArea(targetBubble)) {
                    if (serviceInteractor != null) {
                        serviceInteractor.requestStop();
                    } else {
                        stopSelf();
                    }
                    return;
                }
                closeBubble.remove();
                if (bottomBackground != null) {
                    bottomBackground.remove();
                }
            }

            if (isAnimateToEdgeEnabled) {
                targetBubble.animateIconToEdge();
            }
        }
    }
}