package com.mcgars.basekitk.tools.objBuilder

import android.database.Cursor

/**
 * Created by Владимир on 28.09.2015.
 */
interface CursorParcelable {
    fun bind(cursor: Cursor)
}
