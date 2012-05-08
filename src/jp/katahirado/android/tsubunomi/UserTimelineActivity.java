package jp.katahirado.android.tsubunomi;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UserTimelineActivity extends Activity {
    private Button searchButton;
    private EditText screenNameText;
    private ListView tweetList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usertimeline);

        screenNameText = (EditText) findViewById(R.id.screen_name_text);
        searchButton = (Button) findViewById(R.id.search_button);
        tweetList = (ListView) findViewById(R.id.tweet_list);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpannableStringBuilder builder = (SpannableStringBuilder) screenNameText.getText();
                String query = builder.toString();
            }
        });
    }
}