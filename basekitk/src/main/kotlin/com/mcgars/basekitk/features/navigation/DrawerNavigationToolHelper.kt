package com.mcgars.basekitk.features.navigation

import android.support.annotation.MenuRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.features.base.BaseKitActivity
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.drawer.DrawerTool
import com.mcgars.basekitk.features.drawer.OnViewLoadPageListener
import com.mcgars.basekitk.tools.hideKeyboard

/**
 * Created by Владимир on 16.04.2015.
 */
open class DrawerNavigationToolHelper(
        private val viewController: BaseViewController,
        pageListener: OnViewLoadPageListener,
        toolbar: Toolbar? = null) : NavigationView.OnNavigationItemSelectedListener {

    open var drawerTool = DrawerTool(viewController, pageListener, toolbar)

    var menuResourceId: Int = 0
        set(@MenuRes value) {
            field = value
        }

    var selectedId = -1
        protected set

    protected var mNavigationView: NavigationView? = null

    /**
     * List view were set items

     * @return
     */
    protected val navigationViewId: Int
        get() = R.id.nav_view

    /**
     * Левая менюшка
     */
    fun initDrawer() {
        mNavigationView = viewController.view?.findViewById(navigationViewId) as NavigationView
        mNavigationView?.setNavigationItemSelectedListener(this)
        if (menuResourceId != 0) {
            mNavigationView?.menu?.clear()
            mNavigationView?.inflateMenu(menuResourceId)
        }

        drawerTool.drawerToggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (selectedId == item.itemId) {
            drawerTool.drawerLayout?.closeDrawers()
            return true
        }
        selectedId = item.itemId

        loadPage(selectedId)
        return true
    }

    fun loadPage(pageId: Int) {
        selectedId = pageId
        mNavigationView?.setCheckedItem(pageId)
        drawerTool.loadPage(pageId)
    }

    fun clearSelect() {
        val itemMenu = mNavigationView?.menu?.findItem(selectedId)
        itemMenu?.isChecked = false
        selectedId = -1
    }

    val size: Int
        get() = mNavigationView?.menu?.size() ?: 0


    fun onOptionsItemSelected(item: MenuItem) = drawerTool.drawerToggle.onOptionsItemSelected(item)

    fun hideDrawer() = drawerTool.hideDrawer()

    fun setHomeArrow(arrow: Boolean) = drawerTool.setHomeArrow(arrow)

    fun syncState()
    {
        drawerTool.drawerToggle.syncState()
    }
}
