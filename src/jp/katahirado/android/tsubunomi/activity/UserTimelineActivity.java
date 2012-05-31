package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import jp.katahirado.android.tsubunomi.*;
import jp.katahirado.android.tsubunomi.adapter.TweetListAdapter;
import jp.katahirado.android.tsubunomi.dialog.StatusDialog;
import jp.katahirado.android.tsubunomi.task.UserTimelineTask;
import twitter4j.Status;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UserTimelineActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private AutoCompleteTextView screenNameText;
    private ListView listView;
    private TweetManager tweetManager;
    private InputFilter[] inputFilters = {new InnerFilter()};
    private ArrayAdapter<String> nameAdapter;
    private ArrayList<String> screenNames;
    private ArrayList<String> doubleScreenNames;
    private SharedManager sharedManager;
    private ArrayList<Status> tweetList;
    private String query = "";
    private Intent intent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usertimeline);

        setTitle(getString(R.string.app_name) + " : User");
        listView = (ListView) findViewById(R.id.tweet_list);
        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);
        screenNames = sharedManager.getScreenNames();
        doubleScreenNames = sharedManager.getScreenNames();
        nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, screenNames);

        screenNameText = (AutoCompleteTextView) findViewById(R.id.screen_name_text);
        Button uSearchButton = (Button) findViewById(R.id.u_search_button);
        uSearchButton.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideIME();
                return false;
            }
        });
        screenNameText.setAdapter(nameAdapter);
        screenNameText.setFilters(inputFilters);
        intent = getIntent();
        String receiveName = intent.getStringExtra(Const.SCREEN_NAME);
        if (receiveName != null) {
            query = receiveName;
            getUserTimelineTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenNames = sharedManager.getScreenNames();
        doubleScreenNames = sharedManager.getScreenNames();
        nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, screenNames);
        screenNameText.setAdapter(nameAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Status status = tweetList.get(position);
        new StatusDialog(this, sharedManager, tweetManager, status).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.u_search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) screenNameText.getText();
                query = builder.toString();
                getUserTimelineTask();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_timeline_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_user_timeline_refresh:
                getUserTimelineTask();
                break;
            case R.id.menu_user_timeline_users:
                intent = new Intent(this, UsersActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_screen_name_manage:
                intent = new Intent(this, ScreenNamesManageActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserTimelineTask() {
        if (query.length() == 0) {
            return;
        }
        if (nameAdapter.getPosition(query) == -1) {
            nameAdapter.add(query);
            doubleScreenNames.add(query);
            sharedManager.setScreenNames(doubleScreenNames);
        }
        tweetList = new ArrayList<Status>();
        TweetListAdapter tweetListAdapter = new TweetListAdapter(this, tweetList);
        UserTimelineTask task = new UserTimelineTask(this, tweetManager, tweetListAdapter);
        task.execute(query);
    }

    public void setTimelineListAdapter(TweetListAdapter adapter, String name) {
        listView.setAdapter(adapter);
        hideIME();
        screenNameText.setText("");
        listView.requestFocus();
        setTitle(getString(R.string.app_name) + " : User : " + name);
    }

    private void hideIME() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(screenNameText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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