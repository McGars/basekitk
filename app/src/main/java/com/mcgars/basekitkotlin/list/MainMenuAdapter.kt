package com.mcgars.basekitkotlin.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mcgars.basekitk.features.recycler.ListRecycleAdapter

/**
 * Created by gars on 13.05.2017.
 */
class MainMenuAdapter(
        context: Context,
        list: MutableList<MenuItem>,
        onItemClickListener: ((item: MenuItem, position: Int) -> Unit)?)
    : ListRecycleAdapter<MenuItem, RecyclerView.ViewHolder>(context, list, android.R.layout.simple_list_item_1, onItemClickListener) {

    override fun getViewHolder(view: View, type: Int) = object : RecyclerView.ViewHolder(view) {}

    override fun setValues(h: RecyclerView.ViewHolder, item: MenuItem, position: Int) = with((h.itemView as TextView)) {
        text = item.text
    }
}

data class MenuItem(
        var id: Int,
        var text: String? = null
)