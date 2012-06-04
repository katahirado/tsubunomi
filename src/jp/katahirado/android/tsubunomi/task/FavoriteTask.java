package jp.katahirado.android.tsubunomi.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;
import jp.katahirado.android.tsubunomi.R;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class FavoriteTask extends AsyncTask<Twitter, Integer, Status> {
    private Activity activity;
    private long statusId;
    private ProgressDialog dialog;

    public FavoriteTask(Activity activity, long id) {
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
    protected twitter4j.Status doInBackground(Twitter... twitter) {
        twitter4j.Status favStatus;
        try {
            favStatus = twitter[0].createFavorite(statusId);
        } catch (TwitterException e) {
            favStatus = null;
        }
        return favStatus;
    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        dialog.dismiss();
        String statusMessage;
        if (status != null) {
            statusMessage = "お気に入りに登録しました";
        } else {
            statusMessage = "お気に入りの登録に失敗しました";
        }
        Toast.makeText(activity, statusMessage, Toast.LENGTH_LONG).show();
    }
}
