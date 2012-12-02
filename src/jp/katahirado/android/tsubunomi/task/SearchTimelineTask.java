package jp.katahirado.android.tsubunomi.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.adapter.SearchListAdapter;
import jp.katahirado.android.tsubunomi.TweetManager;
import jp.katahirado.android.tsubunomi.activity.SearchTimelineActivity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

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
        dialog.setMessage(searchActivity.getString(R.string.loading));
        dialog.show();
    }

    @Override
    protected SearchListAdapter doInBackground(Query... query) {
        List<twitter4j.Status> status = null;
        try {
            QueryResult queryResult = tweetManager.connectTwitter().search(query[0]);
            queryString = query[0].getQuery();
            status = queryResult.getTweets();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if (status != null) {
            for (twitter4j.Status tweet : status) {
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
