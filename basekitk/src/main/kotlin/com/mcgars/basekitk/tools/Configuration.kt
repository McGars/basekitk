package com.mcgars.basekitk.tools

import android.app.Activity
import android.content.res.Configuration
import android.view.Surface


fun Activity.getAppOrientation(): Int {
    val orientation = resources.configuration.orientation
    val rotation = windowManager.defaultDisplay.rotation

    when (rotation) {
        Surface.ROTATION_0, Surface.ROTATION_90 -> when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> return Configuration.ORIENTATION_PORTRAIT
            Configuration.ORIENTATION_LANDSCAPE -> return Configuration.ORIENTATION_LANDSCAPE
        }
        Surface.ROTATION_180, Surface.ROTATION_270 -> when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> return Configuration.ORIENTATION_LANDSCAPE
            Configuration.ORIENTATION_LANDSCAPE -> return Configuration.ORIENTATION_LANDSCAPE
        }
    }
    return Configuration.ORIENTATION_LANDSCAPE
}