package jp.katahirado.android.tsubunomi.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.activity.TsubunomiActivity;
import twitter4j.Tweet;
import twitter4j.UserMentionEntity;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class TweetDialog extends MenuDialog {
    private Tweet tweet;

    public TweetDialog(Activity activity, Tweet tweet) {
        super(activity);
        this.tweet = tweet;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position){
            case REPLY:
                String screenName;
                String fromUserName = tweet.getFromUser();
                screenName = fromUserName;
                String currentScreenName = sharedManager.getPrefString(Const.PREF_SCREEN_NAME, "");
                UserMentionEntity[] userMentions = tweet.getUserMentionEntities();
                for (UserMentionEntity userMention : userMentions) {
                    String mentionName = userMention.getScreenName();
                    if (!fromUserName.equals(mentionName) && !mentionName.equals(currentScreenName)) {
                        screenName = screenName + " @" + mentionName;
                    }
                }
                Intent intent = new Intent(activity, TsubunomiActivity.class);
                intent.putExtra(Const.IN_REPLY_TO_STATUS_ID, tweet.getId());
                intent.putExtra(Const.SCREEN_NAME, screenName);
                intent.putExtra(Const.MESSAGE, tweet.getText());
                activity.startActivity(intent);
                break;
        }
    }
}
