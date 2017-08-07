package com.mcgars.basekitkotlin.controller

import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.features.base.BaseKitActivity

/**
 * Created by gars on 13.05.2017.
 */
class HelloAc(activity: BaseKitActivity<HelloAc>?)
    : ActivityController<BaseKitActivity<HelloAc>>(activity) {
    constructor() : this(null)
}

