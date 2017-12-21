package com.mcgars.basekitkotlin.controller

import android.app.Activity
import com.mcgars.basekitk.features.base.BaseKitActivity
import com.mcgars.basekitk.features.simple.ActivityController

/**
 * Created by gars on 13.05.2017.
 */
class HelloAc : ActivityController() {

    var context: BaseKitActivity<HelloAc>? = null

    override fun setActivity(activity: Activity) {
        context = activity as BaseKitActivity<HelloAc>
    }
}

