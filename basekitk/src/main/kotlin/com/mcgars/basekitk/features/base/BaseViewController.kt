package com.gars.percents.base

import android.app.Activity
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.features.base.DecoratorListener
import com.mcgars.basekitk.tools.hideKeyboard
import java.util.*

/**
 * Created by gars on 29.12.2016.
 */
abstract class BaseViewController(args: Bundle? = null) : Controller(args) {

    val decorators: MutableList<DecoratorListener> = ArrayList()

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        Log.d("onCreateView", javaClass.canonicalName)
        return inflater.inflate(getLayoutId(), container, false).apply {
            decorators.forEach { it.onViewInited(this) }
            onReady(this)
        }
    }

    fun <D : DecoratorListener> addDecorator(decoratorListener: D): D {
        addLifecycleListener(decoratorListener)
        decorators.add(decoratorListener)
        return decoratorListener
    }

    /**
     * Call when view is ready to work
     */
    protected abstract fun onReady(view: View)

    override fun onAttach(view: View) {
        setTitle()
        super.onAttach(view)
    }

    protected open fun setTitle() {
        var parentController = parentController
        while (parentController != null) {
            if (parentController is BaseViewController && parentController.getTitle() != null) {
                return
            }
            parentController = parentController.parentController
        }

        // set title
        getTitle()?.run { activity?.title = this }
                ?: getTitleInt().run { if (this != 0) activity?.setTitle(this) }
    }

    protected open fun getTitle(): String? = null

    protected open fun getTitleInt() = 0

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)
        activity.hideKeyboard(activity.window.decorView)
    }

    final override fun addLifecycleListener(lifecycleListener: LifecycleListener) {
        super.addLifecycleListener(lifecycleListener)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        decorators.clear()
    }
}