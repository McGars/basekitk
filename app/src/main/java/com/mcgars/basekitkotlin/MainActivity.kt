package com.mcgars.basekitkotlin

import android.os.Bundle
import android.view.View
import com.mcgars.basekitk.features.base.BaseKitActivity
import com.mcgars.basekitkotlin.controller.HelloAc
import com.mcgars.basekitkotlin.list.ListViewController

class MainActivity : BaseKitActivity<HelloAc>() {

    override fun initActivityController() = HelloAc(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDoubleBackPressedToExit(true)
        loadPage(ListViewController())
    }
}
