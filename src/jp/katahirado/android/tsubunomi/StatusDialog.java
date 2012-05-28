package jp.katahirado.android.tsubunomi;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import jp.katahirado.android.tsubunomi.activity.TsubunomiActivity;
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
                Intent intent = new Intent(activity, TsubunomiActivity.class);
                intent.putExtra(Const.IN_REPLY_TO_STATUS_ID, status.getId());
                intent.putExtra(Const.SCREEN_NAME, screenName);
                intent.putExtra(Const.MESSAGE, status.getText());
                activity.startActivity(intent);
                break;
        }
    }
}
