package jp.katahirado.android.tsubunomi;

import com.twitter.Extractor;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class TweetManager {

    private Extractor extractor;
    private SharedManager sharedManager;

    public TweetManager(SharedManager manager) {
        extractor = new Extractor();
        sharedManager = manager;
    }

    public Twitter connectTwitter() {
        String oAuthAccessToken = sharedManager.getPrefString(Const.PREF_KEY_TOKEN, "");
        String oAuthAccessTokenSecret = sharedManager.getPrefString(Const.PREF_KEY_SECRET, "");
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setOAuthConsumerKey(Const.CONSUMER_KEY)
                .setOAuthConsumerSecret(Const.CONSUMER_SECRET)
                .setOAuthAccessToken(oAuthAccessToken)
                .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
                .setMediaProvider("TWITTER");
        return new TwitterFactory(configurationBuilder.build()).getInstance();
    }

    public int calculateShortURLsLength(String tText) {
        List<String> urls = extractor.extractURLs(tText);
        int diffCount = 0;
        for (String url : urls) {
            int shrinkLength;
            int checkLength;
            int urlLength = url.length();
            if (url.indexOf("http://") == 0) {
                checkLength = shrinkLength = sharedManager.getPrefInt(Const.SHORT_URL_LENGTH, 0);
            } else if (url.indexOf("https://") == 0) {
                checkLength = shrinkLength = sharedManager.getPrefInt(Const.SHORT_URL_LENGTH_HTTPS, 0);
            } else {
                //プロトコル無しurl
                shrinkLength = sharedManager.getPrefInt(Const.SHORT_URL_LENGTH, 0);
                checkLength = shrinkLength - 7;
            }

            if (urlLength >= checkLength) {
                diffCount += urlLength - shrinkLength;
            } else {
                diffCount -= shrinkLength - urlLength;
            }
        }
        return diffCount;
    }

    public String buildReplyMention(Status status) {
        String result;
        String tweetUserName = getTweetName(status);
        result = tweetUserName;
        String currentScreenName = sharedManager.getPrefString(Const.PREF_SCREEN_NAME, "");
        UserMentionEntity[] userMentions = status.getUserMentionEntities();
        for (UserMentionEntity userMention : userMentions) {
            String mentionName = userMention.getScreenName();
            if (!tweetUserName.equals(mentionName) && !mentionName.equals(currentScreenName)) {
                result = result + " @" + mentionName;
            }
        }
        return result;
    }

    public static long getTweetId(Status status){
        long tweetId;
        if(status.getRetweetedStatus()!=null){
            tweetId=status.getRetweetedStatus().getId();
        }else{
            tweetId=status.getId();
        }
        return tweetId;
    }

    public static String getTweetText(Status status){
        String tweetText;
        if(status.getRetweetedStatus()!=null){
            tweetText=status.getRetweetedStatus().getText();
        }else{
            tweetText=status.getText();
        }
        return tweetText;
    }

    public static String getTweetName(Status status){
        String tweetName;
        if(status.getRetweetedStatus()!=null){
            tweetName = status.getRetweetedStatus().getUser().getScreenName();
        }else{
            tweetName = status.getUser().getScreenName();
        }
        return tweetName;
    }

    public static Date getTweetDate(Status status){
        Date createDate;
        if(status.getRetweetedStatus()!=null){
            createDate = status.getRetweetedStatus().getCreatedAt();
        }else{
            createDate = status.getCreatedAt();
        }
        return createDate;
    }
}
