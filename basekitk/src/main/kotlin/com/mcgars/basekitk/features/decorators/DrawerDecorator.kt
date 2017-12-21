package com.mcgars.basekitk.features.decorators

import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.drawer.BaseDrawerAdapter
import com.mcgars.basekitk.features.drawer.DrawerToolHelper
import com.mcgars.basekitk.features.drawer.OnViewLoadPageListener
import com.mcgars.basekitk.tools.find

/**
 * Created by gars on 07.08.17.
 */
open class DrawerDecorator(
        private val viewController: BaseViewController,
        private val pageListener: OnViewLoadPageListener,
        private val drawerAdapter: BaseDrawerAdapter<*, *>
) : DecoratorListener() {

    /**
     * Это котроллер, который выполяет всю логику
     * по инициализации и управлению [android.support.v4.widget.DrawerLayout]
     */
    var drawerTool: DrawerToolHelper? = null

    private var drawerRouter: Router? = null

    override fun onViewCreated(view: View) {
        drawerTool = DrawerToolHelper(viewController, pageListener, drawerAdapter, viewController.toolbar)
        val contentFrameDrawer = view.find<ViewGroup>(R.id.contentFrameDrawer)
        drawerRouter = viewController.getChildRouter(contentFrameDrawer!!, this::class.java.simpleName)
    }

    fun loadPage(page: BaseViewController?) {
        page?.let {
            val transition = RouterTransaction.with(it)
            drawerRouter?.replaceTopController(transition)
        }
    }

    fun onBackPressed(): Boolean {
        return if (drawerTool?.hideDrawer() ?: false) false
        else drawerRouter?.handleBack() ?: false
    }

    /**
     * Change arrow or sandwich
     * @param arrow
     */
    fun setHomeArrow(arrow: Boolean) {
        drawerTool?.setHomeArrow(arrow)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (drawerTool?.onOptionsItemSelected(item) ?: false)
            return true

        return false
    }


}