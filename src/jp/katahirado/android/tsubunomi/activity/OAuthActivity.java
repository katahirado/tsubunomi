package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SharedManager;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: yuichi_katahira
 */
public class OAuthActivity extends Activity {

    private static Twitter twitter;
    private static RequestToken requestToken;
    private Button button;
    private SharedManager sharedManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth);
        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));

        button = (Button) findViewById(R.id.oauthButton);
        changeButtonState();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedManager.isConnected()) {
                    sharedManager.removeOAuth();
                    button.setText("Connect to Twitter");
                } else {
                    startOAuth();
                }
            }
        });

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Const.CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(Const.OAUTH_VERIFIER);
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                sharedManager.saveOAuth(accessToken);
                startActivity(new Intent(this, TsubunomiActivity.class));
            } catch (TwitterException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        changeButtonState();
    }

    private void changeButtonState() {
        if (sharedManager.isConnected()) {
            button.setText("Disconnect to Twitter");
        } else {
            button.setText("Connect to Twitter");
        }
    }

    private void startOAuth() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
        twitter = new TwitterFactory(configurationBuilder.build()).getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken(Const.CALLBACK_URL);
            Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG).show();
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL() + "&force_login=true")));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

}