package jp.katahirado.android.tsubunomi.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_SEARCH_WORDS_TABLE = "create table search_words (" +
            " _id integer primary key autoincrement, word text not null)";

    public DBOpenHelper(Context context) {
        super(context, "tsubunomi.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_SEARCH_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int position, int position1) {
    }
}

