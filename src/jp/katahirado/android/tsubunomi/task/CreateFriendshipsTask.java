package jp.katahirado.android.tsubunomi.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;
import jp.katahirado.android.tsubunomi.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class CreateFriendshipsTask extends AsyncTask<Twitter, Integer, User> {
    private Activity activity;
    private String screenName;
    private ProgressDialog dialog;

    public CreateFriendshipsTask(Activity activity, String screenName) {
        this.activity = activity;
        this.screenName = screenName;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.following));
        dialog.show();
    }

    @Override
    protected User doInBackground(Twitter... twitters) {
        User resultUser;
        try {
            resultUser = twitters[0].createFriendship(screenName);
        } catch (TwitterException e) {
            resultUser = null;
        }
        return resultUser;
    }

    @Override
    protected void onPostExecute(User user) {
        dialog.dismiss();
        String resultString;
        if (user != null) {
            resultString = "フォローしました";
        } else {
            resultString = "フォロー失敗";
        }
        Toast.makeText(activity, resultString, Toast.LENGTH_LONG).show();
    }
}
