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
import jp.katahirado.android.tsubunomi.SharedManager;
import jp.katahirado.android.tsubunomi.TweetManager;
import jp.katahirado.android.tsubunomi.adapter.SearchListAdapter;
import jp.katahirado.android.tsubunomi.dao.DBOpenHelper;
import jp.katahirado.android.tsubunomi.dao.SearchWordDao;
import jp.katahirado.android.tsubunomi.dialog.TweetDialog;
import jp.katahirado.android.tsubunomi.task.SearchTimelineTask;
import twitter4j.Query;
import twitter4j.Tweet;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchTimelineActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private AutoCompleteTextView searchText;
    private ListView listView;
    private TweetManager tweetManager;
    private ArrayAdapter<String> wordAdapter;
    private ArrayList<String> doubleWordList;
    private String query = "";
    private SharedManager sharedManager;
    private SearchWordDao searchWordDao;
    private SearchListAdapter searchListAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtimeline);

        setTitle(getString(R.string.app_name) + " : Search");
        listView = (ListView) findViewById(R.id.search_list);
        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        Button searchButton = (Button) findViewById(R.id.search_button);

        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);
        searchWordDao = new SearchWordDao(new DBOpenHelper(this).getWritableDatabase());
        setWordAdapter();

        searchButton.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideIME();
                return false;
            }
        });

        String receiveHash = getIntent().getStringExtra(Const.HASH);
        if (receiveHash != null) {
            query = receiveHash;
            getSearchTimelineTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWordAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Tweet tweet = searchListAdapter.getItem(position);
        new TweetDialog(this, sharedManager, tweetManager, tweet).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) searchText.getText();
                query = builder.toString();
                getSearchTimelineTask();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_timeline_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_timeline_refresh:
                getSearchTimelineTask();
                break;
            case R.id.menu_search_timeline_to_user_timeline:
                startActivity(new Intent(this, UserTimelineActivity.class));
                break;
            case R.id.menu_search_word_list:
                startActivity(new Intent(this, SearchWordsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setWordAdapter() {
        ArrayList<String> wordList = searchWordDao.all();
        doubleWordList = searchWordDao.all();
        wordAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, wordList);
        searchText.setAdapter(wordAdapter);
    }

    private void getSearchTimelineTask() {
        if (query.length() == 0) {
            return;
        }
        if (wordAdapter.getPosition(query) == -1) {
            wordAdapter.add(query);
            doubleWordList.add(query);
            searchWordDao.insert(query);
        }
        ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
        SearchListAdapter searchListAdapter = new SearchListAdapter(this, tweetList);
        SearchTimelineTask task = new SearchTimelineTask(this, tweetManager, searchListAdapter);
        task.execute(buildQuery(query));
    }

    private Query buildQuery(String s) {
        Query q = new Query();
        String lang = "";
        String[] queryParams = s.split("&");
        if (queryParams.length > 1 && queryParams[1].startsWith("lang")) {
            lang = queryParams[1].split("=")[1];
        }
        q.setQuery(queryParams[0]);
        if (!lang.equals("")) {
            q.setLang(lang);
        }
        return q;
    }

    public void setSearchListAdapter(SearchListAdapter adapter, String q) {
        searchListAdapter = adapter;
        listView.setAdapter(searchListAdapter);
        hideIME();
        searchText.setText("");
        listView.requestFocus();
        setTitle(getString(R.string.app_name) + " : Search : " + q);
    }

    private void hideIME() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}