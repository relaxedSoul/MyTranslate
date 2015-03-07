package com.melcore.mytranslate.cache;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.melcore.mytranslate.model.WordPair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cache for collecting translated words with their translation
 * Created by Melcore on 02.03.2015.
 */
public abstract class CacheUtils {

    private static DatabaseHelper sInstance = null;

    public static DatabaseHelper getInstance(Context context) {
        if (context != null && sInstance == null) {
            sInstance = OpenHelperManager.getHelper(context.getApplicationContext(),
                    DatabaseHelper.class);
        }
        return sInstance;
    }

    public static void releaseInstance() {
        sInstance = null;
        OpenHelperManager.releaseHelper();
    }

    /**
     * We now expect only ru-en translation. So no need for any parameters for saving or loading data
     *
     * @param context - context of app (activity or service or applicationContext)
     * @return stored array of wordPairs.
     */
    public static List<WordPair> loadWordPairs(Context context) {
        try {
            return context == null ? new ArrayList<WordPair>() : getInstance(context).getWordPairDao().queryBuilder().orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
                && getInstance(context).getWordPairDao().queryForEq("origin", pair.getOrigin()).isEmpty()
                && getInstance(context).getWordPairDao().create(pair) == 1;
    }
}
