package jp.katahirado.android.tsubunomi;

import com.twitter.Extractor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class TweetTextCalculator {

    private Extractor extractor;
    private SharedManager sharedManager;

    public TweetTextCalculator(SharedManager manager) {
        extractor = new Extractor();
        sharedManager = manager;
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
