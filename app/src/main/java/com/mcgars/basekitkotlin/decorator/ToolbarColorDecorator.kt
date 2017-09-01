package com.mcgars.basekitkotlin.decorator

import android.view.View
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.decorators.DecoratorListener
import com.mcgars.basekitkotlin.R
import java.util.*

/**
 * dynamically set toolbar's color
 */
class ToolbarColorDecorator(private val viewController: BaseViewController) : DecoratorListener() {

    private val colors by lazy {
        viewController.activity!!.resources.getIntArray(R.array.toolbar_colors)
    }

    override fun postCreateView(controller: Controller, view: View) {
        viewController.toolbar?.setBackgroundColor(
                colors[Random().nextInt(colors.size - 1)]
        )
    }

}