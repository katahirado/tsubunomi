package jp.katahirado.android.tsubunomi.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SharedManager;
import jp.katahirado.android.tsubunomi.TweetManager;
import jp.katahirado.android.tsubunomi.activity.SearchTimelineActivity;
import jp.katahirado.android.tsubunomi.activity.SendDMActivity;
import jp.katahirado.android.tsubunomi.activity.TsubunomiActivity;
import jp.katahirado.android.tsubunomi.activity.UserTimelineActivity;
import twitter4j.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class MenuDialog extends Dialog implements AdapterView.OnItemClickListener {
    protected Activity activity;
    protected TweetManager tweetManager;
    protected String[] menuItems;
    protected static final int REPLY = 0;
    protected static final int RETWEET = 1;
    protected static final int SEND_DM = 2;
    protected SharedManager sharedManager;
    protected ListView menuList;
    private Intent intent;
    private Map<String, String> entitiesDictionary;

    public MenuDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_dialog);
        sharedManager = new SharedManager(activity.getSharedPreferences(Const.PREFERENCE_NAME,
                activity.MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);
        menuList = (ListView) findViewById(R.id.menu_dialog_list);
        menuList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }

    protected void replyToStartActivity(long id, String screenName, String message) {
        Intent intent = new Intent(activity, TsubunomiActivity.class);
        intent.putExtra(Const.IN_REPLY_TO_STATUS_ID, id);
        intent.putExtra(Const.SCREEN_NAME, screenName);
        intent.putExtra(Const.MESSAGE, message);
        activity.startActivity(intent);
    }

    protected void publicReTweet(final long id) {
        new AlertDialog.Builder(activity)
                .setMessage("公式リツイートしますか?")
                .setPositiveButton("Yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        Twitter twitter = tweetManager.connectTwitter();
                        Status resultStatus;
                        String statusMessage;
                        try {
                            resultStatus = twitter.retweetStatus(id);
                        } catch (TwitterException e) {
                            resultStatus = null;
                        }
                        if (resultStatus != null) {
                            statusMessage = "リツイートしました";
                        } else {
                            statusMessage = "リツイート失敗";
                        }
                        Toast.makeText(activity, statusMessage, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {

                    }
                }).create().show();
    }

    protected void DMtoActivity(String screenName) {
        intent = new Intent(activity, SendDMActivity.class);
        intent.putExtra(Const.SCREEN_NAME, screenName);
        activity.startActivity(intent);
    }

    protected void entityAction(int position) {
        String key = menuItems[position];
        String value = entitiesDictionary.get(key);
        if (key.startsWith("@")) {
            startUserTimelineActivity(value);
        } else if (key.startsWith("#")) {
            startSearchTimelineActivity(key);
        } else {
            startExternalBrowser(value);
        }
    }

    //hashはサーチに飛ばす
    private void startSearchTimelineActivity(String hashTag) {
        intent = new Intent(activity, SearchTimelineActivity.class);
        intent.putExtra(Const.HASH, hashTag);
        activity.startActivity(intent);
    }

    //mentionはuserに飛ばす
    private void startUserTimelineActivity(String screenName) {
        intent = new Intent(activity, UserTimelineActivity.class);
        intent.putExtra(Const.SCREEN_NAME, screenName);
        activity.startActivity(intent);
    }

    private void startExternalBrowser(String url) {
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    protected void getIncludeEntitiesMenu(EntitySupport status) {
        List<String> resultList = new ArrayList<String>();
        resultList.add(activity.getString(R.string.reply));
        resultList.add(activity.getString(R.string.retweet));
        resultList.add(activity.getString(R.string.send_dm));
        entitiesDictionary = new HashMap<String, String>();
        //各リストをDictionaryにセット
        MediaEntity[] mediaEntities = status.getMediaEntities();
        if (mediaEntities != null) {
            for (MediaEntity entity : mediaEntities) {
                entitiesDictionary.put(entity.getURL().toString(), entity.getExpandedURL().toString());
            }
        }
        HashtagEntity[] hashTagEntities = status.getHashtagEntities();
        if (hashTagEntities != null) {
            for (HashtagEntity entity : hashTagEntities) {
                entitiesDictionary.put("#" + entity.getText(), entity.getText());
            }
        }
        URLEntity[] urlEntities = status.getURLEntities();
        if (urlEntities != null) {
            for (URLEntity entity : urlEntities) {
                entitiesDictionary.put(entity.getURL().toString(), entity.getExpandedURL().toString());
            }
        }
        UserMentionEntity[] userMentionEntities = status.getUserMentionEntities();
        if (userMentionEntities != null) {
            for (UserMentionEntity entity : userMentionEntities) {
                entitiesDictionary.put("@" + entity.getScreenName(), entity.getScreenName());
            }
        }
        Iterator<String> keySetIterator = entitiesDictionary.keySet().iterator();
        if (keySetIterator != null) {
            while (keySetIterator.hasNext()) {
                resultList.add(keySetIterator.next());
            }
        }
        menuItems = resultList.toArray(new String[resultList.size()]);
    }
}
