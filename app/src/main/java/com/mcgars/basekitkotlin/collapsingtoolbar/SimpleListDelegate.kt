package com.mcgars.basekitkotlin.collapsingtoolbar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mcgars.basekitk.features.recycler2.AbsListItemAdapterDelegate
import com.mcgars.basekitk.features.recycler2.KitAdapter
import com.mcgars.basekitkotlin.R
import kotlinx.android.synthetic.main.item_list.view.*


class SimpleListDelegate(
        private val onItemClickListener: ((item: Item) -> Unit)
) : AbsListItemAdapterDelegate<SimpleListDelegate.Item, Any, RecyclerView.ViewHolder>() {

    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is Item
    }

    override fun onBindViewHolder(item: Item, viewHolder: RecyclerView.ViewHolder) {
        with(viewHolder.itemView) {
            text1.text = item.title
            text2.text = item.subtitle
        }

    }

    override fun onCreateViewHolder(kitAdapter: KitAdapter<Any>, parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return object : RecyclerView.ViewHolder(view) {}.apply {
            itemView.setOnClickListener {
                val item = kitAdapter.getItem(adapterPosition) as? Item ?: return@setOnClickListener
                onItemClickListener.invoke(item)
            }
        }
    }

    data class Item(
            val title: String,
            val subtitle: String
    )

}