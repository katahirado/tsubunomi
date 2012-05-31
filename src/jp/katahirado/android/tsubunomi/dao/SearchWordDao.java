package jp.katahirado.android.tsubunomi.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchWordDao {
    private static final String TABLE_NAME = "search_words";
    private static final String COLUMN_WORD = "word";

    private SQLiteDatabase database;
    private static final String SELECT_ALL = "select " + COLUMN_WORD + " from " + TABLE_NAME
            + " ORDER BY " + COLUMN_WORD + ";";
    private static final String INSERT_WORD = "insert or replace into " + TABLE_NAME + " (word) values (?);";
    private static final String DELETE_WORD = "delete from " + TABLE_NAME + " where word = ?";

    public SearchWordDao(SQLiteDatabase database) {
        this.database = database;
    }

    public ArrayList<String> all() {
        ArrayList<String> resultList = new ArrayList<String>();
        Cursor cursor = database.rawQuery(SELECT_ALL, null);
        if (!cursor.moveToFirst()) {
            return resultList;
        }
        do {
            resultList.add(cursor.getString(cursor.getColumnIndex(COLUMN_WORD)));
        } while (cursor.moveToNext());
        cursor.close();
        return resultList;
    }

    public void insert(String query) {
        SQLiteStatement statement = database.compileStatement(INSERT_WORD);
        statement.bindString(1, query);
        statement.executeInsert();
    }

    public void delete(String word) {
        SQLiteStatement statement = database.compileStatement(DELETE_WORD);
        statement.bindString(1,word);
        statement.execute();
    }
}
