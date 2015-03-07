package com.melcore.mytranslate.cache;

/**
 * DatabaseHelper for Cache. Manages work with DB.
 * Created by Melcore on 02.03.2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.melcore.mytranslate.R;
import com.melcore.mytranslate.model.WordPair;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "MyDictionary.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<WordPair, Integer> wordPairExceptionDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, WordPair.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, WordPair.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void clear(Class<T> clazz) {
        try {
            TableUtils.clearTable(connectionSource, clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RuntimeExceptionDao<WordPair, Integer> getWordPairDao() {
        if (wordPairExceptionDao == null) {
            wordPairExceptionDao = getRuntimeExceptionDao(WordPair.class);
        }
        return wordPairExceptionDao;
    }

    public void close() {
        super.close();
        wordPairExceptionDao = null;
    }
}
