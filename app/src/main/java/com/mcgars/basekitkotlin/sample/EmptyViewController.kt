package com.mcgars.basekitkotlin.sample

import android.os.Bundle
import android.view.View
import com.mcgars.basekitk.features.base.BaseViewController
import com.mcgars.basekitk.tools.isNotEmpty
import com.mcgars.basekitkotlin.R
import kotlinx.android.synthetic.main.view_empry.view.*

/**
 * Created by Владимир on 12.01.2017.
 */
open class EmptyViewController(bundle: Bundle? = null) : BaseViewController(bundle){

    constructor(text: String, isCustom: Boolean = false): this(Bundle().apply {
       putString("text", text)
       putBoolean("isCustom", isCustom)
    })

    override var isCustomLayout = args.getBoolean("isCustom")

    override fun getLayoutId() = R.layout.view_empry

    override fun getTitle() = args.getString("text")

    var homeArrowBool = false
    override fun onReady(view: View) {
//        setHomeArrow(!isCustomLayout)
        args.getString("text").isNotEmpty { text ->
            view.tvEmpty.text = text
        }
        view.tvEmpty.setOnClickListener {
            homeArrowBool = !homeArrowBool
            setHomeArrow(homeArrowBool)
        }
    }



}