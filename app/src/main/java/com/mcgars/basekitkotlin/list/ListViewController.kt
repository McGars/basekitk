package com.mcgars.basekitkotlin.list

import android.content.Intent
import android.view.View
import com.mcgars.basekitk.features.recycler.BaseRecycleViewController
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.draweractivity.DrawerActivity
import com.mcgars.basekitkotlin.loaderController.LoaderViewController
import com.mcgars.basekitkotlin.pullable.PullableViewController
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
        })
    }

    override fun getAdapter(list: MutableList<*>) = MainMenuAdapter(activity!!, list as MutableList<MenuItem>) { item, position ->
        when (item.id) {
            DRAWER -> startActivity(Intent(activity, DrawerActivity::class.java))
            PULLABLE -> loadPage(PullableViewController())
            TABS -> loadPage(TabsViewController())
            LOADER -> loadPage(LoaderViewController())
        }
    }

    companion object {
        val DRAWER = 0
        val PULLABLE = 1
        val TABS = 2
        val LOADER = 3
    }
}