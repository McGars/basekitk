package com.mcgars.basekitkotlin

import android.os.Bundle
import com.mcgars.basekitk.features.base.BaseKitActivity
import com.mcgars.basekitk.tools.Timer
import com.mcgars.basekitkotlin.list.ListViewController
import com.mcgars.basekitkotlin.splash.SplashViewController

class MainActivity : BaseKitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDoubleBackPressedToExit(true)

        if (savedInstanceState == null) {
            loadPage(SplashViewController())

            Timer(2000) {
                loadPage(ListViewController(), false)
            }.start()
        }
    }
}
