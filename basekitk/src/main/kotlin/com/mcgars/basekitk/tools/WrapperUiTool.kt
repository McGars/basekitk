package com.mcgars.basekitk.tools

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

/**
 * Created by Феофилактов on 25.12.2014.
 */
class WrapperUiTool {

    private var view: View by Delegates.notNull()
    internal var positionView: Int = 0
    var layout: ViewGroup? = null
    private var parent: ViewGroup? = null
    private var settings: ((v: View)->Unit)? = null

    /**

     * @param view
     * *
     * @param wrapperLayout это враппер, который обволакивает нужную вьюху
     */
    constructor(view: View, @LayoutRes wrapperLayout: Int) {
        this.view = view
        covertLayout(wrapperLayout)
    }

    /**

     * @param view
     * *
     * @param layout это враппер, который обволакивает нужную вьюху
     */
    constructor(view: View, layout: ViewGroup) {
        this.view = view
        this.layout = layout
    }

    /**
     * Добавляем в контейнер с конкретным id

     * @param inputId
     * *
     * @return
     */
    fun insertAtId(@IdRes inputId: Int): WrapperUiTool {
        settings?.invoke(view)
        (layout!!.findViewById(inputId) as ViewGroup).addView(view)
        rebuildParent()
        return this
    }

    /**
     * Добавляем в конкретную позицию во врапере

     * @param position
     * *
     * @return
     */
    fun insertAtPosition(position: Int): WrapperUiTool {
        settings?.invoke(view)
        rebuildParent()
        layout!!.addView(view, position)
        return this
    }

    fun insert(): WrapperUiTool {
        insertAtPosition(0)
        return this
    }

    /**
     * Дополнительная настройка перед вставкой вьюхи

     * @param settings
     * *
     * @return
     */
    fun setOnSettingsBeforeSet(settings: (v: View)->Unit): WrapperUiTool {
        this.settings = settings
        return this
    }

    /**
     * Удаляем оригинальное вью и вместо него ставим враппер
     */
    private fun rebuildParent() {
        parent?.run {
            removeViewAt(positionView)
            addView(layout, positionView)
        }
    }

    fun covertLayout(wrapperLayout: Int) {
        val inf = LayoutInflater.from(view.context)
        layout = inf.inflate(wrapperLayout, view.parent as ViewGroup, false) as ViewGroup
    }
}
