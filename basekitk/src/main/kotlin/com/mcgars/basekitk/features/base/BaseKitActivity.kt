package com.mcgars.basekitk.features.base

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewGroup
import com.bluelinelabs.conductor.*
import com.mcgars.basekitk.R
import com.mcgars.basekitk.config.KitConfiguration
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.tools.LoaderController
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
abstract class BaseKitActivity<out C : ActivityController<*>> : AppCompatActivity(), ControllerChangeHandler.ControllerChangeListener {

    /**
     * true if click on home button
     * check it in onBackPressed
     * @return
     */
    val TAG = "BaseKitActivity"

    /**
     * Лисенер который срабатывает в момент нажатия назад
     */
    //    protected OnFragmentBackListener fragmentBackListener;
    /**
     * Лисенер который срабатывает в момент нажатия home arrow
     */
    protected var homeListener: (() -> Boolean)? = null
    /**
     * Стандартные настройки, создаються автоматически
     */
    val settings: SharedPreferences by lazy {
        getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }

    var isPageLoading = false

    private var doubleBack: Boolean = false

    val pageController: PageController by lazy { PageController(this) }

    protected val permissionController: BasePermissionController by lazy { BasePermissionController(this) }

    /**
     * Контроллер, который включает в себя жизненный цикл активити
     * все базовые инициализации которые дожны быть в базовом активити
     * необходимо писать туда
     * а уже в коде вызывать activvity.getAC().getNeedMethod()
     */
    private var activityController: C? = null
        private set

    private var router: Router by Delegates.notNull()

    /**
     * If in back stack more than 1 page, always set back arrow
     */
    var alwaysArrow: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityController = initActivityController()
        setContentView(getLayoutId())
        router = Conductor.attachRouter(this, findViewById(R.id.contentFrame) as ViewGroup, savedInstanceState)
        router.addChangeListener(this)
        activityController?.onCreate(savedInstanceState)
    }

//    /**
//     * * Если в разметке есть табы то автоматически подхватится
//     * по умолчанию id=R.id.tabs
//     * но можно переопределить id с помощью метода getToolbarId()
//     * @return [ExSlidingTabLayout]
//     */
//    open val tabs: TabLayout?
//        get() {
//            showTabs(true)
//            return tabsView
//        }
//
//    val isTabsVisible: Boolean
//        get() = tabsView?.visibility == View.VISIBLE
//
//    /**
//     * Показывает табы, которые, ты, разработчик, должен прописать
//     * в разметке xml где находиться тулбар (если они нужны)
//     * @param show
//     */
//    open fun showTabs(show: Boolean) {
//        tabsView.gone(!show)
//    }

    /**
     * Контроллер, который включает в себя жизненный цикл активити
     * все базовые инициализации которые дожны быть в базовом активити
     * необходимо писать туда
     * а уже в коде вызывать activity.getAC().getNeedMethod()
     */
    abstract fun initActivityController(): C?

    /**
     * @return ActivityController inited in your activity or from [KitConfiguration]
     */
    open fun getAC(): C {
        if (activityController == null) {
            if (application is KitConfiguration) {
                return (application as KitConfiguration).getConfiguration()?.baseActivityController as? C
                        ?: throw NullPointerException("Please implements KitConfiguration in you Application and init global configuration by KitBuilder")
            }
            throw NullPointerException("Please implements KitConfiguration in you Application and init global configuration by KitBuilder")
        } else return activityController as C
    }

    /**
     * body xml
     * аналогично setContentView()
     */
    protected open fun getLayoutId() = R.layout.basekit_activity_simple_v2

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
    open protected fun getDefaultPopAnimate(): ControllerChangeHandler? = null

    /**
     * Default out animation for page
     */
    open protected fun getDefaultPushAnimate(): ControllerChangeHandler? = null


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
            if (backstack) pushController(transition) else replaceTopController(transition)
            if (alwaysArrow && backstackSize > 1)
                setHomeArrow(true)
        }
    }
//
//    /**
//     * Если есть аннотация что требуються табы то перед сменной фрагмента
//     * не прячем табы, что бы не было мигантя табов
//     * @param view
//     */
//    protected fun checkHideTabs(view: Controller?) {
//        view?.run {
//            showTabs(javaClass.getAnnotation(ExTabs::class.java) != null)
//        }
//    }



    override fun onBackPressed() {

        if (isPageLoading || router.handleBack()) {
            return
        }

        // Check if we can back pressed from views
        if (alwaysArrow && router.backstackSize > 0)
            setHomeArrow(router.backstackSize > 1)

        // Check if we can back pressed from activity controller
        if (activityController?.onBackPressed() ?: false)
            return

        if (doubleBack && !doubleBackPressed())
            return
        super.onBackPressed()
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
        if (activityController?.onOptionsItemSelected(item) ?: false)
            return true
        if (item.itemId == android.R.id.home) {
            if (homeListener?.invoke() ?: false)
                return true
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /***
     * Remove this from your code, onBackListener add automatically
     * @param homeListener
     */
    fun setOnHomeListener(homeListener: (() -> Boolean)) {
        this.homeListener = homeListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        activityController?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        activityController?.onResume()
        if (CLOSE_APLICATION) {
            if (isTaskRoot)
                CLOSE_APLICATION = false
            finish()
        }
    }

    override fun onDestroy() {
        activityController?.onDestroy()
        router.removeChangeListener(this)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        activityController?.onPause()
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

    override fun onStart() {
        super.onStart()
        activityController?.onStart()
    }

    override fun onStop() {
        super.onStop()
        activityController?.onStop()
    }

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
}

