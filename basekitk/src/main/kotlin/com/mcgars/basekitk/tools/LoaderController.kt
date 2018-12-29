package com.mcgars.basekitk.tools

import android.os.Build
import android.support.annotation.LayoutRes
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
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
class LoaderController constructor(groupToAdd: ViewGroup?) {

    private var root: ViewGroup? = null
    private var loader: View? = null
    private var isShown: Boolean = false
    private var layout: Int = 0

    init {
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        root = groupToAdd

        val coordinator = findCoordinator(root)
        if (coordinator != null)
            root = coordinator

        if (root != null && root !is RelativeLayout && root !is FrameLayout && root !is CoordinatorLayout) {

            val parent = root!!.parent as ViewGroup

            val rootViewNew = FrameLayout(root!!.context).apply {
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

    fun findCoordinator(root: View?): ViewGroup? {
        return when (root) {
            null -> return null
            is CoordinatorLayout -> return root
            else -> findCoordinator(root.parent as? View)
        }
    }

    private fun init() {
        root?.let {
            loader = LayoutInflater.from(it.context).inflate(if (layout != 0) layout else R.layout.basekit_global_loader, root, false)
            root?.addView(loader)
        }
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
        return AnimationUtils.loadAnimation(loader!!.context, anim)
    }

    fun setLoaderView(@LayoutRes layout: Int) {
        this.layout = layout
    }
}
