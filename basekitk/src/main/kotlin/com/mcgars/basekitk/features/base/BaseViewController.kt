package com.mcgars.basekitk.features.base

import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.decorators.DecoratorListener
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.tools.LoaderController
import com.mcgars.basekitk.tools.find
import com.mcgars.basekitk.tools.hideKeyboard
import com.mcgars.basekitk.tools.pagecontroller.PageController
import com.mcgars.basekitk.tools.visible
import java.util.*

/**
 * Created by gars on 29.12.2016.
 */
abstract class BaseViewController(args: Bundle? = null) : Controller(args) {

    val decorators: MutableList<DecoratorListener> = ArrayList()

    init {
        // Lets work with getView()
        addDecorator(object : DecoratorListener() {
            override fun postCreateView(controller: Controller, view: View) {
                if(getToolbarLayout() != 0)
                    toolbar = view.find(R.id.toolbar)
                tabs = view.find(R.id.tablayout)
                setTitle()
                view.post {
                    onReady(view)
                }
            }
        })
    }

    /**
     * Если в разметке есть тулбар то  автоматически подхватится
     * по умолчанию id=R.id.toolbar
     * но можно переопределить id с помощью метода getToolbarId()
     * @return [Toolbar]
     */
    var toolbar: Toolbar? = null
        private set(value) {
            field = value
            if(value != null)
                (activity as AppCompatActivity).setSupportActionBar(value)
        }

    /**
     * Если в разметке есть табы то автоматически подхватится
     * по умолчанию id=R.id.tabs
     * но можно переопределить id с помощью метода getToolbarId()
     */
    var tabs: TabLayout? = null
        private set
        get() {
            field?.visible()
            return field
        }

    /**
     * Run page in new Activity without create new Activity
     */
    val pageController: PageController
        get() = (activity as BaseKitActivity<*>).pageController

    /**
     * Loader block ui when show
     */
    val loader: LoaderController by lazy { LoaderController(activity!!) }

    /**
     * Life cycle of the Activity
     */
    fun <C : ActivityController<BaseKitActivity<C>>> getAc(): C? {
        return (activity as BaseKitActivity<C>).getAC()
    }

    /**
     * Settings for all app
     */
    val settings: SharedPreferences
        get() = (activity as BaseKitActivity<*>).settings

    /**
     * Show page to user
     */
    fun loadPage(viewController: Controller, backStack: Boolean = true) {
        (activity as BaseKitActivity<*>).loadPage(viewController, backStack)
    }

    /**
     * Show home arrow in toolbar
     */
    open fun setHomeArrow(arrow: Boolean) {
        var parentController = parentController
        while (parentController != null) {
            if(parentController.parentController == null)
                break
            parentController = parentController.parentController
        }
        (parentController as? BaseViewController)?.setHomeArrow(arrow)
    }

    /**
     * Layout of the page
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     * Layout with toolbar
     */
    @LayoutRes
    open protected fun getToolbarLayout() = R.layout.basekit_toolbar

    /**
     * if true then view will behind status bar
     */
    open protected fun isFitScreen() = false

    /**
     * If is false then [getLayoutId] wraps by [CoordinatorLayout]
     * and added[getToolbarLayout] and
     */
    open var isCustomLayout = false

    override final fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v: View
        if (isCustomLayout) {
            v = inflater.inflate(getLayoutId(), container, false)
        } else {
            v = buildView(inflater)
        }
        decorators.forEach { it.onViewCreated(v) }
        return v
    }

    private fun buildView(inflater: LayoutInflater): CoordinatorLayout {
        return CoordinatorLayout(activity).also { coordinator ->
            // set layout params
            coordinator.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            // moved view behind status bar
            ViewCompat.setFitsSystemWindows(coordinator, true)
            // add toolbar
            if (getLayoutId() != 0) {
                coordinator.addView(inflater.inflate(getToolbarLayout(), coordinator, false))
            }
            // add content view
            val layoutView = inflater.inflate(getLayoutId(), coordinator, false)
            coordinator.addView(layoutView)
            // attach scroll behavior for app bar
            (layoutView.layoutParams as CoordinatorLayout.LayoutParams).let { layoutParams ->
                if (layoutParams.behavior == null)
                    layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()
            }
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

    protected open fun setTitle() {
        var parentController = parentController
        while (parentController != null) {
            if (parentController is BaseViewController
                    && (parentController.getTitle() != null
                    || parentController.getTitleInt() != 0)) {
                return
            }
            parentController = parentController.parentController
        }

        // set title
        getTitle()?.let { title ->
            activity?.title = title
        } ?: getTitleInt().let { title ->
            if (title != 0) activity?.setTitle(title)
        }
    }

    protected open fun getTitle(): String? = null

    protected open fun getTitleInt() = 0

//    override fun onActivityPaused(activity: Activity) {
//        super.onActivityPaused(activity)
//        activity.hideKeyboard(activity.window.decorView)
//    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        if (changeType == ControllerChangeType.POP_EXIT)
            activity.hideKeyboard(activity?.window?.decorView)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        decorators.clear()
    }
}