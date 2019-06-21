package com.mcgars.basekitk.features.simple

import android.os.Bundle
import com.mcgars.basekitk.features.base.BaseKitActivity

/**
 * Launched this activity by
 * [com.mcgars.basekitk.tools.pagecontroller.PageController.startActivity]
 */
open class SimpleActivity : BaseKitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageController.initParamsFromActivity()
        loadViewController()
    }

    protected open fun loadViewController() {
        pageController.loadPage()
    }

    override fun isShowArrow() = alwaysArrow
}
