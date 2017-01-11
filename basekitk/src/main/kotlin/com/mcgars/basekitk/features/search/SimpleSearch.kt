package com.mcgars.basekitk.features.search

import android.support.annotation.IdRes
import android.support.annotation.MenuRes
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.mcgars.basekitk.R

/**
 * Created by Феофилактов on 10.10.2015.
 */
class SimpleSearch(private val listener: SearchView.OnQueryTextListener) {

    internal var hint: String? = null
    var searchView: SearchView? = null
        private set
    private var item: MenuItem? = null

    fun onCreate(menu: Menu, inflater: MenuInflater, @IdRes itemId: Int = R.id.basekit_item_search, @MenuRes searchMenu: Int = R.menu.basekit_search_menu) {
        inflater.inflate(searchMenu, menu)
        item = menu.findItem(itemId)
        searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView?.setOnQueryTextListener(listener)
        invalidateHint()
    }

    fun showSearchText(show: Boolean) {
        searchView?.isIconified = !show
    }

    fun showSearch(show: Boolean) {
        item?.isVisible = show
    }

    fun setHint(shint: String) {
        this.hint = shint
        invalidateHint()
    }

    private fun invalidateHint() {
        searchView?.queryHint = hint
    }
}
