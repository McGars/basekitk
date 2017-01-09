package com.mcgars.basekitk.tools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.tools.pagecontroller.PageController

/**
 * @author Александр Свиридов on 31.07.2014.
 */
abstract class BaseAuthMaster<T : BaseAuthMaster<T>> {

    protected val context: Context
    protected var frmanager: FragmentManager? = null
    protected var rootId = -1
    protected var arguments = Bundle()
    protected var authActivityClass: Class<out Activity>? = null
    protected var authViewClass: Class<out Controller>? = null

    /** режим работы для AuthActivity-SomeActivity */
    constructor(context: Context) {
        this.context = context
        arguments.putBoolean(ACTIVITY_MODE, true)
    }

    /** режим работы для AuthFragment-SomeFragment */
    constructor(context: Context, @IdRes rootId: Int, frmanager: FragmentManager) {
        this.context = context
        this.rootId = rootId
        this.frmanager = frmanager
        arguments.putBoolean(ACTIVITY_MODE, false)
        arguments.putInt(ROOT_ID, rootId)
    }

    fun setSkip(skipCls: Class<*>): T {
        arguments.putSerializable(SKIP_CLASS, skipCls)
        return this as T
    }

    fun setAuthActivity(authActivityClass: Class<out Activity>): T {
        this.authActivityClass = authActivityClass
        return this as T
    }

    fun setAuthFragment(authFragmentClass: Class<out Controller>): T {
        this.authViewClass = authFragmentClass
        return this as T
    }

    fun setArguments(arguments: Bundle?): T {
        arguments?.let { this.arguments.putAll(arguments) }
        return this as T
    }

    fun setJustAuthorize(justAuthorize: Boolean): T {
        this.arguments.putBoolean(JUST_AUTHORIZE, justAuthorize)
        return this as T
    }

    fun setActivityMode(isActivityMode: Boolean): T {
        this.arguments.putBoolean(ACTIVITY_MODE, isActivityMode)
        return this as T
    }

    /** return Fragment in Fragment mode and null in activity mode */
    fun openPage(pageClass: Class<out Controller>): Controller? {
        arguments.putSerializable(PASSED_CLASS, pageClass)

        if (arguments.getBoolean(ACTIVITY_MODE, false)) {
            context.startActivity(createIntent(pageClass))
            return null
        } else
            return openViewController(pageClass)
    }

    /** returns intent prepared to start. Only for activity mode */
    fun createIntent(pageClass: Class<out Controller>): Intent {
        var pageClass = pageClass
        val auth = isAuthorized
        val intent = Intent()
        if (!auth) {
            intent.setClass(context, authActivityClass!!)
        } else {
            intent.setClass(context, PageController.baseLauncherActivity)
            arguments.putSerializable(PageController.ACTIVITY_CONTROLLER, PageController.baseActivityController)
            arguments.putSerializable(PageController.CONTROLLER, pageClass)
        }
        if (!auth) {
            if (Build.VERSION.SDK_INT > 10)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            else
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        intent.putExtras(arguments)
        return intent
    }

    fun createViewController(pageClass: Class<out Controller>): Controller? {
        try {
            if (!isAuthorized) {
                arguments.putSerializable(PASSED_CLASS, pageClass)
                return buildController(authViewClass!!)
            } else
                return buildController(pageClass)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }

    fun buildController(_class: Class<out Controller>): Controller? {
        val constructor = _class.getConstructor(Bundle::class.java)
        return constructor?.run { newInstance(arguments) } ?: _class.newInstance()
    }

    abstract val isAuthorized: Boolean

    private fun openViewController(pageClass: Class<out Controller>): Controller? {
        val view = createViewController(pageClass)

        return view
    }

    companion object {
        val SKIP_CLASS = "basekit.auth.SKIP_CLASS"
        val PASSED_CLASS = "basekit.auth.PASSED_CLASS"
        val JUST_AUTHORIZE = "basekit.auth.JUST_AUTHORIZE"
        val ACTIVITY_MODE = "basekit.auth.ACTIVITY_MODE"
        internal val ROOT_ID = "basekit.auth.ROOT_ID"
    }
}
