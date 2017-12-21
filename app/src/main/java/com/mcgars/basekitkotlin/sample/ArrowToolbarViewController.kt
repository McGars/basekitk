package com.mcgars.basekitkotlin.sample

import android.view.MenuItem
import android.view.View
import com.mcgars.basekitk.tools.snack
import com.mcgars.basekitkotlin.R
import com.mcgars.basekitkotlin.decorator.ToolbarColorDecorator
import ru.mos.helloworldk.features.animatorHandlers.CircularRevealChangeHandler
import ru.mos.helloworldk.features.animatorHandlers.CircularRevealChangeHandlerCompat

/**
 * Created by gars on 07.08.17.
 */
class ArrowToolbarViewController : EmptyViewController() {

    override fun getTitleInt() = R.string.arrow

    init {
        setHasOptionsMenu(true)
        overridePushHandler(CircularRevealChangeHandlerCompat().apply {
            halfPosition = CircularRevealChangeHandler.TOP_CENTER
        })
        overridePopHandler(CircularRevealChangeHandlerCompat().apply {
            halfPosition = CircularRevealChangeHandler.TOP_CENTER
        })
        addDecorator(ToolbarColorDecorator(this))
    }

    override fun onReady(view: View) {
        super.onReady(view)
        setHomeArrow(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            view?.snack("Home pressed")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}