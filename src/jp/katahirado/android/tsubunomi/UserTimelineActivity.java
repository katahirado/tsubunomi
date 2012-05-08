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
public class UserTimelineActivity extends Activity implements View.OnClickListener{
    private Button searchButton;
    private EditText screenNameText;
    private ListView listView;
    private ArrayList<Status> tweetList;
    private TweetListAdapter tweetListAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usertimeline);

        screenNameText = (EditText) findViewById(R.id.screen_name_text);
        searchButton = (Button) findViewById(R.id.search_button);
        listView = (ListView) findViewById(R.id.tweet_list);
        tweetList = new ArrayList<Status>();
        tweetListAdapter = new TweetListAdapter(this,tweetList);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) screenNameText.getText();
                String query = builder.toString();

                UserTimelineTask task = new UserTimelineTask(this,tweetListAdapter);
                task.execute(query);
                break;
        }
    }
}