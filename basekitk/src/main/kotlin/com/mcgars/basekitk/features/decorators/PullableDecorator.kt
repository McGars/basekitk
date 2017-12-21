package com.mcgars.basekitk.features.decorators

import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.tools.WrapperUiTool
import com.mcgars.basekitk.tools.colorAttr
import com.mcgars.basekitk.tools.find

/**
 * Created by gars on 09.01.2017.
 * Extends view and add pullable behavior
 */
class PullableDecorator private constructor(
        val viewId: Int,
        var swipeLayoutId: Int,
        val onRefreshListener: SwipeRefreshLayout.OnRefreshListener) : DecoratorListener() {

    var swipeRefreshLayout: SwipeRefreshLayout? = null

    fun setColor(@ColorInt color: Int) {
        swipeRefreshLayout?.setColorSchemeColors(color)
    }

    override fun onViewCreated(view: View) {
        if (viewId != 0) {
            view.find<View?>(viewId)?.let {
                swipeRefreshLayout = SwipeRefreshLayout(view.context)
                swipeRefreshLayout!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                WrapperUiTool(it, swipeRefreshLayout!!).insert()
            }
        } else if (swipeLayoutId != 0) {
            swipeRefreshLayout = view.find(swipeLayoutId)
        }

        swipeRefreshLayout?.apply {
            setOnRefreshListener(onRefreshListener)
            setColorSchemeColors(context.colorAttr(R.attr.colorAccent))
        }

    }

    fun showLoader(show: Boolean) = with(swipeRefreshLayout) {
        this?.post({ isRefreshing = show })
    }

    override fun preDestroyView(controller: Controller, view: View) {
        swipeRefreshLayout?.run {
            setOnRefreshListener(null)
            isRefreshing = false
            destroyDrawingCache()
            clearAnimation()
        }
    }

    companion object {
        /**
         * Id view which become attached to swipe
         */
        fun forView(@IdRes viewId: Int, refreshListener: () -> Unit): PullableDecorator {
            return PullableDecorator(viewId, 0, SwipeRefreshLayout.OnRefreshListener { refreshListener() })
        }

        /**
         * Id swipeLayout which swipe behavior
         */
        fun forSwipeLayout(@IdRes viewId: Int, refreshListener: () -> Unit): PullableDecorator {
            return PullableDecorator(0, viewId, SwipeRefreshLayout.OnRefreshListener { refreshListener() })
        }
    }
}