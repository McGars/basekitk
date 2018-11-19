package com.mcgars.basekitk.animatorHandlers

import android.animation.Animator
import android.view.View

/**
 * Hide previous page if used
 * [AnimatorChangeHandler.removesFromViewOnPush]
 * because cpu overdraw
 */
class AnimatorChangeHandlerWrapper : AnimationModify {

    override fun apply(animator: Animator, from: View?, to: View?) {
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