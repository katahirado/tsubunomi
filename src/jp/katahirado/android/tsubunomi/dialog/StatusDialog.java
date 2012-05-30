package jp.katahirado.android.tsubunomi.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import jp.katahirado.android.tsubunomi.SharedManager;
import jp.katahirado.android.tsubunomi.TweetManager;
import twitter4j.Status;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class StatusDialog extends MenuDialog {
    private Status status;

    public StatusDialog(Activity activity, SharedManager shared, TweetManager manager, Status status) {
        super(activity, shared, manager);
        this.status = status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIncludeEntitiesMenu(status.getUser().getScreenName(),status);
        menuList.setAdapter(new ArrayAdapter<String>(activity.getApplicationContext(),
                android.R.layout.simple_list_item_1, menuItems));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case REPLY:
                String screenName = tweetManager.buildReplyMention(status);
                replyToStartActivity(status.getId(), screenName, status.getText());
                break;
            case RETWEET:
                publicReTweet(status.getId());
                break;
            case SEND_DM:
                DMtoActivity(status.getUser().getScreenName());
                break;
            default:
                entityAction(position);
                break;
        }
        dismiss();
    }
}
