package com.mcgars.basekitkotlin.collapsingtoolbar

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mcgars.basekitk.animatorHandlers.CircularRevealChangeHandler
import com.mcgars.basekitk.animatorHandlers.CircularRevealChangeHandlerCompat
import com.mcgars.basekitk.features.recycler2.AdapterDelegateHeader
import com.mcgars.basekitk.features.recycler2.BaseRecycleViewDelegateController
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.sample.EmptyViewController
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*

/**
 * Created by gars on 31.08.17.
 */
class CollapsingToolbarViewController : BaseRecycleViewDelegateController() {

    init {
        isCustomLayout = true
        overridePushHandler(CircularRevealChangeHandlerCompat().apply {
            halfPosition = CircularRevealChangeHandler.LEFT_CENTER
        })
        overridePopHandler(CircularRevealChangeHandlerCompat().apply {
            halfPosition = CircularRevealChangeHandler.LEFT_CENTER
        })
    }

    override fun getLayoutId() = R.layout.view_collapsing_toolbar

    override fun onReady(view: View) {
        prepareData((0..50).map {
            SimpleListDelegate.Item("item $it", "description $it")
        })

        view.ivBack.setImageResource(R.drawable.android)
    }

    override fun getAdapter(list: MutableList<*>): RecyclerView.Adapter<*> {
        return AdapterDelegateHeader(list as MutableList<Any>).apply {
            addDelegate(SimpleListDelegate(::onItemSelected))
        }
    }

    override fun loadData(page: Int) {
    }

    private fun onItemSelected(item: SimpleListDelegate.Item) {
        loadPage(EmptyViewController(item.title).apply {
            overridePushHandler(CircularRevealChangeHandlerCompat())
            overridePopHandler(CircularRevealChangeHandlerCompat())
        })
    }
}