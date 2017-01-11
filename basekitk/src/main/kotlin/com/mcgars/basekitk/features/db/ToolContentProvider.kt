package com.mcgars.basekitk.features.db

/**
 * Created by Феофилактов on 24.11.2014.
 */
abstract class ToolContentProvider {
    abstract fun getContentUri(path: String): android.net.Uri

    abstract fun getContentUriGroupBy(path: String, groupBy: String): android.net.Uri

    abstract fun getContentWithLimitUri(path: String, limit: Int): android.net.Uri

    abstract fun getNoNotifyContentUri(path: String): android.net.Uri

    abstract fun getNoNotifyContentUri(path: String, id: Long): android.net.Uri

    companion object {
        fun setIsDebug(debug: Boolean) {
            SQLBuilder.isDebug = debug
        }
    }

}
