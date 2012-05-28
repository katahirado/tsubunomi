package jp.katahirado.android.tsubunomi.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import twitter4j.Status;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class StatusDialog extends MenuDialog {
    private Status status;

    public StatusDialog(Activity activity, Status status) {
        super(activity);
        this.status = status;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case REPLY:
                String screenName = tweetManager.buildReplyMention(status);
                replyToStartActivity(status.getId(), screenName, status.getText());
                break;
        }
    }
}
