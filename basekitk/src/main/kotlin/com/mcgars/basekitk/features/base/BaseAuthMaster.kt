package com.mcgars.basekitk.features.base

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.IntDef
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.drawer.BaseDrawerNavigationViewController
import com.mcgars.basekitk.features.simple.SimpleActivity
import com.mcgars.basekitk.tools.pagecontroller.PageController
import java.io.Serializable

object PageType {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ACTIVITY, VIEW)
    annotation class ID

    /**
     * Opened auth page in new activity
     * [com.mcgars.basekitk.features.simple.SimpleActivity] by default
     */
    const val ACTIVITY = 1

    /**
     * Opened auth page in same activity using [BaseKitActivity.loadPage]
     * using by default
     */
    const val VIEW = 0
}

/**
 * After success auth will trigger [Activity.onBackPressed]
 */
class AuthReturn : AuthItem

/**
 * Clear all back stack and show auth page
 */
class AuthLogin : AuthItem

/**
 * After success auth will open next page and [BaseAuthMaster.authViewClass]
 * will be closed
 *
 * @param next page that will be opened
 * @param containerId where page will be setting, by default R.id.contentFrame
 */
open class AuthNext(
        open val next: Class<out BaseViewController>,
        /**
         * Container id
         */
        @IdRes
        open val containerId: Int = R.id.contentFrame
) : AuthItem

/**
 * Contains [PageType] status for routing
 */
interface AuthItem : Serializable {

    /**
     * Type authorization, see [PageType]
     */
    @PageType.ID
    fun getPageType() = PageType.VIEW


}

abstract class BaseAuthSendler(
        open val authViewClass: Class<out BaseViewController>,
        open val authActivityClass: Class<out Activity> = SimpleActivity::class.java
) {

    /**
     * Params
     */
    var arguments = Bundle()

    /**
     * Check if user authorize
     */
    abstract fun isAuth(): Boolean

    /**
     * Open auth page or
     *
     * @return true if user already authorized
     */
    fun launch(context: BaseKitActivity, auth: AuthItem): Boolean {
        // flag that user is authorized
        if (isAuth() && auth is AuthReturn) return true
        // start activity
        if (auth.getPageType() == PageType.ACTIVITY) {
            context.startActivity(getIntent(context, auth))
        } else {
            // open page in current activity
            loadPage(context, auth)
        }
        // flag that user is't authorized
        return false
    }

    /*
    * Prepare base params
    * */
    private fun buildParams(auth: AuthItem): Bundle {
        return Bundle().apply {
            // fill users params
            putAll(arguments)
            // set type auth
            putSerializable(PageController.AUTH_CONTROLLER, auth)
        }
    }

    /**
     * @param auth behavior after success authorize
     *
     * @return Auth page or target page
     */
    fun getViewController(auth: AuthNext): BaseViewController {
        return createViewController(auth.next, auth)
    }

    /**
     * @param nextPage target page
     * @param auth behavior after success authorize
     *
     * @return Auth page or target page
     */
    fun getViewController(nextPage: Class<out BaseViewController>, auth: AuthItem): BaseViewController {
        return createViewController(nextPage, auth)
    }

    /**
     * Load page in current activity or create new
     * If user not authorized then load auth page after success login
     * user will pass to page specified bu [AuthItem]
     *
     * @param context activity
     * @param auth logic for route
     */
    fun loadPage(context: BaseKitActivity, auth: AuthItem) {
        // if no auth load auth page or page that request
        if (auth is AuthNext) {
            context.loadPage(createViewController(auth.next, auth))
        } else {
            // just open auth page
            context.loadPage(createViewController(authViewClass, auth))
        }
    }

    /** returns intent prepared to start. Only for activity mode */
    fun getIntent(context: BaseKitActivity, auth: AuthItem): Intent {

        val intent = Intent().apply {
            // put base params
            putExtras(buildParams(auth))
            // clear back stack
            if (auth is AuthLogin) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                        if (Build.VERSION.SDK_INT > 10) Intent.FLAG_ACTIVITY_CLEAR_TASK
                        else Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            }
        }

        // if not auth set Auth Activity
        if (!isAuth()) {
            intent.putExtra(PageController.CONTROLLER, authViewClass)
            intent.setClass(context, authActivityClass)
        } else {
            intent.putExtra(PageController.CONTROLLER, (auth as? AuthNext)?.next)
            intent.setClass(context, PageController.getBaseLauncherActivity(context))
        }

        return intent
    }

    /**
     * Create view page
     */
    private fun createViewController(pageClass: Class<out BaseViewController>, auth: AuthItem): BaseViewController {
        // auth page or request page
        val clazz = if (isAuth()) pageClass else authViewClass

        return try {
            val constructor = clazz.getConstructor(Bundle::class.java)
            constructor.newInstance(buildParams(auth))
        } catch (e: Exception) {
            clazz.newInstance()
        }
    }

}

/**
 * Class help for route after success user auth
 *
 * In order to receiver is work correctly need add constructor with params: Bundle
 * to your page
 */
class AuthReceiver(
        private val params: Bundle
) {

    /**
     * After success auth
     * go to request page
     *
     * @return true if logic setted by [AuthItem] false if not
     */
    fun goToPage(view: BaseViewController): Boolean {
        params.getSerializable(PageController.AUTH_CONTROLLER)?.let { auth ->
            if (auth is AuthNext) {
                if (view is BaseDrawerNavigationViewController)
                    view.drawerTool.loadPage(createViewController(auth.next) as BaseViewController)
                else
                    view.loadPage(createViewController(auth.next), false)
                return true
            } else if (auth is AuthReturn) {
                view.activity?.onBackPressed()
                return true
            }
        }

        return false
    }

    /**
     * Create view page
     */
    private fun createViewController(pageClass: Class<out Controller>): Controller {
        // auth page or request page
        return try {
            val constructor = pageClass.getConstructor(Bundle::class.java)
            constructor.newInstance(params)
        } catch (e: Exception) {
            pageClass.newInstance()
        }
    }

}
