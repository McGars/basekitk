package com.mcgars.basekitk.tools

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

/**
 * Created by Владимир on 01.04.2015.
 */
abstract class SimpleBaseAdapter<T, H>(context: Context, list: List<T>, @LayoutRes private val layout: Int) : ArrayAdapter<T>(context, 0, list) {
    protected val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    /**
     * На случай когда надо разные типы вьюшек строить
     * @param item
     * *
     * @return
     */
    protected fun getLayout(item: T): Int {
        return layout
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var v = view
        val viewHolder: H
        val item = requireNotNull(getItem(position))

        if (v == null) {
            v = inflater.inflate(getLayout(item), parent, false)
            viewHolder = getViewHolder(v)
            setListeners(viewHolder)
            v.tag = viewHolder
        } else {
            viewHolder = v.tag as H
        }
        // заполняем значения
        setValues(viewHolder, item, position)
        viewListener(v!!, item)
        return v
    }

    /**
     * Bind values to views
     * @param holder передается готовый экземпляр класса [.getViewHolder]
     * *
     * @param item передается [.getItem]
     * *
     * @param position позиция в списке элементов
     */
    abstract fun setValues(holder: H, item: T, position: Int)

    /**
     * Инициализация ViewHolder
     * @param v передается inflater.inflate(getWrapperLayout(item), parent, false)
     * *
     * @return ViewHolder
     */
    abstract fun getViewHolder(v: View): H

    /**
     * Тут выставляем все листенеры для вьюшек
     * вызываеться в момент создания view inflater.inflate(getWrapperLayout(item), parent, false)
     * @param holder передается готовый экземпляр класса [.getViewHolder]
     */
    open fun setListeners(holder: H) {
    }

    /**
     * Вызываеться после [.setValues]
     * Если нужно что то сделать с основной вьюшкой
     * @param v основная View
     * *
     * @param item передается [.getItem]
     */
    open fun viewListener(v: View, item: T) {
    }
}
