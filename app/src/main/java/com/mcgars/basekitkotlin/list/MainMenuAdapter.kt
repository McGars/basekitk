package com.mcgars.basekitkotlin.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mcgars.basekitk.features.recycler.ListRecycleAdapter
import com.mcgars.basekitkotlin.R

/**
 * Created by gars on 13.05.2017.
 */
class MainMenuAdapter(
        context: Context,
        onItemClickListener: ((item: MenuItem, position: Int) -> Unit)?)
    : ListRecycleAdapter<MenuItem, RecyclerView.ViewHolder>(context, mutableListOf(), android.R.layout.simple_expandable_list_item_1, onItemClickListener) {

    init {
        addItem(MenuItem(DRAWER, context.getString(R.string.drawer_title)))
        addItem(MenuItem(PULLABLE, context.getString(R.string.pullabe_title)))
        addItem(MenuItem(TABS, context.getString(R.string.tabs_title)))
    }

    override fun getViewHolder(view: View, type: Int) = object : RecyclerView.ViewHolder(view) {}

    override fun setValues(h: RecyclerView.ViewHolder, item: MenuItem, position: Int) = with((h.itemView as TextView)) {
        text = item.text
    }

    companion object {
        val DRAWER = 0
        val PULLABLE = 1
        val TABS = 2
    }
}

data class MenuItem(
        var id: Int,
        var text: String? = null
)