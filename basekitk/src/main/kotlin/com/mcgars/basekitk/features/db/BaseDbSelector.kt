package com.mcgars.basekitk.features.db

import android.content.Context
import android.database.Cursor
import android.text.TextUtils
import android.util.Log

/**
 * Created by Владимир on 19.08.2015.
 * Базовая настройка для работы с базой данных sqlite
 * На основе annotatedSQL библиотеки
 */
abstract class BaseDbSelector(protected var mContext: Context, sqlDataProvider: ToolContentProvider) {
    protected val core: SQLBuilder = SQLBuilder(mContext, sqlDataProvider)

    fun notifyUri(uri: String) {
        core.notifyUri(uri)
    }

    fun getCore(uriContent: String) = core.init(uriContent)

    fun delete(contentUri: String, id: String) {
        if (TextUtils.isEmpty(id)) {
            Log.d(TAG, "can't delete row from $contentUri if id is empty")
            return
        }
        core.init(contentUri)
                .where("_id", id)
                .delete()
    }

    fun deleteBy(contentUri: String, column: String, value: String) {
        if (TextUtils.isEmpty(value)) {
            Log.d(TAG, "can't delete row from $contentUri if value is empty")
            return
        }
        core.init(contentUri)
                .where(column, value)
                .delete()
    }

    fun deleteAll(contentUri: String) {
        core.init(contentUri).delete()
    }

    companion object {
        private val TAG = "BaseDbSelector"

        fun getValue(cur: Cursor, column: String): String {
            return cur.getString(cur.getColumnIndex(column))
        }

        fun getInt(cur: Cursor, column: String): Int {
            return cur.getInt(cur.getColumnIndex(column))
        }
    }
}
