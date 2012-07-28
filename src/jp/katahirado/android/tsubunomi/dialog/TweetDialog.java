package jp.katahirado.android.tsubunomi.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.SharedManager;
import jp.katahirado.android.tsubunomi.TweetManager;
import twitter4j.Tweet;
import twitter4j.UserMentionEntity;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class TweetDialog extends MenuDialog {
    private Tweet tweet;

    public TweetDialog(Activity activity, SharedManager shared, TweetManager tweetManager, Tweet tweet) {
        super(activity, shared, tweetManager);
        this.tweet = tweet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIncludeEntitiesMenu(tweet.getFromUser(), tweet);
        menuList.setAdapter(new ArrayAdapter<String>(activity.getApplicationContext(),
                android.R.layout.simple_list_item_1, menuItems));
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
                replyToStartActivity(tweet.getId(), screenName, tweet.getText(), Const.REPLY);
                break;
            case QUOTE:
                replyToStartActivity(tweet.getId(), tweet.getFromUser(), tweet.getText(), Const.QT);
                break;
            case RETWEET:
                publicReTweet(tweet.getId());
                break;
            case FAVORITE:
                favorite(tweet.getId(),false);
                break;
            case SEND_DM:
                dmToActivity(tweet.getFromUserName());
                break;
            case CREATE_FRIENDSHIPS:
                createFriendships(tweet.getFromUserName());
                break;
            default:
                entityAction(position);
                break;
        }
        dismiss();
    }

}
