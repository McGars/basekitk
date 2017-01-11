package com.mcgars.basekitk.features.db;

/**
 * Created by Феофилактов on 24.11.2014.
 */
public abstract class ToolContentProvider {
    public abstract android.net.Uri getContentUri(String path);

    public abstract android.net.Uri getContentUriGroupBy(String path, String groupBy);

    public abstract android.net.Uri getContentWithLimitUri(String path, int limit);

    public abstract android.net.Uri getNoNotifyContentUri(String path);

    public abstract android.net.Uri getNoNotifyContentUri(String path, long id);

    public static void setIsDebug(boolean debug){
        SQLBuilder.Companion.setIsDebug(debug);
    }

}
