package jp.katahirado.android.tsubunomi.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;
import jp.katahirado.android.tsubunomi.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class RetweetTask extends AsyncTask<Twitter, Integer, twitter4j.Status> {
    private long statusId;
    private Activity activity;
    private ProgressDialog dialog;

    public RetweetTask(Activity activity, long id) {
        this.activity = activity;
        this.statusId = id;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.posting));
        dialog.show();
    }

    @Override
    protected twitter4j.Status doInBackground(Twitter... twitters) {
        twitter4j.Status resultStatus;
        try {
            resultStatus = twitters[0].retweetStatus(statusId);
        } catch (TwitterException e) {
            resultStatus = null;
        }
        return resultStatus;
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        dialog.dismiss();
        String statusMessage;
        if (status != null) {
            statusMessage = "リツイートしました";
        } else {
            statusMessage = "リツイート失敗";
        }
        Toast.makeText(activity, statusMessage, Toast.LENGTH_LONG).show();
    }
}
