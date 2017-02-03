package ru.mos.helloworldk.features.animatorHandlers

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.ViewGroup

class CircularRevealChangeHandlerCompat : CircularRevealChangeHandler {

    constructor()

    constructor(fromView: View, containerView: View) : super(fromView, containerView)

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
