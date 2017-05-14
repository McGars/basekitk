package com.mcgars.basekitkotlin.draweractivity

import android.support.v7.widget.Toolbar
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.features.navigation.DrawerNavigationToolHelper
import com.mcgars.basekitk.features.simple.BaseKitActivity
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.controller.HelloAc
import com.mcgars.basekitkotlin.sample.EmptyViewController

/**
 * Created by gars on 11.01.2017.
 */
class DrawerTool(
        context: BaseKitActivity<HelloAc>,
        toolbar: Toolbar?) : DrawerNavigationToolHelper(context, toolbar) {

    override fun getViewController(pageId: Int): Controller? {
        return when(pageId) {
            R.id.page1 -> EmptyViewController("page 1")
            R.id.page2 -> EmptyViewController("page 2")
            else -> null
        }
    }

    override fun getMenuId() = R.menu.drawer_menu
}