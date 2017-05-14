package com.mcgars.basekitkotlin

import android.os.Bundle
import com.mcgars.basekitk.features.simple.BaseKitActivity
import com.mcgars.basekitkotlin.controller.HelloAc

class MainActivity : BaseKitActivity<HelloAc>() {

    override fun getLayoutId() = R.layout.activity_main

    override fun initActivityController() = HelloAc(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}
