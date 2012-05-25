package jp.katahirado.android.tsubunomi.task;

import android.os.AsyncTask;
import jp.katahirado.android.tsubunomi.SharedManager;
import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class ProfileAndHelpConfigurationTask extends AsyncTask<Twitter, Integer, Void> {


    private SharedManager sharedManager;

    public ProfileAndHelpConfigurationTask(SharedManager manager) {
        sharedManager = manager;
    }

    @Override
    protected Void doInBackground(Twitter... twitter) {
        try {
            TwitterAPIConfiguration apiConfiguration = twitter[0].getAPIConfiguration();
            sharedManager.setTwitterAPIConfiguration(apiConfiguration);
            User user = twitter[0].verifyCredentials();
            sharedManager.setCurrentUser(user);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
