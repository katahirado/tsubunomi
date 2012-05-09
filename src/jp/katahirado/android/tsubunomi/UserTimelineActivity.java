package jp.katahirado.android.tsubunomi;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import twitter4j.Status;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UserTimelineActivity extends Activity implements View.OnClickListener {
    private EditText screenNameText;
    private ListView listView;
    private SharedManager sharedManager;
    private TweetManager tweetManager;
    private Button searchButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usertimeline);


        listView = (ListView) findViewById(R.id.tweet_list);
        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);

        screenNameText = (EditText) findViewById(R.id.screen_name_text);
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) screenNameText.getText();
                String query = builder.toString();

                ArrayList<Status> tweetList = new ArrayList<Status>();
                TweetListAdapter tweetListAdapter = new TweetListAdapter(this, tweetList);
                UserTimelineTask task = new UserTimelineTask(this,tweetManager,tweetListAdapter);
                task.execute(query);
                break;
        }
    }

    public void setTimelineListAdapter(TweetListAdapter adapter){
        listView.setAdapter(adapter);
        screenNameText.setText("");
    }

}