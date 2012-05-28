package jp.katahirado.android.tsubunomi.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import jp.katahirado.android.tsubunomi.Const;
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
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
                replyToStartActivity(tweet.getId(), screenName, tweet.getText());
                break;
        }
    }
}
