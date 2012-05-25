package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import jp.katahirado.android.tsubunomi.*;
import jp.katahirado.android.tsubunomi.task.SearchTimelineTask;
import twitter4j.Query;
import twitter4j.Tweet;
import twitter4j.UserMentionEntity;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchTimelineActivity extends Activity implements View.OnClickListener {
    private AutoCompleteTextView searchText;
    private ListView listView;
    private TweetManager tweetManager;
    private ArrayAdapter<String> adapter;
    private ArrayList<Tweet> tweetList;
    private SharedManager sharedManager;

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
        searchText.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Tweet tweet = tweetList.get(position);
                String screenName = tweet.getFromUser();
                String currentScreenName = sharedManager.getPrefString(Const.PREF_SCREEN_NAME, "");
                UserMentionEntity[] userMentions = tweet.getUserMentionEntities();
                for (UserMentionEntity userMention : userMentions) {
                    String fromUserName = tweet.getFromUser();
                    String mentionName = userMention.getScreenName();
                    if (!fromUserName.equals(mentionName) && !mentionName.equals(currentScreenName)) {
                        screenName = screenName + " @" + mentionName;
                    }
                }
                Intent intent = new Intent(getApplicationContext(), TsubunomiActivity.class);
                intent.putExtra(Const.IN_REPLY_TO_STATUS_ID, tweet.getId());
                intent.putExtra(Const.SCREEN_NAME, screenName);
                intent.putExtra(Const.MESSAGE, tweet.getText());
                startActivity(intent);
            }
        });
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
                tweetList = new ArrayList<Tweet>();
                SearchListAdapter searchListAdapter = new SearchListAdapter(this, tweetList);
                SearchTimelineTask task = new SearchTimelineTask(this, tweetManager, searchListAdapter);
                task.execute(buildQuery(query));
                break;
        }
    }

    private Query buildQuery(String query) {
        Query q = new Query();
        String lang = "";
        String[] queryParams = query.split("&");
        if (queryParams.length > 1 && queryParams[1].startsWith("lang")) {
            lang = queryParams[1].split("=")[1];
        }
        q.setQuery(queryParams[0]);
        if (!lang.equals("")) {
            q.setLang(lang);
        }
        return q;
    }

    public void setSearchListAdapter(SearchListAdapter adapter, String query) {
        listView.setAdapter(adapter);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        searchText.setText("");
        setTitle(getString(R.string.app_name) + " : Search : " + query);
    }
}