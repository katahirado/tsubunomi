package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.dao.DBOpenHelper;
import jp.katahirado.android.tsubunomi.dao.SearchWordDao;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchWordManageActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView listView;
    private SearchWordDao searchWordDao;
    private ArrayList<String> wordList;
    private ArrayAdapter<String> wordAdapter;
    private EditText searchWordText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_word_manage);
        setTitle(getString(R.string.app_name) + " : Delete search word");

        listView = (ListView) findViewById(R.id.search_word_manage_list);
        Button searchButton = (Button) findViewById(R.id.search_word_search_button);
        searchWordText = (EditText) findViewById(R.id.manage_search_word_text);

        searchWordDao = new SearchWordDao(new DBOpenHelper(this).getWritableDatabase());
        wordList = searchWordDao.all();
        wordAdapter = new ArrayAdapter<String>(this, R.layout.search_word_row, wordList);

        searchButton.setOnClickListener(this);
        listView.setAdapter(wordAdapter);
        listView.setOnItemClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_word_search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) searchWordText.getText();
                String query = builder.toString();
                wordAdapter =
                        new ArrayAdapter<String>(this, R.layout.search_word_row, wordListFilter(query));
                listView.setAdapter(wordAdapter);
                hideIME();
                searchWordText.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final String word = wordAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(word + " を削除してもよろしいですか?").setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                wordAdapter.remove(word);
                wordList.remove(word);
                searchWordDao.delete(word);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private ArrayList<String> wordListFilter(String query) {
        if (query.length() == 0) {
            return searchWordDao.all();
        }
        ArrayList<String> list = new ArrayList<String>();
        for (String s : wordList) {
            if (s.toLowerCase().contains(query.toLowerCase())) {
                list.add(s);
            }
        }
        return list;
    }

    private void hideIME() {
        InputMethodManager manager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchWordText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}