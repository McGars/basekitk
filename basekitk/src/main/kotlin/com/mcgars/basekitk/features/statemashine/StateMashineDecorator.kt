package com.mcgars.basekitk.features.statemashine

import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.features.decorators.DecoratorListener


class StateMashineDecorator(
        private val stateView: StateView
) : DecoratorListener() {

    private var coordinator: ViewGroup? = null

    override fun postCreateView(controller: Controller, view: View) {
        coordinator = findCoordinator(view)
    }

    override fun postDestroyView(controller: Controller) {
        if (stateView.view != null) {
            coordinator?.removeView(stateView.view)
        }
    }

    fun showError(throwable: Throwable) {
        showError(throwable.message.orEmpty())
    }

    fun showError(message: String) {
        coordinator?.let {
            if (!stateView.isStateReady()) stateView.initView(it)
            stateView.showMessage(message)
            stateView.visible()
        }
    }

    private fun findCoordinator(root: View?): ViewGroup? {
        return when (root) {
            null -> null
            is CoordinatorLayout -> root
            else -> findCoordinator(root.parent as? View)
        }
    }
}