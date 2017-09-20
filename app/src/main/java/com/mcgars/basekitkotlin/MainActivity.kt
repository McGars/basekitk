package com.mcgars.basekitkotlin

import android.os.Bundle
import com.mcgars.basekitk.features.base.BaseKitActivity
import com.mcgars.basekitk.tools.Timer
import com.mcgars.basekitkotlin.controller.HelloAc
import com.mcgars.basekitkotlin.list.ListViewController
import com.mcgars.basekitkotlin.splash.SplashViewController

class MainActivity : BaseKitActivity<HelloAc>() {

    override fun initActivityController() = HelloAc(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDoubleBackPressedToExit(true)

        loadPage(SplashViewController())

        Timer(2000) {
            loadPage(ListViewController(), false)
        }.start()
    }
}
