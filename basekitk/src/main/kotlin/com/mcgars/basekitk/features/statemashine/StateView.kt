package com.mcgars.basekitk.features.statemashine

import android.view.View
import android.view.ViewGroup


interface StateView {
    var view: View?
    fun initView(coordinator: ViewGroup)
    fun visible()
    fun isStateReady(): Boolean
    fun showMessage(message: String)
}