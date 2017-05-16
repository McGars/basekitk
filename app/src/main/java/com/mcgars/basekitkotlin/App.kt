package com.mcgars.basekitkotlin

import android.app.Application
import com.mcgars.basekitk.tools.pagecontroller.PageController
import com.mcgars.basekitkotlin.controller.HelloAc

/**
 * Created by gars on 16.05.17.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // need for [com.mcgars.basekitk.features.simple.SimpleActivity]
        PageController.baseActivityController = HelloAc::class.java
    }
}