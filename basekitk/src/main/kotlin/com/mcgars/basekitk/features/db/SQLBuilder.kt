package com.mcgars.basekitk.features.db


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.support.v4.content.CursorLoader
import android.text.TextUtils
import android.util.Log

import java.util.ArrayList
import java.util.Arrays

class SQLBuilder(val context: Context, val provider: ToolContentProvider, contentUrl: String? = null) {
    private var sqlQuery: StringBuilder? = null
    private var GROUP_BY: String? = null
    private var ORDER_BY: String? = null
    private var LIMIT = 0
    private var COLUMNS: Array<String>? = null
    private var notFirstSet = 0
    private var insert = false
    private var type_search = Search.AND
    private var contentUrl: String? = null
    private val cr: ContentResolver by lazy { context.contentResolver }
    private var cv: ContentValues? = null
    private var selectArg: MutableList<String> = ArrayList()

    enum class Search {
        AND, OR
    }

    init {
        clearSQlQuery()
    }

    fun init(uriContent: String): SQLBuilder {
        return SQLBuilder(context, provider).apply {
            if (!uriContent.isNullOrEmpty()) setContentUrl(uriContent) else
            if (!contentUrl.isNullOrEmpty()) setContentUrl(contentUrl!!)
        }
    }

    fun setContentUrl(contentUrl: String): SQLBuilder {
        this.contentUrl = contentUrl
        return this
    }

    fun clearSQlQuery() {
        sqlQuery = StringBuilder()
        cv = ContentValues()
        selectArg = ArrayList<String>()
    }

    /**
     * @return
     */
    fun select(columns: Array<String>): SQLBuilder {
        COLUMNS = columns
        return this
    }

    operator fun set(column: String, value: Any?) : SQLBuilder {
        if (value == null)
            cv!!.putNull(column)
        else if (value is String)
            cv!!.put(column, value as String?)
        else if (value is Double)
            cv!!.put(column, value as Double?)
        else if (value is Long)
            cv!!.put(column, value as Long?)
        else if (value is Int)
            cv!!.put(column, value as Int?)
        else if (value is Float)
            cv!!.put(column, value as Float?)
        else if (value is Boolean)
            cv!!.put(column, value as Boolean?)
        else if (value is ByteArray)
            cv!!.put(column, value as ByteArray?)
        else if (value is Byte)
            cv!!.put(column, value as Byte?)
        return this
    }

    fun set(cv: ContentValues) = with(this) {
        this.cv = cv
    }

    // помошник построения where
    private fun buildWhere(type: String, column: String, compare: String, value: String): SQLBuilder {

        if (compare == LIKE) {
            sqlQuery!!.append(type).append(column).append(" ")
                    .append(LIKE).append(VALUE)
            selectArg.add("%$value%")
        } else {
            sqlQuery!!.append(type).append(column).append(compare)
                    .append(VALUE)
            selectArg.add(value)
        }
        return this
    }

    private fun clearValue(value: String?): String {
        if (value == null)
            return ""
        return value.replace("'", "")
    }

    // where
    fun where(column: String, compare: String, value: String): SQLBuilder {
        buildWhere(WHERE, column, compare, value)
        return this
    }

    fun where(column: String, value: Boolean): SQLBuilder {
        val v = if (value) "1" else "0"
        sqlQuery!!.append(WHERE).append(column).append(EQUAL)
                .append(VALUE)
        selectArg.add(v)
        return this
    }

    fun where(column: String, value: String): SQLBuilder {
        sqlQuery!!.append(WHERE).append(column).append(EQUAL)
                .append(VALUE)
        selectArg.add(value)
        return this
    }

    fun where(where: String): SQLBuilder {
        sqlQuery!!.append(WHERE)
                .append(where)
        return this
    }

    // IN AND NOT IN
    fun whereIn(column: String, value: String): SQLBuilder {
        sqlQuery!!.append(WHERE).append(column).append(IN)
                .append(clearValue(value)).append(") ")
        return this
    }

    fun whereNotIn(column: String, value: String): SQLBuilder {
        sqlQuery!!.append(WHERE).append(column).append(NOTIN)
                .append(clearValue(value)).append(") ")
        return this
    }

    fun andWhereIn(column: String, value: String): SQLBuilder {
        sqlQuery!!.append(AND).append(column)
                .append(IN).append(clearValue(value)).append(") ")
        return this
    }

    fun orWhereNotIn(column: String, value: String): SQLBuilder {
        sqlQuery!!.append(OR).append(column)
                .append(NOTIN).append(clearValue(value)).append(") ")
        return this
    }

    fun orWhereIn(column: String, value: String): SQLBuilder {
        sqlQuery!!.append(OR).append(column)
                .append(IN).append(clearValue(value)).append(") ")
        return this
    }

    fun andWhereNotIn(column: String, value: String): SQLBuilder {
        sqlQuery!!.append(AND).append(column)
                .append(NOTIN).append(clearValue(value)).append(") ")
        return this
    }

    // AND
    fun andWhere(column: String, compare: String, value: String): SQLBuilder {
        buildWhere(AND, column, compare, value)
        return this
    }

    fun andWhere(column: String, value: String): SQLBuilder {
        buildWhere(AND, column, EQUAL, value)
        return this
    }

    fun andWhere(column: String, value: Boolean): SQLBuilder {
        val v = if (value) "1" else "0"
        sqlQuery!!.append(AND).append(column).append(EQUAL)
                .append(VALUE)
        selectArg.add(v)
        return this
    }

    fun andWhere(where: String): SQLBuilder {
        sqlQuery!!.append(AND).append(where)
        return this
    }

    // search
    fun setTypeSearch(type: Search): SQLBuilder {
        type_search = type
        return this
    }

    fun search(column: String, text: String): SQLBuilder {
        val query_search = prepareSearch(column, text)
        if (query_search != null) {
            where(query_search)
        }
        return this
    }

    fun andSearch(column: String, text: String): SQLBuilder {
        val query_search = prepareSearch(column, text)
        if (query_search != null) {
            andWhere(query_search)
        }
        return this
    }

    fun orSearch(column: String, text: String): SQLBuilder {
        val query_search = prepareSearch(column, text)
        if (query_search != null) {
            orWhere(query_search)
        }
        return this
    }

    @SuppressLint("DefaultLocale")
    fun prepareSearch(column: String, text: String): String? {
        return prepareSearch(column, text, type_search, selectArg)
    }

    // OR
    fun orWhere(column: String, compare: String, value: String): SQLBuilder {
        buildWhere(OR, column, compare, value)
        return this
    }

    fun orWhere(column: String, value: Boolean): SQLBuilder {
        val v = if (value) "1" else "0"
        sqlQuery!!.append(OR).append(column).append(EQUAL)
                .append(VALUE)
        selectArg.add(v)
        return this
    }

    fun orWhere(where: String): SQLBuilder {
        sqlQuery!!.append(OR).append(where)
        return this
    }

    // group
    fun groupBy(groupBy: String): SQLBuilder {
        GROUP_BY = groupBy
        return this
    }

    // order
    fun orderBy(orderBy: String): SQLBuilder {
        ORDER_BY = orderBy
        return this
    }

    // limit
    fun limit(limit: Int): SQLBuilder {
        LIMIT = limit
        return this
    }

    fun getSqlQuery(): String {
        val str = StringBuilder("SELECT ")
        str.append(COLUMNS!!.toString()).append(" FROM ").append(contentUrl).append(" ")
        str.append(sqlQuery!!.toString())
        return str.toString()
    }

    private fun log(type: String) {
        if (isDebug) {

            val str = StringBuilder("query=").append(type).append("table=")
            str.append(contentUrl)

            if (COLUMNS != null)
                str.append(" COLUMNS ").append(Arrays.toString(COLUMNS))

            if (sqlQuery!!.length > 0)
                str.append(WHERE).append("=").append(whereInstance)
            if (selectArg.size > 0) {
                val array = selectArg.toTypedArray()
                str.append(" selection=").append(Arrays.toString(array))
            }

            Log.d(this.javaClass.simpleName, str.toString())
        }
    }

    private val whereInstance: String
        get() {
            var sql = sqlQuery!!.toString().trim { it <= ' ' }
            val arr = sql.split(" ".toRegex(), 2).toTypedArray()
            if (arr.size > 1) {
                sql = arr[1]
            }
            return sql
        }

    private fun getUri(notify: Boolean): Uri {

        //        Log.d("contentUrltest", ""+contentUrl);

        if (LIMIT == 0)
            return if (notify) provider!!.getContentUri(contentUrl) else provider!!.getNoNotifyContentUri(contentUrl)
        return provider!!.getContentWithLimitUri(contentUrl, LIMIT)
    }

    @JvmOverloads fun delete(notify: Boolean = true) {
        log("DELETE")
        cr!!.delete(getUri(notify), whereInstance, selection)
    }

    @JvmOverloads fun update(notify: Boolean = true) {
        log("UPDATE")
        cr!!.update(getUri(notify), cv, whereInstance, selection)
    }

    @JvmOverloads fun insert(notify: Boolean = true): String {
        log("INSERT")
        val uri = cr!!.insert(getUri(notify), cv)
        return uri!!.pathSegments[1]
    }

    val cursor: Cursor
        get() {
            log("SELECT")
            return cr!!.query(
                    getUri(true),
                    COLUMNS,
                    whereInstance,
                    selection,
                    ORDER_BY)
        }

    val cursorNoNotify: Cursor
        get() {
            log("SELECT")
            return cr!!.query(getUri(false), COLUMNS, whereInstance, selection, ORDER_BY)
        }

    val cursorLoader: CursorLoader
        get() {
            log("SELECT")
            return CursorLoader(
                    context!!,
                    if (GROUP_BY != null)
                        provider!!.getContentUriGroupBy(contentUrl, GROUP_BY)
                    else if (LIMIT == 0)
                        provider!!.getContentUri(contentUrl)
                    else
                        provider!!.getContentWithLimitUri(contentUrl, LIMIT),
                    COLUMNS, whereInstance, selection, ORDER_BY
            )
        }

    private val selection: Array<String>?
        get() {
            if (selectArg.size == 0)
                return null
            return selectArg.toTypedArray()
        }

    private fun clear() {
        GROUP_BY = null
        ORDER_BY = null
        COLUMNS = null
        LIMIT = 0
        insert = false
        notFirstSet = 0
        clearSQlQuery()
    }

    fun notifyUri(uri: String) {
        cr!!.notifyChange(provider!!.getContentUri(uri), null)
    }

    @SuppressLint("DefaultLocale")
    fun prepareSearch(column: String, text: String, type_search: Search, selectArg: MutableList<String>): String? {
        var text = text
        text = text.trim { it <= ' ' }
        if (text.isEmpty())
            return null

        val search_list = text.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val query_search = ArrayList<String>()
        var first_later = ""
        for (i in search_list.indices) {
            first_later = search_list[i].substring(0, 1).toUpperCase()
            if (search_list[i].length > 1) {
                first_later += search_list[i].substring(1)
                query_search.add(first_later)
            }
        }
        if (query_search.size > 0) {
            var _prepare = ""
            for (search_text in query_search) {
                _prepare += type_search.toString() + " (" + column + LIKE + "?" +
                        OR + column + LIKE + "?) "
                selectArg.add("%$search_text%")
                selectArg.add("%" + search_text.toLowerCase() + "%")
            }
            return "(" + _prepare.substring(4) + ")"
        }
        return null
    }

    companion object {
        var isDebug = false

        private val WHERE = " WHERE "
        private val IN = " IN ("
        private val NOTIN = " NOT IN ("
        private val VALUE = " ? "
        var AND = " AND "
        var OR = " OR "
        var EQUAL = "="
        var NOTEQUAL = "!="
        var ISNULL = " IS NULL "
        var NOTNULL = " NOT NULL "
        var LESS = "<"
        var MORE = ">"
        var LIKE = " LIKE "
        var NOTLIKE = " NOT LIKE "
        var LESSANDEQUAL = "<="
        var MOREANDEQUAL = ">="
    }

}
