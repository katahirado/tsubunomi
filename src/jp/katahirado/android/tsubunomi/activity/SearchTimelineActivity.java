package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import jp.katahirado.android.tsubunomi.*;
import jp.katahirado.android.tsubunomi.task.SearchTimelineTask;
import twitter4j.Query;
import twitter4j.Tweet;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchTimelineActivity extends Activity implements View.OnClickListener {
    private AutoCompleteTextView searchText;
    private ListView listView;
    private SharedManager sharedManager;
    private TweetManager tweetManager;
    private Button searchButton;
    private ArrayAdapter<String> adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtimeline);


        listView = (ListView) findViewById(R.id.search_list);
        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);

        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        searchText.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) searchText.getText();
                String query = builder.toString();
                if (query.length() == 0) {
                    return;
                }
                adapter.add(query);
                ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
                SearchListAdapter searchListAdapter = new SearchListAdapter(this, tweetList);
                SearchTimelineTask task = new SearchTimelineTask(this, tweetManager, searchListAdapter);
                task.execute(new Query(query));
                break;
        }
    }

    public void setSearchListAdapter(SearchListAdapter adapter) {
        listView.setAdapter(adapter);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        searchText.setText("");
    }
}