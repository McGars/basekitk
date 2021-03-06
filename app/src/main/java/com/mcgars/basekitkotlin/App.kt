package com.mcgars.basekitkotlin

import android.app.Application
import com.mcgars.basekitk.config.KitBuilder
import com.mcgars.basekitk.config.KitConfiguration

/**
 * Created by gars on 16.05.17.
 */
class App : Application(), KitConfiguration {

    val kitConfig =
            KitBuilder.getInstance()
                    // need for [com.mcgars.basekitk.features.simple.SimpleActivity]
                    .build()

    override fun getConfiguration() = kitConfig

}