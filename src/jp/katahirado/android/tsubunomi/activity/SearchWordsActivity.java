package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.dao.DBOpenHelper;
import jp.katahirado.android.tsubunomi.dao.SearchWordDao;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchWordsActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ArrayAdapter<String> adapter;
    private EditText searchedWordText;
    private ArrayList<String> searchedWordList;
    private ListView listView;
    private SearchWordDao searchWordDao;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_words);
        setTitle(getString(R.string.app_name) + " : 検索履歴一覧");

        listView = (ListView) findViewById(R.id.searched_word_list);
        searchedWordText = (EditText) findViewById(R.id.searched_word_text);
        Button button = (Button) findViewById(R.id.searched_word_search_button);

        searchWordDao = new SearchWordDao(new DBOpenHelper(this).getReadableDatabase());
        setListViewAdapter();
        listView.setOnItemClickListener(this);
        button.setOnClickListener(this);
        listView.requestFocus();
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideIME();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListViewAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_words_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_word_manage:
                startActivity(new Intent(this, SearchWordManageActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searched_word_search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) searchedWordText.getText();
                String query = builder.toString();
                adapter = new ArrayAdapter<String>(this, R.layout.search_word_row, wordListFilter(query));
                listView.setAdapter(adapter);
                hideIME();
                searchedWordText.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, SearchTimelineActivity.class);
        intent.putExtra(Const.HASH, adapter.getItem(position));
        startActivity(intent);
    }

    private void setListViewAdapter() {
        searchedWordList = searchWordDao.all();
        adapter = new ArrayAdapter<String>(this, R.layout.search_word_row, searchedWordList);
        listView.setAdapter(adapter);
    }

    private ArrayList<String> wordListFilter(String query) {
        if (query.length() == 0) {
            return searchedWordList;
        }
        ArrayList<String> list = new ArrayList<String>();
        for (String s : searchedWordList) {
            if (s.toLowerCase().contains(query.toLowerCase())) {
                list.add(s);
            }
        }
        return list;
    }

    private void hideIME() {
        InputMethodManager manager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchedWordText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}