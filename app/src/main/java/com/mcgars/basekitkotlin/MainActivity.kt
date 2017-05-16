package com.mcgars.basekitkotlin

import android.os.Bundle
import com.mcgars.basekitk.features.simple.BaseKitActivity
import com.mcgars.basekitkotlin.controller.HelloAc
import com.mcgars.basekitkotlin.list.ListViewController

class MainActivity : BaseKitActivity<HelloAc>() {

    override fun initActivityController() = HelloAc(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPage(ListViewController())
    }
}
