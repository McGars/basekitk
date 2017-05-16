package com.mcgars.basekitkotlin.loaderController

import android.view.View
import com.gars.percents.base.BaseViewController
import com.mcgars.basekitk.tools.Timer
import com.mcgars.basekitkotlin.R

/**
 * Created by Владимир on 13.01.2017.
 */
class LoaderViewController : BaseViewController(){

    override fun getTitleInt() = R.string.loader_title

    override fun getLayoutId() = R.layout.view_empry

    override fun onReady(view: View) {
        loader.show()
        Timer(2000) {
            loader.hide()
        }.start()
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        loader.hide()
    }

}