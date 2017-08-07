package com.mcgars.basekitkotlin.drawer

import android.graphics.Color
import android.view.View
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.drawer.BaseDrawerNavigationViewController
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.sample.EmptyViewController
import ru.mos.helloworldk.features.animatorHandlers.CircularRevealChangeHandlerCompat

/**
 * Created by gars on 07.08.17.
 */
class DrawerNavigationViewController : BaseDrawerNavigationViewController() {

    init {
        // animation
        overridePushHandler(CircularRevealChangeHandlerCompat())
        overridePopHandler(CircularRevealChangeHandlerCompat())
    }

    override fun getViewController(pageId: Int): BaseViewController? {
        return when(pageId) {
            R.id.page1 -> EmptyViewController("page 1")
            R.id.page2 -> EmptyViewController("page 2")
            else -> null
        }
    }

    override fun getMenuId() = R.menu.drawer_menu

    override fun onReady(view: View) {
        loadPage(R.id.page1)
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        super.onChangeStarted(changeHandler, changeType)
        view?.setBackgroundColor(Color.WHITE)
    }

}