package com.mcgars.basekitk.features.decorators

import android.view.View
import com.bluelinelabs.conductor.Controller

/**
 * Created by Владимир on 12.01.2017.
 */
open class DecoratorListener : Controller.LifecycleListener() {
    open fun onViewCreated(view: View) {}
}