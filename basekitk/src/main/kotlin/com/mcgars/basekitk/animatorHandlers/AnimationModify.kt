package com.mcgars.basekitk.animatorHandlers

import android.animation.Animator
import android.view.View

/**
 * Need for modification
 */
interface AnimationModify {
    fun apply (animator: Animator, from: View?, to: View?)
}