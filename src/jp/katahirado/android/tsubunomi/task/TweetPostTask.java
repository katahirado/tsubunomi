package jp.katahirado.android.tsubunomi.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.activity.TsubunomiActivity;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class TweetPostTask extends AsyncTask<StatusUpdate, Void, Boolean> {

    private TsubunomiActivity mActivity;
    private Twitter mTwitter;
    private ProgressDialog progressDialog;

    public TweetPostTask(TsubunomiActivity activity, Twitter twitter) {
        mActivity = activity;
        mTwitter = twitter;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(mActivity.getString(R.string.posting));
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(StatusUpdate... statusUpdates) {
        Boolean succeed;
        try {
            mTwitter.updateStatus(statusUpdates[0]);
            succeed = true;
        } catch (TwitterException e) {
            e.printStackTrace();
            succeed = false;
        }
        return succeed;
    }

    @Override
    protected void onPostExecute(Boolean succeed) {
        progressDialog.dismiss();
        if (succeed) {
            Toast.makeText(mActivity, "投稿しました", Toast.LENGTH_LONG).show();
        }
        mActivity.afterPostResetView();
    }
}
