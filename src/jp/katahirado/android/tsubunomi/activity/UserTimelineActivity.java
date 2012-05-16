package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import jp.katahirado.android.tsubunomi.*;
import jp.katahirado.android.tsubunomi.task.UserTimelineTask;
import twitter4j.Status;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UserTimelineActivity extends Activity implements View.OnClickListener {
    private AutoCompleteTextView screenNameText;
    private ListView listView;
    private TweetManager tweetManager;
    private InputFilter[] inputFilters = {new InnerFilter()};
    private ArrayAdapter<String> adapter;
    private ArrayList<String> screenNames;
    private SharedManager sharedManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usertimeline);


        setTitle(getString(R.string.app_name) + " : User");
        listView = (ListView) findViewById(R.id.tweet_list);
        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);
        screenNames = sharedManager.getScreenNames();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, screenNames);

        screenNameText = (AutoCompleteTextView) findViewById(R.id.screen_name_text);
        Button uSearchButton = (Button) findViewById(R.id.u_search_button);
        uSearchButton.setOnClickListener(this);
        screenNameText.setAdapter(adapter);
        screenNameText.setFilters(inputFilters);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.u_search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) screenNameText.getText();
                String query = builder.toString();
                if (query.length() == 0) {
                    return;
                }
                if (adapter.getPosition(query) == -1) {
                    adapter.add(query);
                    screenNames.add(query);
                    sharedManager.setScreenNames(screenNames);
                }
                ArrayList<Status> tweetList = new ArrayList<Status>();
                TweetListAdapter tweetListAdapter = new TweetListAdapter(this, tweetList);
                UserTimelineTask task = new UserTimelineTask(this, tweetManager, tweetListAdapter);
                task.execute(query);
                break;
        }
    }

    public void setTimelineListAdapter(TweetListAdapter adapter, String name) {
        listView.setAdapter(adapter);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(screenNameText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        screenNameText.setText("");
        setTitle(getString(R.string.app_name) + " : User : " + name);
    }

    private class InnerFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            if (charSequence.toString().matches("^[a-zA-Z0-9_]+$")) {
                return charSequence;
            } else {
                return "";
            }
        }
    }
}