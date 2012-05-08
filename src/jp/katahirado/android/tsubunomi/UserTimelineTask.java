package jp.katahirado.android.tsubunomi;

import android.os.AsyncTask;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UserTimelineTask extends AsyncTask<String,Integer,TweetListAdapter>{

    public UserTimelineTask(UserTimelineActivity activity, TweetListAdapter adapter) {
    }

    @Override
    protected TweetListAdapter doInBackground(String... queryString) {
        return null;
    }
}
