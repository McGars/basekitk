package com.mcgars.basekitk.tools.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import com.mcgars.basekitk.R
import com.mcgars.basekitk.tools.colorAttr
import com.mcgars.basekitk.tools.getStatusBarHeight


/**
 * In some case fitsystem not work correctly, this draw behind
 * status bar rect with colorAccent
 */
class StatusBarCoordinatorLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private var statusBarHeight = 0f

    private var paint = Paint().apply {
        strokeWidth = 0f
    }

    init {
        // make us to use onDraw method
        setWillNotDraw(false)

        statusBarHeight = context.getStatusBarHeight().toFloat()
        // background color of status bar rect
        paint.color = context.colorAttr(R.attr.colorPrimary)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width > 0) {
            canvas.drawRect(0f, 0f, width.toFloat(), statusBarHeight, paint)
        }
    }
}