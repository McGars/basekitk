package com.mcgars.basekitk.features.base

import android.os.Bundle
import com.mcgars.basekitk.features.decorators.AuthDecorator

/**
 * In order to [AuthReceiver] is work correctly need add constructor with params: Bundle
 * to your page
 */
abstract class BaseAuthViewController(params: Bundle?) : BaseViewController(params) {

    val authDecorator = addDecorator(AuthDecorator(this))

    /**
     * Route to request page
     * or simple back to previous page
     * @return Boolean value about can or can't route
     */
    protected fun goToNextPage() = authDecorator.goToPage()
}