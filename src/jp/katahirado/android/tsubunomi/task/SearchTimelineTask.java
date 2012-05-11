package jp.katahirado.android.tsubunomi.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import jp.katahirado.android.tsubunomi.SearchListAdapter;
import jp.katahirado.android.tsubunomi.TweetManager;
import jp.katahirado.android.tsubunomi.activity.SearchTimelineActivity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.TwitterException;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchTimelineTask extends AsyncTask<Query, Integer, SearchListAdapter> {

    private TweetManager tweetManager;
    private SearchListAdapter searchListAdapter;
    private SearchTimelineActivity searchActivity;
    private ProgressDialog dialog;
    private String queryString;

    public SearchTimelineTask(SearchTimelineActivity activity, TweetManager manager, SearchListAdapter adapter) {
        searchActivity = activity;
        searchListAdapter = adapter;
        tweetManager = manager;
    }


    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(searchActivity);
        dialog.setMessage("取得中");
        dialog.show();
    }

    @Override
    protected SearchListAdapter doInBackground(Query... query) {
        QueryResult queryResult = null;
        try {
            queryResult = tweetManager.connectTwitter().search(query[0]);
            queryString = query[0].getQuery();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if (queryResult.getTweets() != null) {
            for (Tweet tweet : queryResult.getTweets()) {
                searchListAdapter.add(tweet);
            }
        }
        return searchListAdapter;
    }

    @Override
    protected void onPostExecute(SearchListAdapter adapter) {
        dialog.dismiss();
        searchActivity.setSearchListAdapter(adapter,queryString);
    }
}
