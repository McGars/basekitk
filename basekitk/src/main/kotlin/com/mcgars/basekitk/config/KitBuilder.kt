package com.mcgars.basekitk.config

import com.mcgars.basekitk.features.base.BaseKitActivity
import com.mcgars.basekitk.features.simple.ActivityController

/**
 * Created by gars on 03.08.17.
 */
class KitBuilder private constructor() {
    private val options = KitConfig()

    fun setActivityController(ac: Class<out ActivityController>): KitBuilder {
        options.baseActivityController = ac
        return this
    }

    fun setBaseLauncherActivity(ac: Class<out BaseKitActivity<*>>): KitBuilder {
        options.baseLauncherActivity = ac
        return this
    }

    fun build() = options

    companion object {
        fun getInstance() = KitBuilder()
    }
}

class KitConfig internal constructor() {
    var baseActivityController: Class<out ActivityController>? = null
        internal set
    var baseLauncherActivity: Class<out BaseKitActivity<*>>? = null
        internal set

}