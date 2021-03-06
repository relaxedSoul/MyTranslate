package com.melcore.mytranslate.cache;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.melcore.mytranslate.model.WordPair;

import java.sql.SQLException;

/**
 * Cache for collecting translated words with their translation
 * Created by Melcore on 02.03.2015.
 */
public abstract class CacheUtils {

    private static DatabaseHelper sInstance = null;

    private static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = OpenHelperManager.getHelper(context.getApplicationContext(), DatabaseHelper.class);
        }
        return sInstance;
    }

    public static void releaseInstance() {
        sInstance = null;
        OpenHelperManager.releaseHelper();
    }

    /**
     * Saving incoming new pair. Will save new pair, if it's origin doesn't exist in database already
     *
     * @param context - context of app (activity or service or applicationContext)
     * @param pair    - WordPair instance for saving in Cache
     * @return result of saving. If object was saved - true, otherwise - false
     */
    public static boolean saveWordPair(Context context, WordPair pair) {
        return !(context == null || pair == null || TextUtils.isEmpty(pair.getOrigin()) || TextUtils.isEmpty(pair.getTranslate()))
                && getInstance(context).getWordPairDao().queryForEq(WordPair.ORIGIN, pair.getOrigin()).isEmpty()
                && getInstance(context).getWordPairDao().create(pair) == 1;
    }

    public static Cursor getDefaultCursor(Context context) {
        if (context == null) {
            return null;
        }
        return ((AndroidDatabaseResults) getInstance(context).getWordPairDao().iterator().getRawResults()).getRawCursor();
    }

    public static Cursor getCursorForFilter(Context context, String filter) {
        if (context != null) {
            try {
                if (TextUtils.isEmpty(filter)) {
                    return getDefaultCursor(context);
                } else {
                    QueryBuilder<WordPair, Integer> qb = getInstance(context).getWordPairDao().queryBuilder();
                    qb.where().like(WordPair.ORIGIN, "%" + filter + "%").or().like(WordPair.TRANSLATE, "%" + filter + "%");
                    AndroidDatabaseResults results = (AndroidDatabaseResults) qb.iterator().getRawResults();
                    Cursor result = results.getRawCursor();
                    return result;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
