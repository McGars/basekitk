package com.mcgars.basekitkotlin.splash

import android.view.View
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitkotlin.R

/**
 * Created by gars on 20.09.17.
 */
class SplashViewController : BaseViewController() {

    init {
        isCustomLayout = true
    }

    override fun getLayoutId() = R.layout.view_splash

    override fun onReady(view: View) {
        // ignore
    }
}