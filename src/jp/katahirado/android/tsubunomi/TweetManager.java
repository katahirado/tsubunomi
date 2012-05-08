package jp.katahirado.android.tsubunomi;

import com.twitter.Extractor;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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
}
