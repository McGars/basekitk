package com.mcgars.basekitk.features.decorators

import com.mcgars.basekitk.features.base.AuthReceiver
import com.mcgars.basekitk.features.base.BaseViewController

/**
 * Decorator help with route
 */
class AuthDecorator(
        private val viewController: BaseViewController
) : DecoratorListener() {

    /**
     * Auth receiver help route after authorize
     */
    private val authReceiver = AuthReceiver(viewController.args)

    /**
     * Route to request page
     * or simple back to previous page
     * @return Boolean than can or can't route
     */
    fun goToPage() = authReceiver.goToPage(viewController)

}