package com.mcgars.basekitkotlin.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcgars.basekitk.features.recycler2.AbsListItemAdapterDelegate
import com.mcgars.basekitk.features.recycler2.KitAdapter

/**
 * Created by gars on 13.05.2017.
 */
class MainMenuDelegate(
        private val onItemClickListener: ((item: MenuItem, position: Int) -> Unit)
) : AbsListItemAdapterDelegate<MenuItem, Any, RecyclerView.ViewHolder>() {

    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is MenuItem
    }

    override fun onBindViewHolder(item: MenuItem, viewHolder: RecyclerView.ViewHolder) {
        with(viewHolder.itemView as TextView) {
            text = item.text
        }
    }

    override fun onCreateViewHolder(kitAdapter: KitAdapter<Any>, parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return object : RecyclerView.ViewHolder(view) {}.apply {
            itemView.setOnClickListener {
                val menuItem = kitAdapter.getItem(adapterPosition) as? MenuItem ?: return@setOnClickListener
                onItemClickListener.invoke(menuItem, adapterPosition)
            }
        }
    }

}

data class MenuItem(
        var id: Int,
        var text: String? = null
)