package com.mcgars.basekitk.features.navigation

import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.features.simple.BaseKitActivity
import com.mcgars.basekitk.tools.hideKeyboard

/**
 * Created by Владимир on 16.04.2015.
 */
abstract class DrawerNavigationToolHelper(
        context: BaseKitActivity<ActivityController<*>>,
        toolbar: Toolbar? = null) : NavigationView.OnNavigationItemSelectedListener {
    protected var activity: BaseKitActivity<ActivityController<*>> = context
    var drawerLayout: DrawerLayout? = null
        private set
    var drawerToggle: ActionBarDrawerToggle? = null
        private set
    private var launchId = -1
    var selectedId = -1
        protected set
    private var originListener: View.OnClickListener? = null
    protected var mNavigationView: NavigationView? = null

    init {
        initDrawer(toolbar)
    }

    /**
     * Wrapper drawer layout

     * @return
     */
    protected val drawerLayoutId: Int
        get() = R.id.drawer_layout

    /**
     * List view were set items

     * @return
     */
    protected val navigationViewId: Int
        get() = R.id.nav_view

    protected abstract val menuId: Int

    /**
     * Левая менюшка
     */
    private fun initDrawer(toolbar: Toolbar? = null) {
        /**
         * Кнопка нажималась слева сверху
         */

        toolbar?.run {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            activity.supportActionBar?.setHomeButtonEnabled(true)
            drawerToggle = toolbar.tag as ActionBarDrawerToggle
        }

        if (drawerLayout == null) {
            drawerLayout = activity.findViewById(drawerLayoutId) as DrawerLayout
            mNavigationView = activity.findViewById(R.id.nav_view) as NavigationView
            //            setGravityForNavigation(false);
            mNavigationView?.setNavigationItemSelectedListener(this)
            if (menuId != 0) {
                mNavigationView?.menu?.clear()
                mNavigationView?.inflateMenu(menuId)
            }
        }

        if (drawerToggle == null) {
            drawerToggle = initDrawerToggle(toolbar)
            toolbar?.tag = drawerToggle
        }

        if (originListener == null) {
            originListener = drawerToggle?.toolbarNavigationClickListener
        } else {
            drawerToggle?.toolbarNavigationClickListener = originListener
        }
        drawerLayout?.setDrawerListener(drawerToggle)
        drawerToggle?.syncState()
    }

    //    // navigationview открывается слева или справа
    //    public void setGravityForNavigation(boolean open) {
    //        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
    //        layoutParams.gravity = EaApplication.isLeft ? GravityCompat.START : GravityCompat.END;
    //        mNavigationView.setLayoutParams(layoutParams);
    //        if (open)
    //            drawerLayout.openDrawer(EaApplication.isLeft ? GravityCompat.START : GravityCompat.END);
    //    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (selectedId == item.itemId) {
            drawerLayout!!.closeDrawers()
            return true
        }
        selectedId = item.itemId

        chooseItem(selectedId)
        return true
    }

    val size: Int
        get() = mNavigationView?.menu?.size() ?: 0

    private fun initDrawerToggle(toolbar: Toolbar?): ActionBarDrawerToggle {
        return object : ActionBarDrawerToggle(activity, drawerLayout,
                toolbar, R.string.app_name, R.string.app_name) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            override fun onDrawerClosed(view: View?) {
                super.onDrawerClosed(view)
                activity.invalidateOptionsMenu()
                syncState()
                loadPage()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                activity.apply {
                    invalidateOptionsMenu()
                    syncState()
                    hideKeyboard(drawerView)
                }
            }
        }
    }

    fun chooseItem(id: Int, closeDrawer: Boolean = true) {
        launchId = id
        if (closeDrawer && drawerLayout!!.isDrawerOpen(GravityCompat.START))
            drawerLayout!!.closeDrawers()
        else {
            loadPage()
        }
    }

    fun setSelected(id: Int) {
        selectedId = id
        mNavigationView?.setCheckedItem(id)
    }

    open fun loadPage(pageId: Int, closeDrawer: Boolean = true) {
        if (closeDrawer /*&& drawerLayout.isDrawerOpen(EaApplication.isLeft ? GravityCompat.START : GravityCompat.END)*/)
            drawerLayout!!.closeDrawers()

        selectedId = pageId

        val frag = getViewController(pageId) ?: return
        activity.clearBackStack()
        activity.loadPage(frag)
    }

    fun clearSelect() {
        val itemMenu = mNavigationView?.menu?.findItem(selectedId)
        itemMenu?.isChecked = false
        selectedId = -1
    }

    abstract fun getViewController(pageId: Int): Controller?

    private fun loadPage() {
        if (launchId == -1) return
        loadPage(launchId)
        launchId = -1
    }

    fun onOptionsItemSelected(item: MenuItem) = drawerToggle!!.onOptionsItemSelected(item)

    fun hideDrawer(): Boolean {
        drawerLayout!!.closeDrawers()
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
            return true
        } else if (drawerLayout!!.isDrawerOpen(GravityCompat.END)) {
            drawerLayout!!.closeDrawer(GravityCompat.END)
            return true
        }
        return false
    }

    fun setHomeArrow(arrow: Boolean) {
        drawerToggle?.isDrawerIndicatorEnabled = !arrow
        if (arrow) {
            drawerToggle?.toolbarNavigationClickListener = View.OnClickListener { activity.onBackPressed() }
        } else {
            drawerToggle?.toolbarNavigationClickListener = originListener
        }
    }

    fun setCustomToolbar(toolbar: Toolbar?) {
        initDrawer(toolbar)
    }

}
