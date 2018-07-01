package com.mcgars.basekitkotlin.collapsingtoolbar

import android.view.View
import com.mcgars.basekitk.animatorHandlers.CircularRevealChangeHandler
import com.mcgars.basekitk.animatorHandlers.CircularRevealChangeHandlerCompat
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.simplerecycler.SimpleListAdapter
import com.mcgars.basekitk.features.simplerecycler.SimpleListItem
import com.mcgars.basekitkotlin.R
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*

/**
 * Created by gars on 31.08.17.
 */
class CollapsingToolbarViewController : BaseViewController() {

    init {
        isCustomLayout = true
        isFitSystem = false
        overridePushHandler(CircularRevealChangeHandlerCompat().apply {
            halfPosition = CircularRevealChangeHandler.LEFT_CENTER
        })
        overridePopHandler(CircularRevealChangeHandlerCompat().apply {
            halfPosition = CircularRevealChangeHandler.LEFT_CENTER
        })
    }

    override fun getLayoutId() = R.layout.view_collapsing_toolbar

    override fun onReady(view: View) {
        view.recycleView.adapter = SimpleListAdapter(activity!!, (0..50).map {
            SimpleListItem("item $it", "description $it")
        }.toMutableList())

        view.ivBack.setImageResource(R.drawable.android)
    }
}