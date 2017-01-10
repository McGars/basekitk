package com.mcgars.basekitk.features.recycler

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gars.percents.base.BaseViewController
import com.mcgars.basekitk.R
import com.mcgars.basekitk.tools.find
import java.util.*

/**
 * Created by Владимир on 22.09.2015.
 */
abstract class BaseRecycleViewController(args: Bundle? = null) : BaseViewController(args) {
    protected open var layoutManager: LinearLayoutManager? = null
        get() = recyclerView?.layoutManager as LinearLayoutManager?
    var recyclerView: RecyclerView? = null
        private set
    private var hasMoreItems: Boolean = false
    private var isLoading: Boolean = false
    private var adapter: RecyclerView.Adapter<*>? = null
    private val allList = ArrayList<Any>()
    protected var page = DEFAULT_FIRST_PAGE

    override fun getLayoutId() = R.layout.basekit_view_recycler

    protected open fun initLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        recyclerView = view.find(R.id.recycleView)
        recyclerView?.layoutManager = initLayoutManager()
        initLoading()
    }

    protected fun initLoading() {
        recyclerView!!.addOnScrollListener(loadingScroll)
    }

    abstract fun loadData()

    internal var loadingScroll: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
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
                loadData()
            }
        }
    }

    fun hasMoreItems(b: Boolean) {
        isLoading = false
        hasMoreItems = b

        if(adapter is ListRecycleAdapter<*,*>)
            (adapter as ListRecycleAdapter<*,*>).showLoader(b)
    }

    abstract fun getAdapter(list: List<*>): RecyclerView.Adapter<*>

    protected fun prepareData(list: List<Any>, hasmore: Boolean) {
        if (activity == null)
            return

        if (page == DEFAULT_FIRST_PAGE)
            allList.clear()

        list.indices.mapTo(allList) { list[it] }

        if (adapter == null) {
            adapter = getAdapter(allList)
            setAdapter(adapter!!)
        } else {
            if (page > DEFAULT_FIRST_PAGE && list.size > 0)
                adapter!!.notifyItemRangeChanged(allList.size - list.size, list.size)
            else
                adapter!!.notifyDataSetChanged()
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
        if (allList.size > position && adapter is HeaderRecyclerAdapter<*,*>) {
            (adapter as HeaderRecyclerAdapter<*,*>).removeItemByPosition(position)
        }
    }

    protected fun removeItem(item: Any) {
        adapter?.let {
            (adapter as HeaderRecyclerAdapter<Any,*>).removeItem(item)
        }
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView!!.adapter = adapter
    }

    fun getAdapter(): RecyclerView.Adapter<*>? {
        return adapter
    }

    fun onDestroyView() {
        adapter = null
    }

    companion object {
        private var CLOSE_APLICATION: Boolean = false
        var DEFAULT_FIRST_PAGE = 0
        var LIMIT = 10
    }
}
