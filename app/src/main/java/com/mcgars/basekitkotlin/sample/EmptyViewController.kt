package com.mcgars.basekitkotlin.sample

import android.os.Bundle
import android.view.View
import com.gars.percents.base.BaseViewController
import com.mcgars.basekitk.tools.isNotEmpty
import com.mcgars.basekitkotlin.R
import kotlinx.android.synthetic.main.view_empry.view.*

/**
 * Created by Владимир on 12.01.2017.
 */
class EmptyViewController(bundle: Bundle? = null) : BaseViewController(bundle){

    constructor(text: String): this(Bundle().apply {
       putString("text", text)
    })

    override fun getLayoutId() = R.layout.view_empry

    override fun onReady(view: View) {
        args.getString("text").isNotEmpty { text ->
            view.tvEmpty.text = text
        }
    }
}