package com.mcgars.basekitk.tools

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

/**
 * Created by Феофилактов on 25.12.2014.
 */
class WrapperUiTool {

    private var view: View by Delegates.notNull()
    var wrapperLayout: ViewGroup? = null
    private var settings: ((v: View) -> Unit)? = null

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
    constructor(view: View, wrapperLayout: ViewGroup) {
        this.view = view
        this.wrapperLayout = wrapperLayout
    }

    /**
     * Добавляем в контейнер с конкретным id

     * @param inputId
     * *
     * @return
     */
    fun insertAtId(@IdRes inputId: Int): WrapperUiTool {
        settings?.invoke(view)
        (wrapperLayout?.findViewById<ViewGroup>(inputId))?.addView(view)
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
        wrapperLayout!!.addView(view, position)
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
    fun setOnSettingsBeforeSet(settings: (v: View) -> Unit): WrapperUiTool {
        this.settings = settings
        return this
    }

    /**
     * Удаляем оригинальное вью и вместо него ставим враппер
     */
    private fun rebuildParent() {
        view.parent?.run {
            with(view.parent as ViewGroup) {
                val pos = indexOfChild(view)
                addView(wrapperLayout, pos)
                removeView(view)
            }
        }
    }

    fun covertLayout(wrapperLayout: Int) {
        val inf = LayoutInflater.from(view.context)
        this.wrapperLayout = inf.inflate(wrapperLayout, view.parent as ViewGroup, false) as ViewGroup
    }

}
