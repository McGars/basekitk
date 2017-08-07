package com.mcgars.basekitk.features.decorators

import android.support.annotation.MenuRes
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.drawer.BaseDrawerAdapter
import com.mcgars.basekitk.features.drawer.DrawerToolHelper
import com.mcgars.basekitk.features.drawer.OnViewLoadPageListener
import com.mcgars.basekitk.features.navigation.DrawerNavigationToolHelper
import com.mcgars.basekitk.tools.find
import kotlin.concurrent.timer

/**
 * Created by gars on 07.08.17.
 */
open class DrawerNavigationDecorator (
        private val viewController: BaseViewController,
        private val pageListener: OnViewLoadPageListener,
        @MenuRes private val menu: Int) : DecoratorListener() {

    /**
     * Это котроллер, который выполяет всю логику
     * по инициализации и управлению [android.support.v4.widget.DrawerLayout]
     */
    var drawerTool: DrawerNavigationToolHelper? = null

    private var drawerRouter: Router? = null

    override fun onViewCreated(view: View) {
        val contentFrameDrawer = view.find<ViewGroup>(R.id.contentFrameDrawer)
                ?:  throw NullPointerException("Please put ChangeHandlerFrameLayout calls with id R.id.contentFrameDrawer to your xml layout")

        drawerRouter = viewController.getChildRouter(contentFrameDrawer, this::class.java.simpleName)
    }

    override fun postCreateView(controller: Controller, view: View) {
        view.post {
            drawerTool = DrawerNavigationToolHelper(viewController, pageListener, viewController.toolbar)
            drawerTool!!.menuResourceId = menu
            drawerTool!!.initDrawer()
        }
    }

    fun loadPage(pageId: Int) {
        viewController.view?.post {
            drawerTool?.loadPage(pageId)
        }
    }

    fun loadPage(page: BaseViewController?) {
        page?.let {
            it.isCustomLayout = true
            val transition = RouterTransaction.with(it)
            drawerRouter?.replaceTopController(transition)
        }
    }

    fun onBackPressed(): Boolean {
        return if (drawerTool?.hideDrawer() ?: false) true
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
        if(drawerTool?.onOptionsItemSelected(item) ?: false)
            return true

        return false
    }

    override fun postAttach(controller: Controller, view: View) {
        super.postAttach(controller, view)
        view.post {
            drawerTool?.syncState()
        }
    }


}