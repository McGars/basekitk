package com.mcgars.basekitk.animatorHandlers

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler

/**
 * An [AnimatorChangeHandler] that will perform a circular reveal
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
open class CircularRevealChangeHandler : AnimatorChangeHandler {

    private var cx: Int = 0
    private var cy: Int = 0

    var locationX: Int = -1
    var locationY: Int = -1

    var halfPosition = DEFAULT

    constructor()

    constructor(duration: Long = DEFAULT_ANIMATION_DURATION, removesFromViewOnPush: Boolean = true) : super(duration, removesFromViewOnPush)

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
    constructor(fromView: View, containerView: View, duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION, removesFromViewOnPush: Boolean = true) : super(duration, removesFromViewOnPush) {
        calculateSize(fromView, containerView)
    }

    /**
     * Constructor that will create a circular reveal from the center point passed in.
     * @param cx The center's x-axis
     * *
     * @param cy The center's y-axis
     * *
     * @param removesFromViewOnPush If true, the view being replaced will be removed from the view hierarchy on pushes
     */
    constructor(cx: Int, cy: Int, removesFromViewOnPush: Boolean) : this(cx, cy, AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION, removesFromViewOnPush)

    fun calculateSize(fromView: View?, containerView: View) {

        if (fromView == null)
            return

        val fromLocation = IntArray(2)
        fromView.getLocationInWindow(fromLocation)

        val containerLocation = IntArray(2)
        containerView.getLocationInWindow(containerLocation)

        val relativeLeft = fromLocation[0] - containerLocation[0]
        val relativeTop = fromLocation[1] - containerLocation[1]

        cx = fromView.width / 2 + relativeLeft
        cy = fromView.height / 2 + relativeTop
    }

    /**
     * Constructor that will create a circular reveal from the center point passed in.
     * @param cx The center's x-axis
     * *
     * @param cy The center's y-axis
     * *
     * @param duration The duration of the animation
     * *
     * @param removesFromViewOnPush If true, the view being replaced will be removed from the view hierarchy on pushes
     */
    constructor(cx: Int, cy: Int, duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION, removesFromViewOnPush: Boolean = true) : super(duration, removesFromViewOnPush) {
        this.cx = cx
        this.cy = cy
    }

    override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
        calculateSize(from ?: to, container)
        val radius = Math.max(cx.toDouble() * 2, cy.toDouble() * 2).toFloat()
        val (x, y) = calculateHalfPosition(from, to)

        if (isPush && to != null) {
            return ViewAnimationUtils.createCircularReveal(to, x, y, 0f, radius)
        } else if (!isPush && from != null) {
            return ViewAnimationUtils.createCircularReveal(from, x, y, radius, 0f)
        }
        return AnimatorSet()
    }

    private fun calculateHalfPosition(from: View?, to: View?): Pair<Int, Int> {

        val width = (from ?: to)?.width ?: 0
        val height = (from ?: to)?.height ?: 0

        return when (halfPosition) {
            TOP_CENTER -> Pair(width / 2, 0)
            RIGHT_CENTER -> Pair(width, height / 2)
            BOTTOM_CENTER -> Pair(width / 2, height)
            LEFT_CENTER -> Pair(0, height / 2)
            else -> Pair(if (locationX >= 0) locationX else cx, if (locationY >= 0) locationY else cy)
        }
    }

    override fun resetFromView(from: View) {}

    override fun saveToBundle(bundle: Bundle) {
        super.saveToBundle(bundle)
        bundle.putInt(KEY_CX, cx)
        bundle.putInt(KEY_CY, cy)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        cx = bundle.getInt(KEY_CX)
        cy = bundle.getInt(KEY_CY)
    }

    companion object {

        private val KEY_CX = "CircularRevealChangeHandler.cx"
        private val KEY_CY = "CircularRevealChangeHandler.cy"

        const val DEFAULT = 0
        const val TOP_CENTER = 1
        const val RIGHT_CENTER = 2
        const val BOTTOM_CENTER = 3
        const val LEFT_CENTER = 4
    }

}
