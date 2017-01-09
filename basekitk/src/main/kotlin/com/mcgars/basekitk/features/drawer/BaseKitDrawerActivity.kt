package com.mcgars.basekitk.features.drawer

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.features.simple.BaseKitActivity

/**
 * Created by Владимир on 22.06.2015.
 */
abstract class BaseKitDrawerActivity<C : ActivityController<*>> : BaseKitActivity<C>() {

    /**
     * Это котроллер, который выполяет всю логику
     * по инициализации и управлению [android.support.v4.widget.DrawerLayout]
     */
    protected var drawerTool: DrawerToolHelper<out BaseKitActivity<C>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer()
    }

    /**
     * init DrawerToolHelper in your project
     * @return
     */
    abstract fun initDrawerTool(): DrawerToolHelper<out BaseKitActivity<C>>

    /**
     * Левая менюшка
     */
    fun initDrawer() {
        drawerTool = initDrawerTool()
    }

    override fun getLayoutId() = R.layout.basekit_activity_drawer

    override fun loadPage(id: Int) {
        drawerTool?.loadPage(id)
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

    /**
     * Change arrow or sandwich
     * @param arrow
     */
    override fun setHomeArrow(arrow: Boolean) {
        drawerTool?.setHomeArrow(arrow) ?: super.setHomeArrow(arrow)
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
            drawerTool?.setCustomToolbar(toolbar!!)
            return true
        }
        return false
    }
}
