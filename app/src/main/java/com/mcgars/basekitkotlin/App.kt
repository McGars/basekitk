package com.mcgars.basekitkotlin

import android.app.Application
import com.mcgars.basekitk.config.KitBuilder
import com.mcgars.basekitk.config.KitConfig
import com.mcgars.basekitk.config.KitConfiguration
import com.mcgars.basekitk.tools.pagecontroller.PageController
import com.mcgars.basekitkotlin.controller.HelloAc

/**
 * Created by gars on 16.05.17.
 */
class App : Application(), KitConfiguration {

    val kitConfig =
        KitBuilder.getInstance()
                // need for [com.mcgars.basekitk.features.simple.SimpleActivity]
                .setActivityController(HelloAc::class.java)
                .build()

    override fun getConfiguration() = kitConfig

}