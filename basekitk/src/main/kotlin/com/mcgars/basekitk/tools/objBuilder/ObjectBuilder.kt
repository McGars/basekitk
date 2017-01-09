package com.mcgars.basekitk.tools.objBuilder

import android.database.Cursor

import com.google.gson.JsonArray

import org.json.JSONArray

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

/**
 * Refactored by Sviridov Alexander 13.03.2015
 */
class ObjectBuilder {

    /**
     * Work with JSONArray
     * add to exist list
     * @param array
     */
    fun <T : JsonParselable> addData(list: MutableList<T>, objCls: Class<T>, array: JSONArray, checker: ((item: T, position: Int) -> Boolean)? = null) {
        val list1 = getDataList(array, objCls, checker)
        list.addAll(list1)
    }

    /**
     * Get new List
     */
    fun <T : JsonParselable> getDataList(array: JSONArray, objCls: Class<T>, checker: ((item: T, position: Int) -> Boolean)? = null): List<T> {
        return simpleParce(array.length(), objCls, { t, i -> t.bind(array.optJSONObject(i)) }) {
            item, pos -> checker?.invoke(item, pos) ?: false
        }
    }

    /**
     * Work with Gson
     * add to exist list
     * @param array
     */
    fun <T : GsonParcelable> addData(list: MutableList<T>, objCls: Class<T>, array: JsonArray, checker: ((item: T, position: Int) -> Boolean)? = null) {
        val list1 = getDataList(array, objCls, checker)
        list.addAll(list1)
    }

    /**
     * Get new List
     */
    fun <T : GsonParcelable> getDataList(array: JsonArray, objCls: Class<T>, checker: ((item: T, position: Int) -> Boolean)? = null): List<T> {
        return simpleParce(array.size(), objCls, { t, i -> t.bind(array.get(i).asJsonObject) }) {
            item, pos -> checker?.invoke(item, pos) ?: false
        }
    }

    /**
     * Work with Cursor
     * add to exist list
     * @param array
     */
    fun <T : CursorParcelable> addData(list: MutableList<T>, objCls: Class<T>, array: Cursor?) {
        val list1 = parse(array, objCls, null)
        list.addAll(list1)
    }

    /**
     * Get new List
     */
    fun <T : CursorParcelable> getDataList(array: Cursor?, objCls: Class<T>, checker: ((item: T, position: Int) -> Boolean)? = null): List<T> {
        return parse(array, objCls, checker)
    }

    fun <T : CursorParcelable> getDataList(array: Cursor, objCls: Class<T>): List<T> {
        return getDataList(array, objCls, null)
    }

    private fun <T : CursorParcelable> parse(array: Cursor?, objCls: Class<T>, checker: ((item: T, position: Int) -> Boolean)?): List<T> {
        if (array == null) return emptyList()

        val list = ArrayList<T>(array.count)

        var cashColunm: HashMap<String, Int>? = null
        try {
            val preitem = objCls.newInstance()
            if (preitem is CursorParcelableHelper) {
                cashColunm = HashMap<String, Int>()
                val names = array.columnNames
                for (name in names) {
                    cashColunm.put(name, array.getColumnIndex(name))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var i = 0
        if (array.moveToFirst()) {
            do {
                try {
                    val item = objCls.newInstance()
                    if (cashColunm != null) {
                        (item as CursorParcelableHelper).setCursor(array, cashColunm)
                    }
                    item.bind(array)
                    if (checker != null && checker(item, i))
                        continue
                    list.add(item)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                i++
            } while (array.moveToNext())
        }
        return list

    }

    /**
     * Use static method bind for build item

     * @param _class
     * *
     * @param array
     */
    private fun <T : GsonParcelable> parseWithMethod(_class: Class<T>, array: JsonArray, checker: ((item: T, position: Int) -> Boolean)? = null): List<T> {
        return simpleParce(array.size(), _class, { t, i -> t.bind(array.get(i).asJsonObject) }) {
            item, pos -> checker?.invoke(item, pos) ?: false
        }
    }

    inline fun <T> simpleParce(size: Int, objCls: Class<T>, bind: (T, pos: Int) -> Unit, checker: ((item: T, position: Int) -> Boolean)): List<T> {
        val list = ArrayList<T>(size)
        var i = 0
        while (i < size) {
            try {
                val item = objCls.newInstance()
                bind(item, i)
                if (checker(item, i)) {
                    i++
                    continue
                }
                list.add(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            i++
        }
        return list
    }
}
