package com.mcgars.basekitk.features.recycler2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mcgars.basekitk.R

/**
 * Created by gars on 20.04.2017.
 */
class AdapterDelegateLoader<T>(context: Context, list: MutableList<T>) : AdapterDelegateHeader<T>(list) {

    init {
        addDelegate(AdapterViewLoader(context))
    }
}

class AdapterViewLoader<T>(context: Context) : AdapterDelegate<MutableList<T>> {

    private val inflater = LayoutInflater.from(context)

    override fun isForViewType(items: MutableList<T>, position: Int): Boolean {
        return items[position] is BottomLoader
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
                inflater.inflate(R.layout.basekit_view_loading, parent, false)) {}
    }

    override fun onBindViewHolder(items: MutableList<T>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {

    }

}

class BottomLoader