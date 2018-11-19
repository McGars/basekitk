package com.mcgars.basekitk.tools

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


fun View.paddingFast(
        left: Int = paddingLeft,
        top: Int = paddingTop,
        right: Int = paddingRight,
        bottom: Int = paddingBottom
) {
    setPadding(
            left,
            top,
            right,
            bottom
    )
}

/**
 * Inflate view
 */
fun <T : View> View.inflate(layout: Int, parent: ViewGroup? = null) = context.inflate<T>(layout, parent)


fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun <T : View> Array<out T>.offsetForStatusBarByMargin() {
    if (!this.iterator().hasNext()) return

    val offset = first().context.getStatusBarHeight()

    if (offset == 0) return

    forEach {
        val params = it.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = offset
    }
}

fun <T : View> Array<out T>.offsetForStatusBarByPadding() {
    if (!this.iterator().hasNext()) return

    val offset = first().context.getStatusBarHeight()

    if (offset == 0) return

    forEach {
        it.paddingFast(top = offset + it.paddingTop)
    }
}