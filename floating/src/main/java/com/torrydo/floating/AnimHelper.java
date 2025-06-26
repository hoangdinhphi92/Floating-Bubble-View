package com.torrydo.floating;

import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

final class AnimHelper {
    
    private AnimHelper() {
        // Utility class
    }

    /**
     * default minimum value of the property to be animated
     */
    private static final float MIN_VALUE = 0f;
    private static final float DEFAULT_FRICTION = 1.1f;

    // event ---------------------------------------------------------------------------------------

    public interface Event {
        default void onStart() {}
        default void onEnd() {}
        default void onCancel() {}
        default void onUpdate(float value) {}
        default void onUpdatePoint(float x, float y) {}
    }

    // func ----------------------------------------------------------------------------------------

    public static SpringAnimation startSpringX(
            float startValue,
            float finalPosition,
            Event event,
            float stiffness,
            float dampingRatio) {
        SpringAnimation springAnim = new SpringAnimation(new FloatValueHolder());

        SpringForce springForce = new SpringForce();
        springAnim.setStartValue(startValue);
        springForce.setFinalPosition(finalPosition);
        springForce.setStiffness(stiffness);
        springForce.setDampingRatio(dampingRatio);
        springAnim.setSpring(springForce);

        springAnim.addUpdateListener((animation, value, velocity) -> event.onUpdate(value));
        springAnim.addEndListener((animation, canceled, value, velocity) -> event.onEnd());

        event.onStart();
        springAnim.start();

        return springAnim;
    }

    public static SpringAnimation startSpringX(
            float startValue,
            float finalPosition,
            Event event) {
        return startSpringX(startValue, finalPosition, event, 
                SpringForce.STIFFNESS_LOW, SpringForce.DAMPING_RATIO_LOW_BOUNCY);
    }

    public static SpringAnimation animateSpringPath(
            float startX,
            float startY,
            float endX,
            float endY,
            Event event,
            float stiffness,
            float dampingRatio) {
        float xDistance = endX - startX;
        float yDistance = endY - startY;

        SpringAnimation springAnim = new SpringAnimation(new FloatValueHolder());

        SpringForce springForce = new SpringForce();
        springForce.setStiffness(stiffness);
        springForce.setDampingRatio(dampingRatio);

        if (yDistance > xDistance) {
            springAnim.setStartValue(startY);
            springForce.setFinalPosition(endY);

            springAnim.addUpdateListener((animation, value, velocity) -> {
                float ratio = 1 - (endY - value) / yDistance;
                event.onUpdatePoint(
                    startX + xDistance * ratio,
                    value
                );
            });
        } else {
            springAnim.setStartValue(startX);
            springForce.setFinalPosition(endX);

            springAnim.addUpdateListener((animation, value, velocity) -> {
                float ratio = (value - startX) / xDistance;
                event.onUpdatePoint(
                    value,
                    startY + yDistance * ratio
                );
            });
        }

        springAnim.setSpring(springForce);
        springAnim.addEndListener((animation, canceled, value, velocity) -> event.onEnd());

        event.onStart();
        springAnim.start();

        return springAnim;
    }

    public static SpringAnimation animateSpringPath(
            float startX,
            float startY,
            float endX,
            float endY,
            Event event) {
        return animateSpringPath(startX, startY, endX, endY, event,
                SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
    }
}