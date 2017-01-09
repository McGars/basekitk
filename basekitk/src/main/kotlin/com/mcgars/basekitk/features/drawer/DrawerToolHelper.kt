package com.mcgars.basekitk.features.drawer

import android.support.annotation.IdRes
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.simple.BaseKitActivity
import com.mcgars.basekitk.tools.hideKeyboard

/**
 * Created by Владимир on 16.04.2015.
 */
abstract class DrawerToolHelper<T : BaseKitActivity<*>>(
        protected var activity: T,
        toolbar: Toolbar? = null) {

    val drawerLayout: DrawerLayout? by lazy { activity.findViewById(drawerLayoutId) as DrawerLayout }
    val drawerList: ListView? by lazy { activity.findViewById(drawerListViewId) as ListView }
    val drawerContainer: View? by lazy { activity.findViewById(drawerContainerViewId) }

    var drawerToggle: ActionBarDrawerToggle? = null
        private set
    /**
     * @return DrawerAdapter or null
     */
    var adaper: BaseDrawerAdapter<*, *>? = null
        private set
    private var launchId = -1
    private var originListener: View.OnClickListener? = null

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
    protected val drawerListViewId: Int
        get() = R.id.left_drawer

    protected val drawerContainerViewId: Int
        @IdRes
        get() = drawerListViewId

    /**
     * Левая менюшка
     */
    private fun initDrawer(toolbar: Toolbar? = null) {
        /**
         * Кнопка нажималась слева сверху
         */
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeButtonEnabled(true)

        drawerToggle = toolbar?.tag as ActionBarDrawerToggle

        if (drawerToggle == null) {
            drawerToggle = initDrawerToggle(toolbar)
            toolbar?.tag = drawerToggle
        }

        if (adaper == null) {
            adaper = drawerAdapter
            // Set the adapter for the list view
            drawerList?.adapter = adaper
            // Set the list's click listener
            drawerList?.onItemClickListener = drawerClickItemListener
            originListener = drawerToggle?.toolbarNavigationClickListener
        } else {
            drawerToggle?.toolbarNavigationClickListener = originListener
        }
        drawerLayout?.setDrawerListener(drawerToggle)
        drawerToggle?.syncState()
    }

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
                activity.invalidateOptionsMenu()
                syncState()
                activity.hideKeyboard(drawerView)
            }
        }
    }


    abstract val drawerAdapter: BaseDrawerAdapter<*, *>
    /**
     * Listener click drawer
     */
    internal var drawerClickItemListener: AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val itemId = view.id
                if (itemId == adaper?.selectedId) {
                    drawerLayout?.closeDrawers()
                    return@OnItemClickListener
                }

                chooseItem(itemId)
                adaper?.setSelected(itemId, position)
            }

    fun chooseItem(id: Int) {
        launchId = id

        if (drawerLayout!!.isDrawerOpen(GravityCompat.START))
            drawerLayout!!.closeDrawers()
        else {
            adaper!!.setSelected(id, BaseDrawerAdapter.NON_SELECTED)
            loadPage()
        }
    }

    fun setSelected(id: Int) {
        adaper?.setSelected(id)
    }

    /**
     * Открытие корневой страницы. Бекстек очищается
     */
    fun loadPage(pageId: Int) {

        drawerLayout?.run { if (isDrawerOpen(GravityCompat.START)) closeDrawers() }

        val view = getViewController(pageId) ?: return
        activity.clearBackStack()
        activity.loadPage(view)
    }

    abstract fun getViewController(pageId: Int): Controller?

    private fun loadPage() {
        if (launchId == -1) return
        loadPage(launchId)
        launchId = -1
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle?.onOptionsItemSelected(item) ?: false
    }

    fun hideDrawer(): Boolean {
        if (drawerLayout!!.isDrawerOpen(drawerContainer!!)) {
            drawerLayout!!.closeDrawer(drawerContainer)
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

    init {
        initDrawer(toolbar)
    }
}
