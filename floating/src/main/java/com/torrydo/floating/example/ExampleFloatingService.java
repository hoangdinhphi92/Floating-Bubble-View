package com.torrydo.floating.example;

import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.Nullable;
import com.torrydo.floating.CloseBubbleBehavior;
import com.torrydo.floating.FloatingBubbleListener;
import com.torrydo.floating.helper.ViewHelper;
import com.torrydo.floating.service.expandable.BubbleBuilder;
import com.torrydo.floating.service.expandable.ExpandableBubbleService;
import com.torrydo.floating.service.expandable.ExpandedBubbleBuilder;

/**
 * Example service demonstrating Java API usage of the floating module
 * This demonstrates the same functionality as the original MyServiceJava 
 * but using the new Java-only floating module
 */
public class ExampleFloatingService extends ExpandableBubbleService {

    @Nullable
    @Override
    public BubbleBuilder configBubble() {
        // Create bubble view using ViewHelper (same API as original)
        View imgView = ViewHelper.fromDrawable(this, com.torrydo.floating.R.drawable.ic_rounded_blue_diamond, 60, 60);
        imgView.setOnClickListener(view -> expand()); // Expand when clicked

        return new BubbleBuilder(this)
                .bubbleView(imgView)
                .bubbleStyle(com.torrydo.floating.R.style.default_bubble_style)
                .bubbleDraggable(true)
                .forceDragging(true)
                .closeBubbleView(ViewHelper.fromDrawable(this, com.torrydo.floating.R.drawable.ic_close_bubble))
                .closeBubbleStyle(com.torrydo.floating.R.style.default_close_bubble_style)
                .distanceToClose(100)
                .closeBehavior(CloseBubbleBehavior.FIXED_CLOSE_BUBBLE)
                .startLocation(100, 100)
                .enableAnimateToEdge(true)
                .bottomBackground(false)
                .addFloatingBubbleListener(new FloatingBubbleListener() {
                    @Override
                    public void onFingerDown(float x, float y) {
                        // Handle finger down
                    }

                    @Override
                    public void onFingerUp(float x, float y) {
                        // Handle finger up
                    }

                    @Override
                    public void onFingerMove(float x, float y) {
                        // Handle finger move
                    }
                });
    }

    @Nullable
    @Override
    public ExpandedBubbleBuilder configExpandedBubble() {
        // Create expanded view - simple layout for demonstration
        View expandedView = LayoutInflater.from(this).inflate(com.torrydo.floating.R.layout.bubble, null);
        
        return new ExpandedBubbleBuilder(this)
                .expandedView(expandedView)
                .draggable(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Start in minimized state like the original example
        minimize();
    }
}