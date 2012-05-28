package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SharedManager;
import jp.katahirado.android.tsubunomi.TweetManager;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created with IntelliJ IDEA.
 * User: yuichi_katahira
 */
public class SendDMActivity extends Activity {
    private EditText dmText;
    private Button dmButton;
    private String screenName;
    private Twitter twitter;
    private TextView dmTweetCount;
    private TweetManager tweetManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.senddm);

        SharedManager sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);

        dmTweetCount = (TextView) findViewById(R.id.dmTweetCount);
        dmText = (EditText) findViewById(R.id.dmText);
        dmButton = (Button) findViewById(R.id.dmButton);

        dmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDM();
            }
        });

        dmText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int diffCount = tweetManager.calculateShortURLsLength(editable.toString());
                int tCount = Const.TWEET_COUNT_DEFAULT + diffCount - editable.length();
                dmTweetCount.setText(String.valueOf(tCount));
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(dmText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        Intent intent;
        if (sharedManager.isConnected()) {
            twitter = tweetManager.connectTwitter();
            dmButton.setEnabled(true);
        } else {
            dmButton.setEnabled(false);
            //Twitter連携設定が保持されていなかったら連携設定に飛ばす
            intent = new Intent(this, OAuthActivity.class);
            startActivity(intent);
        }
        intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                screenName = uri.getLastPathSegment();
                setDMTitle();
            }
        } else {
            String name = intent.getStringExtra(Const.SCREEN_NAME);
            if (name != null) {
                screenName = name;
                setDMTitle();
            }
        }
    }

    private void setDMTitle() {
        setTitle(getString(R.string.app_name) + " : " + screenName + "にダイレクトメッセージ");
    }

    private void sendDM() {
        SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) dmText.getText();
        String dmString = spannableStringBuilder.toString();
        try {
            twitter.sendDirectMessage(screenName, dmString);
            Toast.makeText(this, "送信しました", Toast.LENGTH_LONG).show();
        } catch (TwitterException e) {
            e.printStackTrace();
        } finally {
            screenName = null;
            dmText.setText("");
            setTitle(R.string.app_name);
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(dmButton.getWindowToken(), 0);
        }
    }
}