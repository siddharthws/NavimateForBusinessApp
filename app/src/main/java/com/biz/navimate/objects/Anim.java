package com.biz.navimate.objects;

import android.animation.Animator;
import android.view.View;

import com.biz.navimate.interpolators.BaseInterpolator;

/**
 * Animation data object which specifies the type of animation and other info about it.
 */

public class Anim
{
    // ----------------------- Constants ----------------------- //
    // Animation Types
    public static final int TYPE_INVALID                    = 0;
    public static final int TYPE_FADE_IN                    = 1;
    public static final int TYPE_FADE_OUT                   = 2;
    public static final int TYPE_SLIDE                      = 3;
    public static final int TYPE_ROTATE_WITH_FADE_IN        = 4;
    public static final int TYPE_ROTATE_WITH_FADE_OUT       = 5;

    public static class Base
    {
        // ----------------------- Globals ----------------------- //
        public int                  type            = TYPE_INVALID;
        public View                 view            = null;
        public BaseInterpolator     interpolator    = null;
        public Animator.AnimatorListener listener   = null;

        // ----------------------- Constructor ----------------------- //
        public Base(int type, View view, BaseInterpolator interpolator, Animator.AnimatorListener listener)
        {
            this.type           = type;
            this.view           = view;
            this.interpolator   = interpolator;
            this.listener       = listener;
        }
    }

    public static class Slide extends Base
    {
        // ----------------------- Constants ----------------------- //
        // Slide Axis Types
        public static final int SLIDE_AXIS_X      = 1;
        public static final int SLIDE_AXIS_Y      = 2;

        // ----------------------- Globals ----------------------- //
        public float    startF      = 0.0f;
        public float    endF        = 0.0f;
        public int      slideAxis   = SLIDE_AXIS_X;

        // ----------------------- Constructor ----------------------- //
        public Slide(View view, BaseInterpolator interpolator, float startF, float endF, int slideAxis, Animator.AnimatorListener listener)
        {
            super(TYPE_SLIDE, view, interpolator, listener);
            this.startF           = startF;
            this.endF           = endF;
            this.slideAxis      = slideAxis;
        }
    }
}
