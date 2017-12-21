package ru.mos.helloworldk.features.animatorHandlers

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler

open class CircularRevealChangeHandlerCompat : CircularRevealChangeHandler {

    constructor()

    constructor(duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION, removesFromViewOnPush: Boolean = true) : super(duration, removesFromViewOnPush)

    constructor(fromView: View, containerView: View) : super(fromView, containerView)

    /**
     * Constructor that will create a circular reveal from the center of the fromView parameter.
     * @param fromView The view from which the circular reveal should originate
     * *
     * @param containerView The view that hosts fromView
     * *
     * @param duration The duration of the animation
     * *
     * @param removesFromViewOnPush If true, the view being replaced will be removed from the view hierarchy on pushes
     */
    constructor(
            fromView: View,
            containerView: View,
            duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
            removesFromViewOnPush: Boolean = true
    ) : super(
            fromView,
            containerView,
            duration,
            removesFromViewOnPush)

    override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return super.getAnimator(container, from, to, isPush, toAddedToContainer)
        } else {
            val animator = AnimatorSet()
            if (to != null && toAddedToContainer) {
                animator.play(ObjectAnimator.ofFloat<View>(to, View.ALPHA, 0f, 1f))
            }

            if (from != null) {
                animator.play(ObjectAnimator.ofFloat<View>(from, View.ALPHA, 0f))
            }

            return animator
        }
    }
}
