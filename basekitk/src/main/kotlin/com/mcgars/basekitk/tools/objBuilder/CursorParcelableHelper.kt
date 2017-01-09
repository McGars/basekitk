package com.mcgars.basekitk.tools.objBuilder

import android.annotation.TargetApi
import android.database.Cursor
import android.os.Build

import java.util.HashMap

/**
 * Created by Владимир on 28.09.2015.
 */
abstract class CursorParcelableHelper : CursorParcelable {
    private var cursor: Cursor? = null
    internal var cashColunm = HashMap<String, Int>()

    fun setCursor(cursor: Cursor) {
        this.cursor = cursor
        val names = cursor.columnNames
        for (name in names) {
            cashColunm.put(name, getColumn(name))
        }
    }

    fun setCursor(cursor: Cursor, cashColunm: HashMap<String, Int>) {
        this.cursor = cursor
        this.cashColunm = cashColunm
    }

    fun getColumn(columnName: String): Int {
        return cursor!!.getColumnIndex(columnName)
    }

    fun getInt(columnName: String): Int {
        return cursor!!.getInt(cashColunm[columnName]!!)
    }

    fun getString(columnName: String): String {
        return cursor!!.getString(cashColunm[columnName]!!)
    }

    fun getDowble(columnName: String): Double {
        return cursor!!.getDouble(cashColunm[columnName]!!)
    }

    fun getByte(columnName: String): ByteArray {
        return cursor!!.getBlob(cashColunm[columnName]!!)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun <C : Any> getValue(columnName: String): C {
        val type = cursor!!.getType(cashColunm[columnName]!!)
        if (type == Cursor.FIELD_TYPE_INTEGER)
            return getInt(columnName) as C
        else if (type == Cursor.FIELD_TYPE_FLOAT)
            return getDowble(columnName) as C
        else if (type == Cursor.FIELD_TYPE_BLOB)
            return getByte(columnName) as C
        else
            return getString(columnName) as C
    }
}
