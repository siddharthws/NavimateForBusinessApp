package com.biz.navimate.misc;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;

import com.biz.navimate.debug.Dbg;
import com.biz.navimate.interpolators.PowerInterpolator;
import com.biz.navimate.objects.Anim;
import com.biz.navimate.objects.Statics;

/**
 * Helper class. Exposes APIs to animate views and handles different types of animations
 */

public class AnimHelper
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "ANIM_HELPER";

    private static final int ANIM_DURATION_MS = 500;

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    // ----------------------- Globals ----------------------- //
    private Context context = null;

    // ----------------------- Constructor ----------------------- //
    public AnimHelper(Context context)
    {
        this.context = context;
    }

    // ----------------------- Overrides ----------------------- //
    // ----------------------- Public APIs ----------------------- //
    public void Animate(final Anim.Base anim)
    {
        // Get Animator
        ObjectAnimator animator = GetAnimator(anim);
        if (animator == null)
        {
            Dbg.error(TAG, "Animator not available");
            return;
        }

        // Set common properties on animator
        animator.setDuration(ANIM_DURATION_MS);
        animator.setInterpolator(anim.interpolator);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation)
            {
                switch (anim.type)
                {
                    case Anim.TYPE_FADE_IN:
                    case Anim.TYPE_ROTATE_WITH_FADE_IN:
                    {
                        anim.view.setVisibility(View.VISIBLE);
                        break;
                    }
                }

                if (anim.listener != null)
                {
                    anim.listener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                switch (anim.type)
                {
                    case Anim.TYPE_FADE_OUT:
                    case Anim.TYPE_ROTATE_WITH_FADE_OUT:
                    {
                        anim.view.setVisibility(View.GONE);
                        anim.view.setAlpha(1.0f);
                        break;
                    }
                }

                if (anim.listener != null)
                {
                    anim.listener.onAnimationEnd(animation);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                if (anim.listener != null)
                {
                    anim.listener.onAnimationCancel(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
                if (anim.listener != null)
                {
                    anim.listener.onAnimationRepeat(animation);
                }
            }
        });

        // Start Animation
        animator.start();
    }

    public void Swap(final View exitView, final View enterView)
    {
        // Start exit animation
        Animate(new Anim.Slide(exitView, new PowerInterpolator(false, 3), 0, -1 * Statics.SCREEN_SIZE.x, Anim.Slide.SLIDE_AXIS_X, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                exitView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }));

        // Start Enter Animation
        enterView.setVisibility(View.VISIBLE);
        Animate(new Anim.Slide(enterView, new PowerInterpolator(false, 3), Statics.SCREEN_SIZE.x, 0, Anim.Slide.SLIDE_AXIS_X, null));
    }

    public void SwapReverse(final View exitView, final View enterView)
    {
        // Start exit animation
        Animate(new Anim.Slide(exitView, new PowerInterpolator(false, 3), 0, Statics.SCREEN_SIZE.x, Anim.Slide.SLIDE_AXIS_X, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                exitView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }));

        // Start Enter Animation
        enterView.setVisibility(View.VISIBLE);
        Animate(new Anim.Slide(enterView, new PowerInterpolator(false, 3), -1 * Statics.SCREEN_SIZE.x, 0, Anim.Slide.SLIDE_AXIS_X, null));
    }

    // ----------------------- Private APIs ----------------------- //
    private ObjectAnimator GetAnimator(Anim.Base anim)
    {
        ObjectAnimator animator = null;

        switch (anim.type)
        {
            case Anim.TYPE_FADE_IN:
            {
                animator = ObjectAnimator.ofFloat(anim.view, "alpha", 0.0f, 1.0f);
                break;
            }
            case Anim.TYPE_FADE_OUT:
            {
                animator = ObjectAnimator.ofFloat(anim.view, "alpha", 1.0f, 0.0f);
                break;
            }
            case Anim.TYPE_SLIDE:
            {
                Anim.Slide slideAnim = (Anim.Slide) anim;
                if (slideAnim.slideAxis == Anim.Slide.SLIDE_AXIS_X)
                {
                    animator = ObjectAnimator.ofFloat(anim.view, "translationX", slideAnim.startF, slideAnim.endF);
                }
                else
                {
                    animator = ObjectAnimator.ofFloat(anim.view, "translationY", slideAnim.startF, slideAnim.endF);
                }
                break;
            }
            case Anim.TYPE_ROTATE_WITH_FADE_OUT:
            {
                PropertyValuesHolder rotate = PropertyValuesHolder.ofFloat("rotation", 0.0f, 135.0f);
                PropertyValuesHolder fade = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
                animator = ObjectAnimator.ofPropertyValuesHolder(anim.view, fade, rotate);
                break;
            }
            case Anim.TYPE_ROTATE_WITH_FADE_IN:
            {
                PropertyValuesHolder rotate = PropertyValuesHolder.ofFloat("rotation", -135.0f, 0.0f);
                PropertyValuesHolder fade = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
                animator = ObjectAnimator.ofPropertyValuesHolder(anim.view, fade, rotate);
                break;
            }
        }
        return animator;
    }
}
