package com.mcgars.basekitk.tools

import android.view.View


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