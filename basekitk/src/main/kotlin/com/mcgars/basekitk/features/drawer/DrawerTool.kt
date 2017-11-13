package com.mcgars.basekitk.features.drawer

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.tools.hideKeyboard

/**
 * Created by gars on 07.08.17.
 */
open class DrawerTool(
        protected val viewController: BaseViewController,
        protected val pageListener: OnViewLoadPageListener,
        toolbar: Toolbar? = null) {

    private var launchId = -1

    /**
     * Wrapper drawer wrapperLayout
     * @return
     */
    protected val drawerLayoutId: Int
        get() = R.id.drawer_layout

    val drawerLayout: DrawerLayout? by lazy { viewController.view?.findViewById<DrawerLayout>(drawerLayoutId) }

    val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(viewController.activity, drawerLayout,
            toolbar, R.string.app_name, R.string.app_name) {
        /**
         * Called when a drawer has settled in a completely closed state.
         */
        override fun onDrawerClosed(view: View?) {
            super.onDrawerClosed(view)
            (viewController.activity as AppCompatActivity).invalidateOptionsMenu()
            syncState()
            loadPageSync()
        }

        /**
         * Called when a drawer has settled in a completely open state.
         */
        override fun onDrawerOpened(drawerView: View?) {
            super.onDrawerOpened(drawerView)
            (viewController.activity as AppCompatActivity).invalidateOptionsMenu()
            syncState()
            viewController.activity.hideKeyboard(drawerView)
        }
    }

    private var originListener: View.OnClickListener? = null

    init {
        (viewController.activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
        originListener = drawerToggle.toolbarNavigationClickListener
        drawerLayout?.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    /**
     * Show selected page
     */
    open fun loadPage(pageId: Int) {
        launchId = pageId
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawers()
        } else {
            loadPageSync()
        }
    }

    private fun loadPageSync() {
        if (launchId == -1) return
        pageListener.showPage(launchId)
        launchId = -1
    }

    fun setHomeArrow(arrow: Boolean) {
        drawerToggle.isDrawerIndicatorEnabled = !arrow
        if (arrow) {
            drawerToggle.toolbarNavigationClickListener = View.OnClickListener { viewController.activity?.onBackPressed() }
        } else {
            drawerToggle.toolbarNavigationClickListener = originListener
        }
    }

    fun hideDrawer(): Boolean {
        return if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout!!.closeDrawers()
            true
        } else false
    }
}