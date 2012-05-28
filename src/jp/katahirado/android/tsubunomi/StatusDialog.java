package jp.katahirado.android.tsubunomi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.katahirado.android.tsubunomi.activity.TsubunomiActivity;
import twitter4j.Status;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class StatusDialog extends Dialog implements AdapterView.OnItemClickListener {
    private Activity activity;
    private Status status;
    private static final int REPLY = 0;
    private TweetManager tweetManager;
    private String[] menuItems;

    public StatusDialog(Activity activity, Status status) {
        super(activity);
        this.activity = activity;
        this.status = status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_dialog);

        SharedManager sharedManager = new SharedManager(activity.getSharedPreferences(Const.PREFERENCE_NAME,
                activity.MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);
        ListView menuList = (ListView) findViewById(R.id.status_dialog_list);
        menuItems = new String[]{activity.getString(R.string.reply), activity.getString(R.string.retweet)};
        menuList.setAdapter(new ArrayAdapter<String>(activity.getApplicationContext(),
                android.R.layout.simple_list_item_1, menuItems));
        menuList.setOnItemClickListener(this);
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
