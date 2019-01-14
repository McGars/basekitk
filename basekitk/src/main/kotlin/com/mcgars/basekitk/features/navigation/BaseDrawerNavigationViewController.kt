package com.mcgars.basekitk.features.drawer

import android.os.Bundle
import androidx.annotation.MenuRes
import com.google.android.material.navigation.NavigationView
import android.view.MenuItem
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.decorators.DrawerNavigationDecorator

/**
 * Created by gars on 07.08.17.
 */
abstract class BaseDrawerNavigationViewController(args: Bundle? = null) : BaseViewController(args), OnViewLoadPageListener {

    override fun getLayoutId() = R.layout.basekit_view_navigation

    abstract fun getViewController(pageId: Int): BaseViewController?

    /**
     * Drawer navigation
     */
    fun getNavigation(): NavigationView? = drawerTool.drawerTool?.mNavigationView

    override var isCustomLayout = true

    @MenuRes
    abstract fun getMenuId(): Int

    /**
     * Это котроллер, который выполяет всю логику
     * по инициализации и управлению [android.support.v4.widget.DrawerLayout]
     */
    val drawerTool = addDecorator(DrawerNavigationDecorator(this, this, getMenuId()))

    /**
     * Show page and selected item in drawer menu
     */
    fun loadPage(pageId: Int) {
        view?.post {
            drawerTool.loadPage(pageId)
        }
    }

    /**
     * Show page
     */
    override fun showPage(pageId: Int) {
        drawerTool.loadPage(getViewController(pageId)?.apply {
            isCustomLayout = true
        })
    }

    override fun handleBack(): Boolean {
        return if (drawerTool.onBackPressed()) true
        else super.handleBack()
    }

    override fun setHomeArrow(arrow: Boolean) {
        drawerTool.setHomeArrow(arrow)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerTool.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

}