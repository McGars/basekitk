package com.mcgars.basekitk.features.simple

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.tools.*
import com.mcgars.basekitk.tools.pagecontroller.ExTabs
import com.mcgars.basekitk.tools.pagecontroller.PageController
import com.mcgars.basekitk.tools.permission.BasePermissionController
import kotlin.properties.Delegates

/**
 * Created by Феофилактов on 26.07.2015.
 * Базовая активити, от которой наследуються все активити проекта в основном
 */
abstract class BaseKitActivity<out C : ActivityController<*>> : AppCompatActivity() {
    /**
     * true if click on home button
     * check it in onBackPressed
     * @return
     */
    var isHomeButtonPressed: Boolean = false
        private set

    /**
     * Лисенер который срабатывает в момент нажатия назад
     */
    //    protected OnFragmentBackListener fragmentBackListener;
    /**
     * Лисенер который срабатывает в момент нажатия home arrow
     */
    protected var homeListener: OnHomeListener? = null
    /**
     * Стандартные настройки, создаються автоматически
     */
    private val mSettings: SharedPreferences by lazy {
        getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }
    private var doubleBack: Boolean = false

    val pageController: PageController by lazy { PageController(this) }

    /**
     * Если в разметке есть тулбар то  автоматически подхватится
     * по умолчанию id=R.id.toolbar
     * но можно переопределить id с помощью метода getToolbarId()
     * @return [Toolbar]
     */
    var toolbar: Toolbar? = null
        private set
    /**
     * Если в разметке есть табы то автоматически подхватится
     * по умолчанию id=R.id.tabs
     * но можно переопределить id с помощью метода getToolbarId()
     */
    var tabsView: TabLayout? = null
        private set

    protected val permissionController: BasePermissionController by lazy { BasePermissionController(this) }

    val loaderController: LoaderController by lazy { LoaderController(this) }
    /**
     * Контроллер, который включает в себя жизненный цикл активити
     * все базовые инициализации которые дожны быть в базовом активити
     * необходимо писать туда
     * а уже в коде вызывать activvity.getAC().getNeedMethod()
     */
    private var activityController: C? = null
        private set
    /**
     * Switcher appBarLayout
     */
    private val abbBarCoordinator: AppBarCoordinator by lazy { AppBarCoordinator(this) }
    /**
     * @return CoordinatorLayout
     */
    val coordinatorLayout: CoordinatorLayout by lazy { findViewById(getCoordinatorLayoutId()) as CoordinatorLayout }

    private var router: Router by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityController = initActivityController()
        setContentView(getLayoutId())
        initToolBar()
        router = Conductor.attachRouter(this, findViewById(R.id.contentFrame) as ViewGroup, savedInstanceState)
        activityController?.onCreate(savedInstanceState)
    }

    protected open fun initToolBar() {
        toolbar = findViewById(getToolbarId()) as Toolbar
        tabsView = findViewById(getTabsId()) as TabLayout
        prepareTabsConfig(tabsView)
        toolbar?.run { setSupportActionBar(this) }
    }

    /**
     * Change default app bar layout whit toolbar to custom
     * @param appBarLayout
     * *
     * @return boolean
     */
    open fun setAppBar(appBarLayout: Int): Boolean {
        if (abbBarCoordinator.setAppBar(appBarLayout)) {
            initToolBar()
            return true
        }
        return false
    }

    /**
     * Set default app bar layout
     * @return boolean
     */
    open fun setAppBarDefault(): Boolean {
        if (abbBarCoordinator.setAppBar(AppBarCoordinator.DEFAULT_APP_BAR)) {
            initToolBar()
            return true
        }
        return false
    }

    /**
     * Если нужно переопределить настройки табов (цвет, линию и т.д.)
     * @param tabs
     */
    protected open fun prepareTabsConfig(tabs: TabLayout?) {

    }

    protected open fun getToolbarId() = R.id.toolbar

    protected open fun getTabsId() = R.id.tablayout

    protected open fun getCoordinatorLayoutId() = R.id.coordinatorLayout

    /**
     * * Если в разметке есть табы то автоматически подхватится
     * по умолчанию id=R.id.tabs
     * но можно переопределить id с помощью метода getToolbarId()
     * @return [ExSlidingTabLayout]
     */
    val tabs: TabLayout?
        get() {
            showTabs(true)
            return tabsView
        }

    val isTabsVisible: Boolean
        get() = tabsView?.visibility === View.VISIBLE

    /**
     * Показывает табы, которые, ты, разработчик, должен прописать
     * в разметке xml где находиться тулбар (если они нужны)
     * @param show
     */
    open fun showTabs(show: Boolean) {
        tabsView.gone(!show)
    }

    /**
     * Контроллер, который включает в себя жизненный цикл активити
     * все базовые инициализации которые дожны быть в базовом активити
     * необходимо писать туда
     * а уже в коде вызывать activvity.getAC().getNeedMethod()
     */
    abstract fun initActivityController(): C?

    open fun getAC() = activityController

    /**
     * body xml
     * аналогично setContentView()

     */
    protected open fun getLayoutId() = R.layout.basekit_activity_simple

    /**
     * Remove all fragments from stack
     */
    fun clearBackStack() {
        // Clear all back stack.
        router.popToRoot()
    }


    /**
     * Override this if need load fragment by id
     * @param id
     */
    open fun loadPage(id: Int) {
    }


    /**
     * Display fragment on the screen
     * @param _frag
     * *
     * @param backstack if true, remove previous fragment from Fragment Manager
     * *        and when press back button then will be -2 Fragments
     */
     open fun loadPage(view: Controller?, backstack: Boolean = true) {

        if (view == null || (Build.VERSION.SDK_INT >= 17 && isDestroyed) || isFinishing)
            return

        /**
         * Прячим табы когда переходим на другую страницу
         */
        checkHideTabs(view)
        setAppBarDefault()
        if (!backstack) {
            router.replaceTopController((RouterTransaction.with(view)))
        } else {
            router.pushController((RouterTransaction.with(view)))
        }
    }

    /**
     * Если есть аннотация что требуються табы то перед сменной фрагмента
     * не прячем табы, что бы не было мигантя табов
     * @param view
     */
    protected fun checkHideTabs(view: Controller?) {
        view?.run {
            showTabs(javaClass.getAnnotation(ExTabs::class.java) != null)
        }
    }

    override fun onBackPressed() {
        // Check if we can back pressed from views
        if (router.handleBack()) {
            return
        }

        // Check if we can back pressed from activity controller
        if (activityController?.onBackPressed() ?: false)
            return

        isHomeButtonPressed = false
        if (doubleBack && !doubleBackPressed())
            return
        super.onBackPressed()
    }

    fun setDoubleBackPressedToExit(doubleBack: Boolean) {
        this.doubleBack = doubleBack
    }

    private var isFirstBack = true
    fun doubleBackPressed(): Boolean {

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
            if (homeListener?.onHomePressed() ?: false)
                return true
            isHomeButtonPressed = true
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * home button listener
     */
    interface OnHomeListener {
        fun onHomePressed(): Boolean
    }

    /***
     * Remove this from your code, onBackListener add automatically
     * @param homeListener
     */
    fun setOnHomeListener(homeListener: OnHomeListener) {
        this.homeListener = homeListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d("onActivityResult", "base responce ")
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
    fun exitApplication() {
        CLOSE_APLICATION = true
        finish()
    }

    override fun setTitle(title: CharSequence) {
        supportActionBar?.run { setTitle(title) } ?: super.setTitle(title)
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

    fun checkPermission(permission: String, listener: ((allow: Boolean) -> Unit)) {
        permissionController.checkPermission(permission, listener)
    }

    companion object {
        private var CLOSE_APLICATION: Boolean = false
    }

}

