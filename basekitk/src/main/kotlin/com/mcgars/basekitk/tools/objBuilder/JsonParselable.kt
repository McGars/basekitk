package com.mcgars.basekitk.tools.objBuilder

import org.json.JSONObject

/**
 * Created on 15.09.2014.
 */
interface JsonParselable {
    fun bind(json: JSONObject)
}

