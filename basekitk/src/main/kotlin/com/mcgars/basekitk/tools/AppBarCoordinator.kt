package com.mcgars.basekitk.tools

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mcgars.basekitk.R

/**
 * Created by Владимир on 28.03.2016.
 */
class AppBarCoordinator(private val activity: Activity) {
    private val inflater: LayoutInflater = LayoutInflater.from(activity)
    internal var selected = DEFAULT_APP_BAR

    fun setAppBar(appBarLayout: Int): Boolean {

        if (selected == appBarLayout)
            return false

        selected = appBarLayout
        val v = activity.findViewById(R.id.appBarlayout)
        v?.let {
            val parent = v.parent as ViewGroup
            val position = parent.indexOfChild(v)
            parent.removeView(v)
            parent.addView(inflater.inflate(appBarLayout, parent, false), position)
            return true
        }

        return false
    }

    companion object {
        var DEFAULT_APP_BAR = R.layout.basekit_toolbar
    }

}
