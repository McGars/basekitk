package com.mcgars.basekitk.config

import com.mcgars.basekitk.features.base.BaseKitActivity

/**
 * Created by gars on 03.08.17.
 */
class KitBuilder private constructor() {
    private val options = KitConfig()

    fun setBaseLauncherActivity(ac: Class<out BaseKitActivity>): KitBuilder {
        options.baseLauncherActivity = ac
        return this
    }

    fun setDebug(isDebug: Boolean): KitBuilder {
        options.isDebug = isDebug
        return this
    }

    fun build() = options

    companion object {
        fun getInstance() = KitBuilder()
    }
}

class KitConfig internal constructor() {
    var baseLauncherActivity: Class<out BaseKitActivity>? = null
        internal set
    var isDebug: Boolean = false
        internal set
}