package jp.katahirado.android.tsubunomi.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.dao.DBOpenHelper;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.dao.SearchWordDao;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchWordsActivity extends ListActivity {
    private ArrayAdapter<String> adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_words);
        setTitle(getString(R.string.app_name)+" : 検索履歴一覧");

        SearchWordDao searchWordDao = new SearchWordDao(new DBOpenHelper(this).getReadableDatabase());
        ArrayList<String> searchWordList = searchWordDao.all();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchWordList);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, SearchTimelineActivity.class);
        intent.putExtra(Const.HASH, adapter.getItem(position));
        startActivity(intent);
    }
}