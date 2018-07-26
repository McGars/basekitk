package com.mcgars.basekitk.features.base

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
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

    /**
     * Disable call [ViewCompat.setFitsSystemWindows]
     */
    var isFitSystem = true

    /**
     * Если в разметке есть тулбар то  автоматически подхватится
     * по умолчанию id=R.id.toolbar
     * но можно переопределить id с помощью метода getToolbarId()
     * @return [Toolbar]
     */
    var toolbar: Toolbar? = null
        get() {
            if (field != null)
                return field
            if (parentController != null) {
                return (parentController as BaseViewController).toolbar
            }
            return null
        }
        private set(value) {
            field = value
            if (value != null) {
                (activity as AppCompatActivity).setSupportActionBar(value)
            }
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
     * Loader blocking ui when show
     */
    val loader: LoaderController by lazy { LoaderController(view as ViewGroup) }

    /**
     * Settings for all app
     */
    val settings: SharedPreferences
        get() = (activity as BaseKitActivity<*>).settings

    /**
     * If is false then [getLayoutId] wraps by [CoordinatorLayout]
     * and added[getToolbarLayout]
     */
    open var isCustomLayout = false

    /*
     * trigger when page ready to work
     */
    private val readyDecorator = object : DecoratorListener() {
        override fun postCreateView(controller: Controller, view: View) {
            setTitle()
            onReady(view)
        }
    }

    init {
        // find toolbar and tabs
        addDecorator(object : DecoratorListener() {
            override fun postCreateView(controller: Controller, view: View) {
                if (getContainerLayout() != 0) {
                    toolbar = view.find<Toolbar>(R.id.toolbar)
                }

                tabs = view.find(R.id.tablayout)
            }

            override fun onChangeEnd(controller: Controller, changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
                if (Build.VERSION.SDK_INT >= 20) {
                    controller.view?.requestApplyInsets()
                }
            }
        })
    }

    /**
     * Layout of the page
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     * Call when view is ready to work
     */
    protected abstract fun onReady(view: View)

    /**
     * Set title manually
     */
    fun setTitle(title: String) {
        activity?.title = title
    }

    /**
     * Title from resources
     */
    @StringRes
    protected open fun getTitleInt() = 0

    /**
     * Life cycle of the Activity
     */
    fun <C : ActivityController> getAc(): C? {
        return (activity as? BaseKitActivity<C>)?.getAC()
    }

    /**
     * Show page to user
     */
    open fun loadPage(viewController: Controller, backStack: Boolean = true) {
        (activity as BaseKitActivity<*>).loadPage(viewController, backStack)
    }

    /**
     * Add extention decorator who modified current page's view
     */
    fun <D : DecoratorListener> addDecorator(decoratorListener: D): D {
        addLifecycleListener(decoratorListener)
        decorators.add(decoratorListener)
        return decoratorListener
    }

    /**
     * Show home arrow in toolbar
     * by default if backstack > 2 then back arrow show automatically
     * but you can disable auto set arrow just change [BaseKitActivity.alwaysArrow]
     */
    open fun setHomeArrow(arrow: Boolean) {
        var parentController = parentController
        while (parentController != null) {
            if (parentController.parentController == null)
                break
            parentController = parentController.parentController
        }
        (parentController as? BaseViewController)?.setHomeArrow(arrow)
    }

    /**
     * Change home up indicator in toolbar
     */
    open fun setUpIndicator(@DrawableRes drawable: Int) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(drawable)
        }
    }

    /**
     * Change home up indicator in toolbar
     */
    open fun setUpIndicator(drawable: Drawable) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(drawable)
        }
    }

    /**
     * Layout with toolbar
     */
    @LayoutRes
    protected open fun getContainerLayout() = R.layout.basekit_view_container

    /**
     * if true then view will behind status bar
     */
    protected open fun isFitScreen() = false

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val v: View = if (isCustomLayout) {
            inflater.inflate(getLayoutId(), container, false)
        } else {
            buildView(inflater, container)
        }
        decorators.forEach { it.onViewCreated(v) }

        addDecorator(readyDecorator)
        return v
    }

    /**
     * Set title from [getTitleInt] or [getTitle]
     */
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

    /**
     * Custom title
     */
    protected open fun getTitle(): String? = null

    private fun buildView(inflater: LayoutInflater, container: ViewGroup): View {

        val coordinator = inflater.inflate(getContainerLayout(), container, false) as ViewGroup

        // add content view
        val layoutView = inflater.inflate(getLayoutId(), coordinator, false)
        coordinator.addView(layoutView)
        // attach scroll behavior for app bar
        (layoutView.layoutParams as CoordinatorLayout.LayoutParams).let { layoutParams ->
            if (layoutParams.behavior == null)
                layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()
        }

        if (isFitSystem) {
            ViewCompat.setFitsSystemWindows(coordinator, true)
        }

        return coordinator
    }

    override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        // if use ControllerChangeHandler.removesFromViewOnPush() == false
        // then onCreateOptionMenu fired in page with which user carried out
        // and in toolbar falls into not needed items
        // so when change started we hide menu and when change ended show menu
        // if in setHasOptionsMenu() setted true
        setOptionsMenuHidden(true)

        if (changeType == ControllerChangeType.POP_EXIT) {
            activity.hideKeyboard(activity?.window?.decorView)
        }
    }

    @CallSuper
    override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
        if (changeType == ControllerChangeType.PUSH_ENTER || changeType == ControllerChangeType.POP_ENTER) {
            // show menu if in setHasOptionsMenu() setted true
            setOptionsMenuHidden(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        decorators.clear()
    }
}