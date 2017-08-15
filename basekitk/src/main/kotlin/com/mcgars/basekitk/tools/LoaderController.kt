package com.mcgars.basekitk.tools

import android.app.Activity
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout

import com.mcgars.basekitk.R


/**
 * Created by Владимир on 25.06.2014.
 */
class LoaderController @JvmOverloads constructor(private val activity: Activity, groupToAdd: ViewGroup? = null) {

    private var root: ViewGroup? = null
    private var loader: View? = null
    private var isShown: Boolean = false
    private var layout: Int = 0

    init {
        var height = ViewGroup.LayoutParams.WRAP_CONTENT

        root = groupToAdd
        if (root == null) {
            root = (activity.findViewById(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        val coordinator = findCoordinatorLayout(root!!)
        if (coordinator != null)
            root = coordinator

        if (root !is RelativeLayout && root !is FrameLayout && root !is CoordinatorLayout) {

            val parent = root!!.parent as ViewGroup

            val rootViewNew = FrameLayout(activity).apply {
                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                if (Build.VERSION.SDK_INT >= 16) {
                    fitsSystemWindows = root!!.fitsSystemWindows
                }
            }

            parent.run {
                indexOfChild(root).run {
                    removeView(root)
                    addView(rootViewNew, this)
                }
            }
            rootViewNew.addView(root)
            this.root = rootViewNew
        }
    }

    private fun findCoordinatorLayout(root: ViewGroup): ViewGroup? {
        if (root is CoordinatorLayout)
            return root
        for (i in 0..root.childCount - 1) {
            val v = root.getChildAt(i)
            if (v is CoordinatorLayout)
                return v
            if (v is ViewGroup) {
                val chaildView = findCoordinatorLayout(v)
                if (chaildView is CoordinatorLayout)
                    return chaildView
            }
        }
        return null
    }

    private fun init() {
        loader = activity.layoutInflater.inflate(if (layout != 0) layout else R.layout.basekit_global_loader, root, false)
        root?.addView(loader)
    }

    fun show() {
        if (isShown) return

        if (loader == null) init()

        isShown = true
        loader?.run {
            visibility = View.VISIBLE
            startAnimation(getAnimation(R.anim.abc_fade_in))
        }
    }

    fun hide() {
        if (!isShown || loader == null)
            return

        isShown = false
        val animation = getAnimation(R.anim.abc_fade_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                loader?.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        loader?.startAnimation(animation)
    }

    private fun getAnimation(anim: Int): Animation {
        loader?.clearAnimation()
        return AnimationUtils.loadAnimation(activity, anim)
    }

    fun setLoaderView(@LayoutRes layout: Int) {
        this.layout = layout
    }
}
