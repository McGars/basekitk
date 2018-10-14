package com.mcgars.basekitkotlin.list

import android.view.View
import com.mcgars.basekitk.features.recycler.BaseRecycleViewController
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.collapsingtoolbar.CollapsingToolbarViewController
import com.mcgars.basekitkotlin.drawer.DrawerNavigationViewController
import com.mcgars.basekitkotlin.loaderController.LoaderViewController
import com.mcgars.basekitkotlin.pullable.PullableViewController
import com.mcgars.basekitkotlin.sample.ArrowToolbarViewController
import com.mcgars.basekitkotlin.sample.EmptyViewController
import com.mcgars.basekitkotlin.tabs.TabsViewController

/**
 * Created by gars on 13.05.2017.
 */
class ListViewController : BaseRecycleViewController() {

    override fun getTitleInt() = R.string.menu

    override fun onReady(view: View) {
        loadData(DEFAULT_FIRST_PAGE)
    }

    /**
     * See base class
     */
    override fun loadData(page: Int) {
        prepareData(mutableListOf<MenuItem>().apply {
            add(MenuItem(DRAWER, activity?.getString(R.string.drawer_title)))
            add(MenuItem(PULLABLE, activity?.getString(R.string.pullabe_title)))
            add(MenuItem(TABS, activity?.getString(R.string.tabs_title)))
            add(MenuItem(LOADER, activity?.getString(R.string.loader_title)))
            add(MenuItem(SIMPLE, activity?.getString(R.string.simple_activity)))
            add(MenuItem(ARROW, activity?.getString(R.string.arrow)))
            add(MenuItem(COLLAPSINGTOOLBAR, activity?.getString(R.string.collapsing_toolbar)))
        })
    }

    override fun getAdapter(list: MutableList<*>) = MainMenuAdapter(activity!!, list as MutableList<MenuItem>) { item, _ ->
        when (item.id) {
            DRAWER -> loadPage(DrawerNavigationViewController())
            PULLABLE -> loadPage(PullableViewController())
            TABS -> loadPage(TabsViewController())
            LOADER -> loadPage(LoaderViewController())
            SIMPLE -> pageController.startActivity(EmptyViewController::class.java)
            ARROW -> loadPage(ArrowToolbarViewController())
            COLLAPSINGTOOLBAR -> loadPage(CollapsingToolbarViewController())
        }
    }

    companion object {
        val DRAWER = 0
        val PULLABLE = 1
        val TABS = 2
        val LOADER = 3
        val SIMPLE = 4
        val ARROW = 5
        val COLLAPSINGTOOLBAR = 6
    }
}