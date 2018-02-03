package com.mcgars.basekitk.animatorHandlers

import android.animation.Animator
import android.view.View

/**
 * Hide previous page if used
 * [AnimatorChangeHandler.removesFromViewOnPush]
 * because cpu overdraw
 */
class AnimatorChangeHandlerWrapper(
        private val animator: Animator,
        private val from: View?,
        private val to: View?
) {
    init {
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                from?.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                to?.visibility = View.VISIBLE
            }

        })
    }
}