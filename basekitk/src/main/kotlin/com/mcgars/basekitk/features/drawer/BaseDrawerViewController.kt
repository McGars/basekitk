package com.mcgars.basekitk.features.drawer

import android.os.Bundle
import android.view.MenuItem
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.decorators.DrawerDecorator

/**
 * Created by gars on 07.08.17.
 */
abstract class BaseDrawerViewController(args: Bundle? = null) : BaseViewController(args), OnViewLoadPageListener {

    override fun getLayoutId() = R.layout.basekit_view_drawer

    abstract fun initDrawerAdapter(): BaseDrawerAdapter<*, *>

    abstract fun getViewController(pageId: Int): BaseViewController?

    /**
     * Это котроллер, который выполяет всю логику
     * по инициализации и управлению [android.support.v4.widget.DrawerLayout]
     */
    val drawerTool by lazy { addDecorator(DrawerDecorator(this, this, initDrawerAdapter())) }

    override fun showPage(pageId: Int) {
        drawerTool.loadPage(getViewController(pageId))
    }

    override fun handleBack(): Boolean {
        return if (drawerTool.onBackPressed()) true
        else super.handleBack()
    }

    override fun setHomeArrow(arrow: Boolean) {
        drawerTool.setHomeArrow(arrow)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerTool.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

}