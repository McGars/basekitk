package com.mcgars.basekitkotlin.draweractivity

import android.os.Bundle
import com.mcgars.basekitk.features.navigation.BaseDrawerNavigationActivity
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.controller.HelloAc

/**
 * Created by Владимир on 11.01.2017.
 */
class DrawerActivity : BaseDrawerNavigationActivity<HelloAc>() {

    override fun initDrawerTool() = DrawerTool(this, toolbar)

    override fun initActivityController() = HelloAc(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerTool?.loadPage(R.id.page1)
    }
}