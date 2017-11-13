package com.mcgars.basekitk.features.recycler2

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.features.custom.PlaceholderRecyclerViewAdapter
import com.mcgars.basekitk.features.decorators.DecoratorListener
import com.mcgars.basekitk.tools.find
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
    val allList = ArrayList<Any>()
    protected var page = DEFAULT_FIRST_PAGE
    /**
     * If user on the first page, then all list will be cleared when new data arrived
     */
    var clearOnFirstPage = true

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

    protected open fun initLayoutManager() = LinearLayoutManager(activity)

    /**
     * Load more items when list scrolls to end
     */
    protected fun initLoading() {
        recyclerView!!.addOnScrollListener(loadingScroll)
    }

    /**
     * Calls when list scrolls to end
     */
    abstract fun loadData(page: Int)

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

    /**
     * Indicate have more items and when list scrolls to end call [loadData]
     */
    @CallSuper
    open fun hasMoreItems(has: Boolean) {
        isLoading = false
        hasMoreItems = has

        if (adapter is AdapterDelegateHeader<*>)
            showAdapterLoader(adapter as AdapterDelegateHeader<*>)
        else if (adapter is PlaceholderRecyclerViewAdapter) {
            val originalAdapter = (adapter as PlaceholderRecyclerViewAdapter).originalAdapter
            if (originalAdapter is AdapterDelegateHeader<*>) {
                showAdapterLoader(originalAdapter)
            }
        }
    }

    /**
     * Try show pagination loader
     */
    fun showAdapterLoader(adpr: AdapterDelegateHeader<*>) {
        adpr.manager.let { manarer ->
            val size = manarer.delegates.size()
            (0..size).forEach {
                val delegate = manarer.delegates[it]
                if (delegate is AdapterViewLoader<*>) {
                    delegate.showLoader(hasMoreItems)
                }
            }
        }
    }

    /**
     * Drop page to default
     */
    fun setDefaultPage() {
        page = DEFAULT_FIRST_PAGE
    }

    /**
     * Call when [adapter] is null, in first init
     */
    abstract fun getAdapter(list: MutableList<*>): RecyclerView.Adapter<*>

    /**
     * You must call this when data is ready to show for UI
     */
    protected fun prepareData(list: List<Any>, hasmore: Boolean = false) {
        if (activity == null)
            return

        if (clearOnFirstPage && page == DEFAULT_FIRST_PAGE)
            allList.clear()

        list.indices.mapTo(allList) { list[it] }

        if (adapter == null) {
            adapter = getAdapter(allList)
            setAdapter(adapter!!)
        } else {
            if ((!clearOnFirstPage || page > DEFAULT_FIRST_PAGE) && list.isNotEmpty()) {
                if (adapter is AdapterDelegateHeader<*>) {
                    (adapter as AdapterDelegateHeader<*>).notifyItemsInserted(allList.size - list.size, list.size)
                } else {
                    adapter?.notifyItemRangeChanged(allList.size - list.size, list.size)
                }
            } else
                adapter?.notifyDataSetChanged()
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
        if (allList.size > position && adapter is AdapterDelegateHeader<*>) {
            (adapter as AdapterDelegateHeader<*>).removeItemByPosition(position)
        }
    }

    protected fun removeItem(item: Any) {
        (adapter is AdapterDelegateHeader<*>).let {
            (adapter as AdapterDelegateHeader<Any>).removeItem(item)
        }
    }

    private fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView?.adapter = adapter
    }

    fun getAdapter() = adapter

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
