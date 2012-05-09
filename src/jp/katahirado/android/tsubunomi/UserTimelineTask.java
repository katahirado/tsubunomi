package jp.katahirado.android.tsubunomi;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import twitter4j.TwitterException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UserTimelineTask extends AsyncTask<String, Integer, TweetListAdapter> {

    private UserTimelineActivity timelineActivity;
    private TweetListAdapter tweetListAdapter;
    private ProgressDialog dialog;
    private TweetManager tweetManager;

    public UserTimelineTask(UserTimelineActivity activity, TweetManager manager, TweetListAdapter adapter) {
        timelineActivity = activity;
        tweetListAdapter = adapter;
        tweetManager = manager;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(timelineActivity);
        dialog.setMessage("取得中");
        dialog.show();
    }

    @Override
    protected TweetListAdapter doInBackground(String... queryString) {
        List<twitter4j.Status> statuses = null;
        try {
            statuses = tweetManager.connectTwitter().getUserTimeline(queryString[0]);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        for (twitter4j.Status status : statuses) {
            tweetListAdapter.add(status);
        }
        return tweetListAdapter;
    }

    @Override
    protected void onPostExecute(TweetListAdapter adapter) {
        dialog.dismiss();
        timelineActivity.setTimelineListAdapter(adapter);
    }
}
