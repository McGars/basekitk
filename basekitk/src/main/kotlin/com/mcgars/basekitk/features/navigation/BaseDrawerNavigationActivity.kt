package com.mcgars.basekitk.features.navigation

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.features.simple.BaseKitActivity

/**
 * Created by Владимир on 21.03.2016.
 */
abstract class BaseDrawerNavigationActivity<C : ActivityController<*>> : BaseKitActivity<C>() {

    override fun getLayoutId() = R.layout.basekit_activity_navigation

    var drawerTool: DrawerNavigationToolHelper? = null
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer()
    }

    /**
     * init DrawerToolHelper in your project
     * @return
     */
    abstract fun initDrawerTool(): DrawerNavigationToolHelper

    /**
     * Левая менюшка
     */
    fun initDrawer() {
        drawerTool = initDrawerTool()
    }

    override fun loadPage(id: Int) {
        if (drawerTool != null)
            drawerTool!!.loadPage(id)
    }

    override fun setAppBar(appBarLayout: Int): Boolean {
        if (super.setAppBar(appBarLayout)) {
            drawerTool?.setCustomToolbar(toolbar)
            return true
        }
        return false
    }

    override fun setAppBarDefault(): Boolean {
        if (super.setAppBarDefault()) {
            drawerTool?.setCustomToolbar(toolbar)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(drawerTool?.onOptionsItemSelected(item) ?: false)
            return true

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerTool?.hideDrawer() ?: false)
            return

        super.onBackPressed()
    }

    val drawerToggle: ActionBarDrawerToggle?
        get() = drawerTool?.drawerToggle

    override fun setHomeArrow(arrow: Boolean) {
        drawerTool?.setHomeArrow(arrow) ?: super.setHomeArrow(arrow)
    }
}
