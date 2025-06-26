package com.torrydo.floating;

public interface FloatingBubbleListener {

    /**
     * the location of the finger on the screen
     */
    default void onFingerDown(float x, float y) {}

    /**
     * the location of the finger on the screen
     */
    default void onFingerUp(float x, float y) {}

    /**
     * the location of the finger on the screen
     */
    default void onFingerMove(float x, float y) {}
}