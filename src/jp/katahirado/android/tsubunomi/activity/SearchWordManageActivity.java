package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.katahirado.android.tsubunomi.DBOpenHelper;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SearchWordDao;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchWordManageActivity extends Activity {
    private ListView listView;
    private SearchWordDao searchWordDao;
    private ArrayList<String> wordList;
    private ArrayAdapter<String> wordAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_word_manage);
        setTitle(getString(R.string.app_name) + " : Delete search word");

        listView = (ListView) findViewById(R.id.search_word_manage_list);
        DBOpenHelper dbHelper = new DBOpenHelper(this);
        searchWordDao = new SearchWordDao(dbHelper.getWritableDatabase());
        wordList = searchWordDao.all();
        wordAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, wordList);
    }
}