package com.mcgars.basekitk.features.drawer

import android.content.Context
import com.mcgars.basekitk.tools.SimpleBaseAdapter


abstract class BaseDrawerAdapter<T, H>(context: Context, list: List<T>, layoutId: Int) : SimpleBaseAdapter<T, H>(context, list, layoutId) {

    var selectedPosition = NON_SELECTED
        protected set
    var selectedId: Int = 0
        protected set

    init {
        initData()
    }

    fun setSelected(menuId: Int, selectedPos: Int = NON_SELECTED) {
        if (selectedId == menuId) return

        selectedId = menuId
        this.selectedPosition = selectedPos
        notifyDataSetChanged()
    }

    /**
     * Заполняем список менюшками
     */
    protected abstract fun initData()

    companion object {
        val NON_SELECTED = -1
    }
}
