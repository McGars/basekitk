package com.mcgars.basekitkotlin.tabs

import android.view.View
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.tools.pagecontroller.ExTabs
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.sample.EmptyViewController
import kotlinx.android.synthetic.main.view_pager.view.*

/**
 * Created by Владимир on 12.01.2017.
 */
@ExTabs
class TabsViewController : BaseViewController() {

    override fun getLayoutId() = R.layout.view_pager

    override fun getTitleInt() = R.string.feature_toolbar_tabs

    val pagerAdapter = object : RouterPagerAdapter(this) {

        override fun configureRouter(router: Router, position: Int) {
            router.pushController(RouterTransaction.with(EmptyViewController("page: $position")))
        }

        override fun getCount() = 3

        override fun getPageTitle(position: Int) = "Page " + position
    }


    override fun onReady(view: View) {
        view.viewPager.adapter = pagerAdapter
        tabs?.setupWithViewPager(view.viewPager)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        view.viewPager.adapter = null
    }

}