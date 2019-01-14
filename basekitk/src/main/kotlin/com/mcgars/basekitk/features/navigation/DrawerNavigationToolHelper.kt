package com.mcgars.basekitk.features.navigation

import androidx.annotation.MenuRes
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.drawer.DrawerTool
import com.mcgars.basekitk.features.drawer.OnViewLoadPageListener

/**
 * Created by Владимир on 16.04.2015.
 */
open class DrawerNavigationToolHelper(
        private val viewController: BaseViewController,
        pageListener: OnViewLoadPageListener,
        toolbar: Toolbar? = null) : NavigationView.OnNavigationItemSelectedListener {

    open var drawerTool = DrawerTool(viewController, pageListener, toolbar)

    @MenuRes
    var menuResourceId: Int = 0

    var selectedId = -1
        protected set

    var mNavigationView: NavigationView? = null

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
        mNavigationView = viewController.view?.findViewById(navigationViewId)
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

    fun syncState() {
        drawerTool.drawerToggle.syncState()
    }
}
