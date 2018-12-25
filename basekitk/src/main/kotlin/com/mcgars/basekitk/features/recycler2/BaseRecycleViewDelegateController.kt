package com.mcgars.basekitk.features.recycler2

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.decorators.DecoratorListener
import com.mcgars.basekitk.tools.find
import com.mcgars.basekitk.tools.gone
import com.mcgars.basekitk.tools.visible
import java.util.*

/**
 * Created by Владимир on 22.09.2015.
 */
abstract class BaseRecycleViewDelegateController(args: Bundle? = null) : BaseViewController(args) {
    protected open var layoutManager: LinearLayoutManager? = null
        get() = recyclerView?.layoutManager as LinearLayoutManager?
    var recyclerView: RecyclerView? = null
        private set
    private var hasMoreItems: Boolean = false
    private var isLoading: Boolean = false
    private var adapter: RecyclerView.Adapter<*>? = null
    val allList = mutableListOf<Any>()
    protected var page = DEFAULT_FIRST_PAGE
    /**
     * If user on the first page, then all list will be cleared when new data arrived
     */
    var clearOnFirstPage = true

    private var loadingScroll: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {

            val visibleItemCount = layoutManager!!.childCount
            val totalItemCount = layoutManager!!.itemCount
            val pastVisiblesItems = layoutManager!!.findFirstVisibleItemPosition()

            val lastVisibleItem = visibleItemCount + pastVisiblesItems
            if (!isLoading && hasMoreItems) {
                if (lastVisibleItem != totalItemCount)
                    return
                page++
                isLoading = true
                loadData(page)
            }
        }
    }

    init {
        addDecorator(object : DecoratorListener() {
            override fun postCreateView(controller: Controller, view: View) {
                recyclerView = view.find(R.id.recycleView)
                recyclerView?.layoutManager = initLayoutManager()
                initLoading()
            }
        })
    }

    override fun getLayoutId() = R.layout.basekit_view_recycler

    /**
     * Drop page to default
     */
    fun setDefaultPage() {
        page = DEFAULT_FIRST_PAGE
    }

    fun getAdapter() = adapter

    protected open fun initLayoutManager() = LinearLayoutManager(activity)

    /**
     * Load more items when list scrolls to end
     */
    protected fun initLoading() {
        recyclerView?.addOnScrollListener(loadingScroll)
    }

    /**
     * Call when [adapter] is null, in first init
     */
    abstract fun getAdapter(list: MutableList<*>): RecyclerView.Adapter<*>

    /**
     * Calls when list scrolls to end
     */
    abstract fun loadData(page: Int)

    /**
     * Indicate have more items and when list scrolls to end call [loadData]
     */
    @CallSuper
    open fun hasMoreItems(has: Boolean) {
        isLoading = false
        hasMoreItems = has

        if (adapter is KitAdapter<*>) {
            showAdapterLoader((adapter as KitAdapter<Any>).getDelegates())
        }
    }

    /**
     * You must call this when data is ready to show for UI
     */
    protected fun prepareData(
            list: List<Any>,
            hasmore: Boolean = false,
            customNotify: ((RecyclerView.Adapter<*>) -> Unit)? = null
    ) {
        if (activity == null)
            return

        if (clearOnFirstPage && page == DEFAULT_FIRST_PAGE)
            allList.clear()

        list.indices.mapTo(allList) { list[it] }

        if (adapter == null) {
            recyclerView?.gone()
            adapter = getAdapter(allList).also {
                setAdapter(it)
            }
            recyclerView?.visible()
        } else {
            if (customNotify != null)
                customNotify.invoke(adapter!!)
            else if ((!clearOnFirstPage || page > DEFAULT_FIRST_PAGE) && list.isNotEmpty()) {
                if (adapter is AdapterDelegateHeader<*>) {
                    (adapter as AdapterDelegateHeader<*>).notifyItemsInserted(allList.size - list.size, list.size)
                } else {
                    adapter?.notifyItemRangeChanged(allList.size - list.size, list.size)
                }
            } else {
                adapter?.notifyItemRangeChanged(allList.size - list.size, list.size)
            }
        }
        hasMoreItems(hasmore)
    }

    /**
     * Use this only if you need recreate adapter
     */
    protected fun destroyAdapter() {
        adapter = null
    }

    protected fun removeItem(position: Int) {
        if (allList.size > position && adapter is KitAdapter<*>) {
            (adapter as KitAdapter<*>).removeItemByPosition(position)
        }
    }

    protected fun removeItem(item: Any) {
        (adapter is KitAdapter<*>).let {
            (adapter as KitAdapter<Any>).removeItem(item)
        }
    }

    protected fun addItem(item: Any) {
        (adapter is KitAdapter<*>).let {
            (adapter as KitAdapter<Any>).addItem(item)
        }
    }

    protected fun addItem(position: Int, item: Any) {
        (adapter is KitAdapter<*>).let {
            (adapter as KitAdapter<Any>).addItem(position, item)
        }
    }

    private fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView?.adapter = adapter
    }

    /*
    * Try show pagination loader
    */
    private fun showAdapterLoader(delegates: List<AdapterDelegate<Any>>?) {
        delegates?.forEach {
            if (it is AdapterViewLoader<*>) {
                it.showLoader(hasMoreItems)
            }
        }
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        // When we go to another page and back to this page list disappears, fix this
        adapter = null
    }

    companion object {
        var DEFAULT_FIRST_PAGE = 0
        var LIMIT = 10
    }
}
