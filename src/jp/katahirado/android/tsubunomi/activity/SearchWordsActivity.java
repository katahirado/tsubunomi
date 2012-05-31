package jp.katahirado.android.tsubunomi.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import jp.katahirado.android.tsubunomi.DBOpenHelper;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SearchWordDao;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchWordsActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_words);

        SearchWordDao searchWordDao = new SearchWordDao(new DBOpenHelper(this).getReadableDatabase());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}