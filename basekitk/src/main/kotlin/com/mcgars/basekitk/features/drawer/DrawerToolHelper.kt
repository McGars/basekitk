package com.mcgars.basekitk.features.drawer

import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.decorators.DecoratorListener

/**
 * Created by Владимир on 16.04.2015.
 */
open class DrawerToolHelper(
        protected val viewController: BaseViewController,
        protected val pageListener: OnViewLoadPageListener,
        protected val drawerAdapter: BaseDrawerAdapter<*, *>,
        toolbar: Toolbar? = null) : DecoratorListener() {

    init {
        initDrawer()
    }

    open val drawerTool = DrawerTool(viewController, pageListener, toolbar)

    val drawerList: ListView? by lazy { viewController.view?.findViewById<ListView>(drawerListViewId) }

    /**
     * List view were set items
     * @return
     */
    protected val drawerListViewId: Int
        get() = R.id.left_drawer

    /**
     * Левая менюшка
     */
    private fun initDrawer() {
        drawerList?.adapter = drawerAdapter
        // Set the list's click listener
        drawerList?.onItemClickListener = drawerClickItemListener
        drawerTool.drawerToggle.syncState()
    }


    /**
     * Listener click drawer
     */
    internal var drawerClickItemListener: AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener { _, view, position, _ ->
                val itemId = view.id
                if (itemId == drawerAdapter.selectedId) {
                    drawerTool.drawerLayout?.closeDrawers()
                    return@OnItemClickListener
                }

                chooseItem(itemId)
                drawerAdapter.setSelected(itemId, position)
            }

    fun chooseItem(id: Int) {
        setSelected(id)
        drawerTool.loadPage(id)
    }

    fun setSelected(id: Int) = drawerAdapter.setSelected(id)

    fun onOptionsItemSelected(item: MenuItem) = drawerTool.drawerToggle.onOptionsItemSelected(item)

    fun hideDrawer() = drawerTool.hideDrawer()

    fun setHomeArrow(arrow: Boolean) = drawerTool.setHomeArrow(arrow)

}
