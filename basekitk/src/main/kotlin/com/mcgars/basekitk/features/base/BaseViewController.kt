package com.gars.percents.base

import android.app.Activity
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.tools.hideKeyboard
import com.mcgars.basekitk.tools.inflate

/**
 * Created by gars on 29.12.2016.
 */
abstract class BaseViewController(args: Bundle? = null) : Controller(args) {

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        Log.d("onCreateView", javaClass.canonicalName)
        return inflater.inflate(getLayoutId(), container, false).apply { onViewCreated(this) }
    }

    fun <D : LifecycleListener> addDecorator(lifecycleListener: D): D {
        addLifecycleListener(lifecycleListener)
        return lifecycleListener
    }

    /**
     * Call when view is created
     */
    protected open fun onViewCreated(view: View) {}

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
}