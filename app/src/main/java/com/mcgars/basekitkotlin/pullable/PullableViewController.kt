package com.mcgars.basekitkotlin.pullable

import android.view.View
import com.gars.percents.base.BaseViewController
import com.mcgars.basekitk.features.decorators.PullableDecorator
import com.mcgars.basekitk.tools.Timer
import com.mcgars.basekitk.tools.snack
import com.mcgars.basekitkotlin.R

/**
 * Created by gars on 13.05.2017.
 */
class PullableViewController : BaseViewController() {

    override fun getLayoutId() = R.layout.view_pullable

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
        view.snack(R.string.pull_to_refresh)
    }
}