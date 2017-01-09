package com.mcgars.basekitk.tools.pagecontroller

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.features.simple.ActivityController
import com.mcgars.basekitk.features.simple.BaseKitActivity
import com.mcgars.basekitk.features.simple.SimpleActivity
import com.mcgars.basekitk.tools.BaseKitConstants


/**
 * Created by Феофилактов on 16.07.2015.

 * like
 * [Page](key1 = "param1")
 * CustomFragment extend Fragment{}

 * equals
 * Bundle b = new Bundle()
 * b.putObject(key1(), val1);
 * b.putObject(key2(), val2);
 * fragment.setArguments(b);

 * use instead of
 * public static CustomFragment newInstance(Object val1, Object val2){
 * Bundle b = new Bundle()
 * b.putObject("param1", val1);
 * b.putObject("param2", val2);
 * fragment = new CustomFragment();
 * fragment.setArguments(b);
 * return fragment;
 * }
 */
class PageController(private val context: BaseKitActivity<ActivityController<*>>) {
    private var activityClass: Class<out Activity> = SimpleActivity::class.java

    internal var params = Bundle()
    private var viewClass: Class<out Controller>? = null
    internal var pageAnn: Page? = null
    private var uri: Uri? = null
    private var activityController: Class<out ActivityController<*>>? = null

    fun setActivityController(activityController: Class<out ActivityController<*>>): PageController {
        this.activityController = activityController
        return this
    }

    fun initParamsFromActivity(): PageController {
        context.intent.extras?.run { params = this }
        return this
    }

    private fun invalidate(): PageController {
        params = Bundle()
        activityClass = SimpleActivity::class.java
        uri = null
        viewClass = null
        pageAnn = null
        activityController = null
        return this
    }

    fun startActivity(view: Class<out Controller>? = null) {
        setView(view)
        startActivityForResult(0)
    }

    fun startActivityForResult(
            view: Class<out Controller>,
            code: Int = 0,
            val1: Any? = null,
            val2: Any? = null,
            val3: Any? = null
            ) {
        setView(view)
        startActivity(view, val1, val2, val3, code)
    }

    fun loadPage() {
        context.loadPage(fragment)
        invalidate()
    }

    fun loadPage(view: Class<out Controller>, `val`: Any? = null, val2: Any? = null, val3: Any? = null) {
        getAnnotation(view, `val`, val2, val3)
        context.loadPage(initView(view))
        invalidate()
    }

    fun startActivity(view: Class<out Controller>, `val`: Any?= null, val2: Any? = null, val3: Any? = null, codeResult: Int = 0) {
        setView(view)
        getAnnotation(view, `val`, val2, val3)
        startActivityForResult(codeResult)
    }

    private fun getAnnotation(fragment: Class<out Controller>, `val`: Any?, val2: Any?, val3: Any?) {
        if (pageAnn == null)
            pageAnn = fragment.getAnnotation(Page::class.java)
        if (`val` != null)
            setParamFromAnno(pageAnn, 1, `val`)
        if (val2 != null)
            setParamFromAnno(pageAnn, 2, val2)
        if (val3 != null)
            setParamFromAnno(pageAnn, 3, val3)
    }

    val intent: Intent
        get() {
            val baseIntetnt = Intent()

            if (activityClass == SimpleActivity::class.java && baseLauncherActivity != null)
                baseIntetnt.setClass(context, baseLauncherActivity)
            else
                baseIntetnt.setClass(context, activityClass)

            return baseIntetnt
        }

    private fun intController() {
        if (activityController != null)
            params.putSerializable(ACTIVITY_CONTROLLER, activityController)
        else if (baseActivityController != null)
            params.putSerializable(ACTIVITY_CONTROLLER, baseActivityController)
    }

    fun <C : ActivityController<*>> getActivityController(): C? {
        var _class = params.getSerializable(ACTIVITY_CONTROLLER) as Class<out ActivityController<*>>?
        if (_class == null)
            _class = baseActivityController

        if (_class != null) {
            try {
                val controller = _class.newInstance()
                controller.activity = context
                return controller as C?
            } catch (e: Exception) {
                e.printStackTrace()
                throw IllegalStateException(e.message)
            }

        }
        return null
    }

    fun startActivityForResult(code: Int) {
        intController()
        val i = intent

        i.putExtras(params)
        if (uri != null)
            i.data = uri
        if (code > 0)
            context.startActivityForResult(i, code)
        else
            context.startActivity(i)
        invalidate()
    }

    fun setView(clazz: Class<out Controller>?): PageController {
        viewClass = clazz
        params.putSerializable(CONTROLLER, clazz)
        return this
    }

    fun addParam(key: String, value: String): PageController {
        params.putString(key, value)
        return this
    }

    fun addParam(key: String, value: Int): PageController {
        params.putInt(key, value)
        return this
    }

    /**
     * Ставим первый парамметр в аннотации Page
     * @param value
     * *
     * @return
     */
    fun setParam1(value: Any): PageController {
        return setCustomParams(value, null, null)
    }

    fun setParam2(value: Any): PageController {
        return setCustomParams(null, value, null)
    }

    fun setParam3(value: Any): PageController {
        return setCustomParams(null, null, value)
    }

    private fun setCustomParams(`val`: Any?, val2: Any?, val3: Any?): PageController {
        if (viewClass == null)
            throw IllegalStateException("You must call setView(Fragment.class) method before!")

        getAnnotation(viewClass!!, `val`, val2, val3)
        return this
    }

    fun setData(uri: Uri): PageController {
        this.uri = uri
        return this
    }

    fun setTitle(title: Int): PageController {
        params.putInt(BaseKitConstants.TITLE, title)
        return this
    }

    fun setTitle(title: String): PageController {
        params.putString(BaseKitConstants.TITLE, title)
        return this
    }

    fun addParam(key: String, value: Boolean): PageController {
        params.putBoolean(key, value)
        return this
    }

    fun setParams(b: Bundle): PageController {
        params.putAll(b)
        return this
    }

    /**
     * Set another start Activity
     * Default @ru.altarix.kit.SimpleActivity
     * @param activity
     * *
     * @return
     */
    fun setLaunchActivity(activity: Class<out Activity>): PageController {
        activityClass = activity
        return this
    }

    fun getParams(): Bundle {
        return params
    }

    /**
     * Return new Fragment and set params Arguments
     * @return
     */
    val fragment: Controller?
        get() = initView(classView)

    private fun initView(_class: Class<out Controller>): Controller? {
        var view: Controller? = null
        try {
            val constructor = _class.getConstructor(Bundle::class.java)
            view = constructor?.run {
                newInstance(params)
            } ?: _class.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }

    val classView: Class<out Controller>
        get() = params.getSerializable(CONTROLLER) as Class<out Controller>

    private fun setParamFromAnno(pageAnn: Page?, pos: Int, `val`: Any) {
        if (pageAnn != null) {

            val key = when(pos) {
                1 -> pageAnn.key1
                2 -> pageAnn.key2
                3 -> pageAnn.key3
                else->null
            }

            if (!TextUtils.isEmpty(key)) {
                addParam(key!!, `val`)
            }
        }
    }

    private fun addParam(key: String, `val`: Any?) {
        if (`val` is String)
            params.putString(key, `val` as String?)
        else if (`val` is Boolean)
            params.putBoolean(key, `val`)
        else if (`val` is Int)
            params.putInt(key, `val`)
        else if (`val` != null)
            throw IllegalStateException(`val`.javaClass.simpleName + " - Not supported, use String, int, boolean for annotation or another build construction OR ASK Vladimir Feofilaktov")
    }

    companion object {

        val CONTROLLER = "viewController"
        val ACTIVITY_CONTROLLER = "controller"
        var baseActivityController: Class<out ActivityController<*>>? = null
        var baseLauncherActivity: Class<out BaseKitActivity<*>>? = SimpleActivity::class.java

        fun init(context: BaseKitActivity<*>): PageController {
            return PageController(context)
        }
    }
}
/**
 * Ставим первый парамметр в аннотации Page
 * @param fragment
 * *
 * @param val
 */
/**
 * Ставим первый и второй парамметр в аннотации Page
 * @param fragment
 * *
 * @param val
 */
/**
 * Ставим первый парамметр в аннотации Page
 * @param fragment
 * *
 * @param val
 */
