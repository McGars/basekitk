package com.mcgars.basekitkotlin.sample

import android.view.View
import com.mcgars.basekitkotlin.R

/**
 * Created by gars on 07.08.17.
 */
class ArrowToolbarViewController : EmptyViewController() {

    override fun getTitleInt() = R.string.arrow

    override fun onReady(view: View) {
        super.onReady(view)
        setHomeArrow(true)
    }
}