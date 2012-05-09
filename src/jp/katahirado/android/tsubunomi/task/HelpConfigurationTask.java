package jp.katahirado.android.tsubunomi.task;

import android.os.AsyncTask;
import jp.katahirado.android.tsubunomi.SharedManager;
import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class HelpConfigurationTask extends AsyncTask<Twitter, Void, Void> {


    private SharedManager sharedManager;

    public HelpConfigurationTask(SharedManager manager) {
        sharedManager = manager;
    }

    @Override
    protected Void doInBackground(Twitter... twitters) {
        try {
            TwitterAPIConfiguration apiConfiguration = twitters[0].getAPIConfiguration();
            sharedManager.setTwitterAPIConfiguration(apiConfiguration);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
