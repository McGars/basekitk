package com.mcgars.basekitkotlin.pullable

import android.view.View
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.decorators.PullableDecorator
import com.mcgars.basekitk.tools.Timer
import com.mcgars.basekitk.tools.snack
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.decorator.ToolbarColorDecorator
import com.mcgars.basekitk.animatorHandlers.CircularRevealChangeHandlerCompat

/**
 * Created by gars on 13.05.2017.
 */
class PullableViewController : BaseViewController() {

    override fun getTitleInt() = R.string.pullabe_title

    override fun getLayoutId() = R.layout.view_pullable

    init {
        // animation
        overridePushHandler(CircularRevealChangeHandlerCompat())
        overridePopHandler(CircularRevealChangeHandlerCompat())
        addDecorator(ToolbarColorDecorator(this))
    }

    /**
     * Find swipe refresh view and attach listeners
     */
    val pullDecorator = addDecorator(PullableDecorator.forSwipeLayout(R.id.swipeRefresh) {
        startRefresh()
    })

    fun startRefresh() {
        Timer(1000) {
            pullDecorator.showLoader(false)
        }.start()
    }

    override fun onReady(view: View) {
        view.post { view.snack(R.string.pull_to_refresh) }
    }
}