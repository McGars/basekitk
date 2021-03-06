package com.mcgars.basekitk.tools.behavior

import android.content.Context
import com.google.android.material.appbar.AppBarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View

/**
 * Created by Altarix on 01.04.2016.
 */
class KitSimpleActivityBehavior : AppBarLayout.ScrollingViewBehavior {
    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val bool = super.onDependentViewChanged(parent, child, dependency)
//        child?.let {
//            if (child.id == R.id.contentFrame && child.paddingTop >= 0) {
//                if (Build.VERSION.SDK_INT > 10)
//                    child.translationY = (-child.paddingTop).toFloat()
//                child.setPadding(0, 0, 0, 0)
//                parent?.requestLayout()
//            }
//        }
        return bool
    }
}
