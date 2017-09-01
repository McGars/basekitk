package com.mcgars.basekitk.features.simplerecycler

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.recycler.HeaderRecyclerAdapter
import com.mcgars.basekitk.features.recycler.ListRecycleAdapter
import com.mcgars.basekitk.tools.gone
import java.util.*

/**
 * Created by gars on 05.01.2017.
 */

class SimpleListAdapter<T : SimpleItem>(
        context: Context,
        items: MutableList<T>,
        layout: Int = R.layout.basekit_view_simple_list,
        onItemClickListener: ((item: SimpleItem, position: Int)->Unit)? = null) :
        HeaderRecyclerAdapter<T, SimpleListHolder>(context, items, layout, onItemClickListener) {

    override fun getViewHolder(view: View, type: Int) = SimpleListHolder(view)

    override fun setValues(holder: SimpleListHolder, item: T, i: Int) {
        with(holder) {
            tvTitle?.text = item.title
            tvDescription?.text = item.description
            tvDescription?.gone(TextUtils.isEmpty(item.description))
        }
    }
}
