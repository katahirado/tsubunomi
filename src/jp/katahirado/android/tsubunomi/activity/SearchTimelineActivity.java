package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import jp.katahirado.android.tsubunomi.*;
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
    private ArrayAdapter<String> adapter;
    private ArrayList<Tweet> tweetList;
    private SharedManager sharedManager;
    private String query = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtimeline);

        setTitle(getString(R.string.app_name) + " : Search");
        listView = (ListView) findViewById(R.id.search_list);
        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);

        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        searchText.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Tweet tweet = tweetList.get(position);
        new TweetDialog(this, tweet).show();
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSearchTimelineTask() {
        if (query.length() == 0) {
            return;
        }
        adapter.add(query);
        tweetList = new ArrayList<Tweet>();
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
        listView.setAdapter(adapter);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        searchText.setText("");
        setTitle(getString(R.string.app_name) + " : Search : " + q);
    }

}