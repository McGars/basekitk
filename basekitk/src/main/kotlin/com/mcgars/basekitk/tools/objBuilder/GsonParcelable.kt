package com.mcgars.basekitk.tools.objBuilder

import com.google.gson.JsonObject

/**
 * Created by Владимир on 14.10.2015.
 */
interface GsonParcelable {
    fun bind(cursor: JsonObject)
}
