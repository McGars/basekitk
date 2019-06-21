package com.mcgars.basekitk.features.statemashine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.internal.Experimental
import com.mcgars.basekitk.R
import com.mcgars.basekitk.tools.gone
import com.mcgars.basekitk.tools.visible

@Experimental
class RefreshStateView(
        private val onRefresh: () -> Unit
) : StateView {

    override var view: View? = null

    private var basekitStateViewMessage: TextView? = null
    private var basekitStateViewButton: Button? = null
    private val layout: Int = R.layout.basekit_state_view

    override fun initView(coordinator: ViewGroup) {
        view = LayoutInflater.from(coordinator.context).inflate(layout, coordinator, false).also {
            basekitStateViewMessage = it.findViewById(R.id.basekitStateViewMessage)
            basekitStateViewButton = it.findViewById<Button>(R.id.basekitStateViewButton).apply {
                setOnClickListener {
                    view.gone()
                    onRefresh.invoke()
                }
            }
            coordinator.addView(it)
        }
    }

    override fun visible() {
        view.visible()
    }

    override fun isStateReady(): Boolean = view != null

    override fun showMessage(message: String) {
        basekitStateViewMessage?.text = message
    }

}