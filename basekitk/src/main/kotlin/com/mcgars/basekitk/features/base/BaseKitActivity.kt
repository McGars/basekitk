package com.mcgars.basekitk.features.base

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.internal.ThreadUtils
import com.mcgars.basekitk.R
import com.mcgars.basekitk.config.KitConfig
import com.mcgars.basekitk.config.KitConfiguration
import com.mcgars.basekitk.features.decorators.DecoratorListener
import com.mcgars.basekitk.tools.log
import com.mcgars.basekitk.tools.pagecontroller.PageController
import com.mcgars.basekitk.tools.permission.BasePermissionController
import com.mcgars.basekitk.tools.toast
import com.mcgars.basekitk.tools.trying
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by Феофилактов on 26.07.2015.
 * Базовая активити, от которой наследуються все активити проекта в основном
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseKitActivity : AppCompatActivity(), ControllerChangeHandler.ControllerChangeListener {

    val TAG = "BaseKitActivity"

    /**
     * Стандартные настройки, создаються автоматически
     */
    val settings: SharedPreferences by lazy {
        getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }

    var isPageLoading = false
        private set

    private var doubleBack: Boolean = false

    val pageController: PageController by lazy { PageController(this) }

    protected val permissionController: BasePermissionController by lazy { BasePermissionController(this) }

    private var router: Router by Delegates.notNull()

    /**
     * If in back stack more than 1 page, always set back arrow
     */
    var alwaysArrow: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        router = Conductor.attachRouter(this, findViewById(R.id.contentFrame), savedInstanceState)
        router.addChangeListener(this)
    }

    /**
     * body xml
     * аналогично setContentView()
     */
    protected open fun getLayoutId() = R.layout.basekit_activity_simple

    /**
     * Remove all fragments from stack
     */
    open fun clearBackStack() {
        // Clear all back stack.
        router.setBackstack(ArrayList<RouterTransaction>(), null)
    }

    /**
     * Override this if need load controller by id
     * @param id
     */
    open fun loadPage(id: Int) {
    }

    /**
     * Default in animation for page
     */
    protected open fun getDefaultPopAnimate(): ControllerChangeHandler? = null

    /**
     * Default out animation for page
     */
    protected open fun getDefaultPushAnimate(): ControllerChangeHandler? = null


    /**
     * Display controller on the screen
     * @param _frag
     * *
     * @param backstack if true, remove previous controller from Fragment Manager
     * *        and when press back button then will be -2 Fragments
     */
    open fun loadPage(view: Controller?, backstack: Boolean = true) {

        if (view == null || (Build.VERSION.SDK_INT >= 17 && isDestroyed) || isFinishing)
            return

        if (view.overriddenPopHandler == null)
            view.overridePopHandler(getDefaultPopAnimate())
        if (view.overriddenPushHandler == null)
            view.overridePushHandler(getDefaultPushAnimate())

        val transition = RouterTransaction.with(view)
        router.run {
            if (view is BaseViewController) {
                view.addDecorator(object : DecoratorListener() {
                    override fun postAttach(controller: Controller, view: View) {
                        setHomeArrow(isShowArrow())
                    }
                })
            }

            if (getKitConfig()?.isDebug == true) {
                log { "load page: ${view.javaClass.simpleName}" }
            }

            if (backstack) pushController(transition) else replaceTopController(transition)
        }
    }

    /**
     * Check if need show arrow in toolbar
     */
    open fun isShowArrow() = alwaysArrow && router.backstackSize > 1

    override fun onBackPressed() {

        // Check if we can back pressed from views
        if (isPageLoading || handleBack()) {
            return
        }

        if (router.backstackSize > 1) {
            router.popCurrentController()
            return
        }

        if (doubleBack && !doubleBackPressed())
            return
        super.onBackPressed()
    }

    @UiThread
    fun handleBack(): Boolean {
        ThreadUtils.ensureMainThread()

        router.backstack.let { backStack ->
            if (!backStack.isEmpty()) {
                if (backStack.last().controller().handleBack()) {
                    return true
                }
            }
        }

        return false
    }

    fun setDoubleBackPressedToExit(doubleBack: Boolean) {
        this.doubleBack = doubleBack
    }

    private var isFirstBack = true
    private fun doubleBackPressed(): Boolean {

        isFirstBack = when (isFirstBack) {
            true -> {
                toast(R.string.basekit_one_more_back_to_exit)
                Thread {
                    trying { Thread.sleep(2000L) }
                    isFirstBack = true
                }.start()
                false
            }
            false -> true
        }

        return isFirstBack
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (router.onOptionsItemSelected(item))
                return true
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (CLOSE_APLICATION) {
            if (isTaskRoot)
                CLOSE_APLICATION = false
            finish()
        }
    }

    override fun onDestroy() {
        router.removeChangeListener(this)
        super.onDestroy()
    }

    open fun setHomeArrow(arrow: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(arrow)
    }

    /**
     * Close all activity in stack
     */
    open fun exitApplication() {
        CLOSE_APLICATION = true
        finish()
    }

    override fun setTitle(title: CharSequence) {
        supportActionBar?.let {
            it.title = title
        } ?: super.setTitle(title)
    }

    override fun setTitle(text: Int) = setTitle(getString(text))

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionController.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun checkPermission(permission: String, listener: ((allow: Boolean) -> Unit)) {
        permissionController.checkPermission(permission, listener)
    }

    companion object {
        private var CLOSE_APLICATION: Boolean = false
    }

    override fun onChangeCompleted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
        isPageLoading = false
    }

    override fun onChangeStarted(to: Controller?, from: Controller?, isPush: Boolean, container: ViewGroup, handler: ControllerChangeHandler) {
        isPageLoading = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        router.backstack.forEach { transition ->
            val controller = transition.controller()
            if (controller is BaseViewController) {
                controller.onConfigurationChanged(newConfig)
            }
        }
    }

    private fun getKitConfig(): KitConfig? {
        return (application as? KitConfiguration)?.getConfiguration()
    }
}

